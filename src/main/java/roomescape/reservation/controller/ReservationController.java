package roomescape.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@RequestBody @Valid ReservationRequest request) {
        ReservationResponse response = reservationService.create(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> read() {
        List<ReservationResponse> responses = reservationService.read();
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
