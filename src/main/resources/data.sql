INSERT INTO reservation_time (start_at) VALUES
    ('10:00:00'),
    ('11:30:00'),
    ('13:00:00'),
    ('14:30:00'),
    ('16:00:00'),
    ('17:30:00'),
    ('19:00:00'),
    ('20:30:00');

INSERT INTO theme (name, description, thumbnail) VALUES
    ('비밀 실험실', '폐쇄된 연구소에서 사라진 연구 기록을 찾아 탈출하세요.', '/images/theme-lab.svg'),
    ('고성의 초대장', '낡은 고성의 초대장을 따라 숨겨진 방의 비밀을 밝힙니다.', '/images/theme-mansion.svg'),
    ('사라진 지하철', '막차 이후 멈춰 선 지하철에서 단서를 모아 탈출하세요.', '/images/theme-subway.svg'),
    ('금지된 서재', '봉인된 서재에서 금서의 암호를 풀고 문을 여세요.', '/images/theme-library.svg');

INSERT INTO reservation (name, date, time_id, theme_id) VALUES
    ('포비', DATEADD('DAY', 1, CURRENT_DATE), 1, 1),
    ('포비', DATEADD('DAY', 3, CURRENT_DATE), 4, 2),
    ('크롱', DATEADD('DAY', 1, CURRENT_DATE), 2, 2),
    ('루피', DATEADD('DAY', 2, CURRENT_DATE), 5, 3),
    ('패티', DATEADD('DAY', 2, CURRENT_DATE), 6, 4),
    ('해리', DATEADD('DAY', 4, CURRENT_DATE), 7, 1),
    ('에디', DATEADD('DAY', 5, CURRENT_DATE), 3, 3),
    ('통통이', DATEADD('DAY', 6, CURRENT_DATE), 8, 4),
    ('나나', DATEADD('DAY', -1, CURRENT_DATE), 1, 1),
    ('마틴', DATEADD('DAY', -1, CURRENT_DATE), 2, 1),
    ('제이슨', DATEADD('DAY', -2, CURRENT_DATE), 3, 1),
    ('소피아', DATEADD('DAY', -2, CURRENT_DATE), 4, 2),
    ('노아', DATEADD('DAY', -3, CURRENT_DATE), 5, 2),
    ('리아', DATEADD('DAY', -4, CURRENT_DATE), 6, 3),
    ('민준', DATEADD('DAY', -5, CURRENT_DATE), 7, 1),
    ('서연', DATEADD('DAY', -6, CURRENT_DATE), 8, 4),
    ('도윤', DATEADD('DAY', -8, CURRENT_DATE), 1, 4);
