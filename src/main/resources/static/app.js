const ADMIN_NAME = '소낙눈';

const state = {
    name: '',
    themes: [],
    reservations: [],
    selectedTimeId: null,
};

const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => Array.from(document.querySelectorAll(selector));

const loginScreen = $('#login-screen');
const userScreen = $('#user-screen');
const adminScreen = $('#admin-screen');

document.addEventListener('DOMContentLoaded', async () => {
    setDefaultDate($('#reservation-date'));
    bindEvents();
    const savedName = sessionStorage.getItem('roomescapeUserName');
    if (savedName) {
        $('#login-name').value = savedName;
        await loginWithName(savedName);
    }
});

function bindEvents() {
    $('#login-form').addEventListener('submit', handleLogin);
    $('#reservation-date').addEventListener('change', loadAvailableTimes);
    $('#previous-date-button').addEventListener('click', () => shiftReservationDate(-1));
    $('#next-date-button').addEventListener('click', () => shiftReservationDate(1));
    $('#today-date-button').addEventListener('click', setReservationDateToday);
    $('#theme-select').addEventListener('change', loadAvailableTimes);
    $('#create-reservation-button').addEventListener('click', createSelectedReservation);
    $('#time-form').addEventListener('submit', handleCreateTime);
    $('#theme-form').addEventListener('submit', handleCreateTheme);

    $$('.logout-button').forEach((button) => button.addEventListener('click', logout));
    $$('.tab-button').forEach((button) => button.addEventListener('click', () => activateAdminTab(button.dataset.adminTab)));
}

async function handleLogin(event) {
    event.preventDefault();
    const name = $('#login-name').value.trim();
    if (!name) {
        return;
    }

    await loginWithName(name);
}

async function loginWithName(name) {
    state.name = name;
    sessionStorage.setItem('roomescapeUserName', name);
    $$('.current-user').forEach((element) => element.textContent = name);
    loginScreen.classList.add('hidden');

    if (name === ADMIN_NAME) {
        adminScreen.classList.remove('hidden');
        await refreshAdmin();
        return;
    }

    userScreen.classList.remove('hidden');
    await refreshUser();
    applyRedirectState();
}

function logout() {
    state.name = '';
    state.themes = [];
    state.reservations = [];
    state.selectedTimeId = null;
    sessionStorage.removeItem('roomescapeUserName');
    sessionStorage.removeItem('roomescapeFlash');
    $('#login-name').value = '';
    userScreen.classList.add('hidden');
    adminScreen.classList.add('hidden');
    loginScreen.classList.remove('hidden');
    hideAlert('user');
    hideAlert('admin');
}

async function refreshUser() {
    try {
        const [themes, popularThemes, reservations] = await Promise.all([
            api('/themes'),
            api('/themes/popular'),
            api(`/reservations?name=${encodeURIComponent(state.name)}`),
        ]);

        state.themes = themes;
        state.reservations = reservations;
        renderThemeOptions(themes);
        renderPopularThemes(popularThemes);
        renderMyReservations(reservations);
        await loadAvailableTimes();
    } catch (error) {
        showAlert('user', error.message, 'error');
    }
}

async function refreshAdmin() {
    try {
        const [reservations, times, themes] = await Promise.all([
            api('/admin/reservations'),
            api('/admin/times'),
            api('/themes'),
        ]);

        renderAdminReservations(reservations);
        renderAdminTimes(times);
        renderAdminThemes(themes);
    } catch (error) {
        showAlert('admin', error.message, 'error');
    }
}

function renderThemeOptions(themes) {
    const select = $('#theme-select');
    select.innerHTML = '';

    if (themes.length === 0) {
        select.innerHTML = '<option value="">등록된 테마가 없습니다</option>';
        return;
    }

    themes.forEach((theme) => {
        const option = document.createElement('option');
        option.value = theme.id;
        option.textContent = theme.name;
        select.appendChild(option);
    });
}

function renderPopularThemes(themes) {
    const container = $('#popular-themes');
    if (themes.length === 0) {
        container.innerHTML = '<div class="empty-message">최근 1주일 예약된 테마가 없습니다.</div>';
        return;
    }

    container.innerHTML = themes.map(themeCard).join('');
}

function renderMyReservations(reservations) {
    const container = $('#my-reservations');
    if (reservations.length === 0) {
        container.innerHTML = '<div class="empty-message">아직 예약이 없습니다.</div>';
        return;
    }

    container.innerHTML = reservations.map((reservation) => `
        <article class="reservation-card" data-reservation-id="${reservation.id}">
            <div class="reservation-main">
                <div>
                    <strong>${escapeHtml(reservation.theme.name)}</strong>
                    <div class="meta">${reservation.date} ${formatTime(reservation.time.startAt)} · ${escapeHtml(reservation.name)}</div>
                </div>
                <div class="actions">
                    <button class="ghost-button small-button" type="button" data-action="edit">변경</button>
                    <button class="danger-button small-button" type="button" data-action="cancel">취소</button>
                </div>
            </div>
            <div class="edit-panel hidden">
                <label>
                    변경할 날짜
                    <input type="date" class="edit-date" value="${reservation.date}">
                </label>
                <label>
                    변경할 시간
                    <select class="edit-time"></select>
                </label>
                <button class="small-button" type="button" data-action="save-edit">변경 완료</button>
                <p class="edit-help">날짜를 바꾸면 해당 날짜에 예약 가능한 시간 목록이 다시 표시됩니다.</p>
            </div>
        </article>
    `).join('');

    container.querySelectorAll('[data-action="edit"]').forEach((button) => {
        button.addEventListener('click', () => openEditPanel(button.closest('.reservation-card')));
    });
    container.querySelectorAll('[data-action="cancel"]').forEach((button) => {
        button.addEventListener('click', () => cancelReservation(button.closest('.reservation-card').dataset.reservationId));
    });
    container.querySelectorAll('[data-action="save-edit"]').forEach((button) => {
        button.addEventListener('click', () => updateReservation(button.closest('.reservation-card')));
    });
    container.querySelectorAll('.edit-date').forEach((input) => {
        input.min = todayString();
        input.addEventListener('input', () => populateEditTimes(input.closest('.reservation-card')));
        input.addEventListener('change', () => populateEditTimes(input.closest('.reservation-card')));
    });
}

async function loadAvailableTimes() {
    const themeId = $('#theme-select').value;
    const date = $('#reservation-date').value;
    const container = $('#available-times');
    clearSelectedTime();

    if (!themeId || !date) {
        container.className = 'time-grid empty';
        container.textContent = '날짜와 테마를 선택해 주세요.';
        return;
    }

    try {
        const times = await api(`/times?themeId=${themeId}&date=${date}`);
        if (times.length === 0) {
            container.className = 'time-grid empty';
            container.textContent = '등록된 예약 시간이 없습니다.';
            return;
        }

        container.className = 'time-grid';
        container.innerHTML = times.map((time) => `
            <button class="time-button ${time.booked ? 'booked' : ''}" type="button" data-time-id="${time.id}" ${time.booked ? 'disabled' : ''}>
                ${formatTime(time.startAt)}
            </button>
        `).join('');
        container.querySelectorAll('.time-button:not(:disabled)').forEach((button) => {
            button.addEventListener('click', () => selectReservationTime(button));
        });
    } catch (error) {
        showAlert('user', error.message, 'error');
    }
}

function selectReservationTime(button) {
    state.selectedTimeId = button.dataset.timeId;
    $$('.time-button').forEach((timeButton) => timeButton.classList.remove('selected'));
    button.classList.add('selected');
    $('#selected-time-message').textContent = `${button.textContent.trim()} 시간으로 예약을 신청할 수 있습니다.`;
    $('#create-reservation-button').disabled = false;
}

function clearSelectedTime() {
    state.selectedTimeId = null;
    $('#selected-time-message').textContent = '예약할 시간을 선택해 주세요.';
    $('#create-reservation-button').disabled = true;
}

async function shiftReservationDate(days) {
    const input = $('#reservation-date');
    const nextDate = addDays(input.value || todayString(), days);
    input.value = nextDate < todayString() ? todayString() : nextDate;
    await loadAvailableTimes();
}

async function setReservationDateToday() {
    $('#reservation-date').value = todayString();
    await loadAvailableTimes();
}

async function createSelectedReservation() {
    if (!state.selectedTimeId) {
        showAlert('user', '예약할 시간을 선택해 주세요.', 'error');
        return;
    }

    try {
        await api('/reservations', {
            method: 'POST',
            body: {
                name: state.name,
                date: $('#reservation-date').value,
                time_id: Number(state.selectedTimeId),
                theme_id: Number($('#theme-select').value),
            },
        });
        showAlert('user', '예약이 생성되었습니다.', 'success');
        await refreshUser();
    } catch (error) {
        showAlert('user', error.message, 'error');
    }
}

async function openEditPanel(card) {
    const reservation = state.reservations.find((item) => String(item.id) === String(card.dataset.reservationId));
    card.querySelector('.edit-panel').classList.toggle('hidden');
    if (reservation) {
        await populateEditTimes(card, reservation.time.id);
    }
}

async function populateEditTimes(card, selectedTimeId) {
    const reservation = state.reservations.find((item) => String(item.id) === String(card.dataset.reservationId));
    if (!reservation) {
        return;
    }

    const date = card.querySelector('.edit-date').value;
    const select = card.querySelector('.edit-time');
    select.innerHTML = '<option>불러오는 중...</option>';

    try {
        const times = await api(`/times?themeId=${reservation.theme.id}&date=${date}`);
        const selectableTimes = times.filter((time) => !time.booked || time.id === selectedTimeId);
        if (selectableTimes.length === 0) {
            select.innerHTML = '<option value="">선택 가능한 시간이 없습니다</option>';
            return;
        }

        select.innerHTML = selectableTimes.map((time) => `
            <option value="${time.id}" ${time.id === selectedTimeId ? 'selected' : ''}>${formatTime(time.startAt)}</option>
        `).join('');
    } catch (error) {
        select.innerHTML = '<option value="">시간 조회 실패</option>';
        showAlert('user', error.message, 'error');
    }
}

async function updateReservation(card) {
    const timeId = card.querySelector('.edit-time').value;
    if (!timeId) {
        showAlert('user', '변경할 시간을 선택해 주세요.', 'error');
        return;
    }

    try {
        await api(`/reservations/${card.dataset.reservationId}`, {
            method: 'PATCH',
            body: {
                name: state.name,
                date: card.querySelector('.edit-date').value,
                time_id: Number(timeId),
            },
        });
        await showCompletionModal();
        sessionStorage.setItem('roomescapeFlash', '변경된 예약 내역을 반영했습니다.');
        window.location.href = `${window.location.pathname}?updated=${Date.now()}#my-reservations`;
    } catch (error) {
        showAlert('user', error.message, 'error');
    }
}

async function cancelReservation(id) {
    try {
        await api(`/reservations/${id}?name=${encodeURIComponent(state.name)}`, { method: 'DELETE' });
        showAlert('user', '예약이 취소되었습니다.', 'success');
        await refreshUser();
    } catch (error) {
        showAlert('user', error.message, 'error');
    }
}

function renderAdminReservations(reservations) {
    const container = $('#admin-reservation-list');
    if (reservations.length === 0) {
        container.innerHTML = '<div class="empty-message">예약이 없습니다.</div>';
        return;
    }

    container.innerHTML = reservations.map((reservation) => `
        <div class="table-row">
            <div>
                <strong>${escapeHtml(reservation.theme.name)}</strong>
                <div class="meta">${reservation.date} ${formatTime(reservation.time.startAt)} · ${escapeHtml(reservation.name)}</div>
            </div>
            <button class="danger-button small-button" type="button" data-id="${reservation.id}">삭제</button>
        </div>
    `).join('');
    container.querySelectorAll('button').forEach((button) => {
        button.addEventListener('click', () => deleteAdminReservation(button.dataset.id));
    });
}

function renderAdminTimes(times) {
    const container = $('#admin-time-list');
    if (times.length === 0) {
        container.innerHTML = '<div class="empty-message">예약 시간이 없습니다.</div>';
        return;
    }

    container.innerHTML = times.map((time) => `
        <div class="time-chip">
            <strong>${formatTime(time.startAt)}</strong>
            <button class="danger-button small-button" type="button" data-id="${time.id}">삭제</button>
        </div>
    `).join('');
    container.querySelectorAll('button').forEach((button) => {
        button.addEventListener('click', () => deleteTime(button.dataset.id));
    });
}

function renderAdminThemes(themes) {
    const container = $('#admin-theme-list');
    if (themes.length === 0) {
        container.innerHTML = '<div class="empty-message">테마가 없습니다.</div>';
        return;
    }

    container.innerHTML = themes.map((theme) => `
        ${themeCard(theme, `<button class="danger-button small-button" type="button" data-id="${theme.id}">삭제</button>`)}
    `).join('');
    container.querySelectorAll('button').forEach((button) => {
        button.addEventListener('click', () => deleteTheme(button.dataset.id));
    });
}

async function handleCreateTime(event) {
    event.preventDefault();
    try {
        await api('/admin/times', {
            method: 'POST',
            body: { startAt: $('#time-start-at').value },
        });
        event.target.reset();
        showAlert('admin', '예약 시간이 추가되었습니다.', 'success');
        await refreshAdmin();
    } catch (error) {
        showAlert('admin', error.message, 'error');
    }
}

async function handleCreateTheme(event) {
    event.preventDefault();
    try {
        await api('/admin/themes', {
            method: 'POST',
            body: {
                name: $('#theme-name').value.trim(),
                description: $('#theme-description').value.trim(),
                thumbnail: $('#theme-thumbnail').value.trim(),
            },
        });
        event.target.reset();
        showAlert('admin', '테마가 추가되었습니다.', 'success');
        await refreshAdmin();
    } catch (error) {
        showAlert('admin', error.message, 'error');
    }
}

async function deleteAdminReservation(id) {
    try {
        await api(`/admin/reservations/${id}`, { method: 'DELETE' });
        showAlert('admin', '예약이 삭제되었습니다.', 'success');
        await refreshAdmin();
    } catch (error) {
        showAlert('admin', error.message, 'error');
    }
}

async function deleteTime(id) {
    try {
        await api(`/admin/times/${id}`, { method: 'DELETE' });
        showAlert('admin', '예약 시간이 삭제되었습니다.', 'success');
        await refreshAdmin();
    } catch (error) {
        showAlert('admin', error.message, 'error');
    }
}

async function deleteTheme(id) {
    try {
        await api(`/admin/themes/${id}`, { method: 'DELETE' });
        showAlert('admin', '테마가 삭제되었습니다.', 'success');
        await refreshAdmin();
    } catch (error) {
        showAlert('admin', error.message, 'error');
    }
}

function activateAdminTab(tabId) {
    $$('.tab-button').forEach((button) => button.classList.toggle('active', button.dataset.adminTab === tabId));
    $$('.admin-tab').forEach((tab) => tab.classList.toggle('hidden', tab.id !== tabId));
}

async function api(path, options = {}) {
    const response = await fetch(path, {
        method: options.method || 'GET',
        headers: options.body ? { 'Content-Type': 'application/json' } : undefined,
        body: options.body ? JSON.stringify(options.body) : undefined,
    });

    if (response.ok) {
        if (response.status === 204) {
            return null;
        }
        return response.json();
    }

    let message = '요청을 처리하지 못했습니다.';
    try {
        const error = await response.json();
        message = error.message || message;
    } catch {
        message = `${response.status} ${response.statusText}`;
    }
    throw new Error(message);
}

function themeCard(theme, action = '') {
    return `
        <article class="theme-card">
            <img src="${escapeAttribute(theme.thumbnail)}" alt="${escapeAttribute(theme.name)}">
            <div class="theme-body">
                <h3>${escapeHtml(theme.name)}</h3>
                <p>${escapeHtml(theme.description)}</p>
                ${action ? `<div class="actions" style="margin-top: 12px;">${action}</div>` : ''}
            </div>
        </article>
    `;
}

function showAlert(area, message, type) {
    const element = $(`#${area}-alert`);
    element.textContent = message;
    element.className = `alert ${type}`;
}

function hideAlert(area) {
    const element = $(`#${area}-alert`);
    element.textContent = '';
    element.className = 'alert hidden';
}

function applyRedirectState() {
    if (window.location.hash === '#my-reservations') {
        $('#my-reservations').scrollIntoView({ block: 'start' });
    }

    const flash = sessionStorage.getItem('roomescapeFlash');
    if (flash) {
        sessionStorage.removeItem('roomescapeFlash');
        showAlert('user', flash, 'success');
    }
}

function showCompletionModal() {
    const modal = $('#completion-modal');
    const confirmButton = $('#completion-confirm-button');
    modal.classList.remove('hidden');
    confirmButton.focus();

    return new Promise((resolve) => {
        const close = () => {
            modal.classList.add('hidden');
            confirmButton.removeEventListener('click', close);
            resolve();
        };

        confirmButton.addEventListener('click', close);
    });
}

function setDefaultDate(input) {
    input.value = todayString();
    input.min = todayString();
}

function addDays(value, days) {
    const [year, month, day] = value.split('-').map(Number);
    const date = new Date(year, month - 1, day);
    date.setDate(date.getDate() + days);
    const nextYear = date.getFullYear();
    const nextMonth = String(date.getMonth() + 1).padStart(2, '0');
    const nextDay = String(date.getDate()).padStart(2, '0');
    return `${nextYear}-${nextMonth}-${nextDay}`;
}

function todayString() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function formatTime(value) {
    return String(value).slice(0, 5);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}

function escapeAttribute(value) {
    return escapeHtml(value);
}
