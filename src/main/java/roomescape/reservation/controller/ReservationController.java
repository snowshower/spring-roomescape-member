package roomescape.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.dto.*;
import roomescape.reservation.service.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationIdResponse> create(
            @RequestBody @Valid ReservationRequest request, @RequestHeader("X-User-Id") Long userId) {
        ReservationResult result = reservationService.create(userId, request.scheduleId());
        ReservationIdResponse response = ReservationIdResponse.from(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<ReservationsResponse> findAllByUserId(@RequestHeader("X-User-Id") Long id) {
        List<ReservationResult> results = reservationService.findAllByUserId(id);
        ReservationsResponse response = ReservationsResponse.from(results);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMyReservation(
            @PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        reservationService.cancel(id, userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateMyReservation(
            @PathVariable Long id, @RequestBody @Valid ReservationUpdateRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        reservationService.changeSchedule(id, request.scheduleId(), userId);

        ReservationResult result = reservationService.findById(id);
        ReservationResponse response = ReservationResponse.from(result);

        return ResponseEntity.ok(response);
    }
}
