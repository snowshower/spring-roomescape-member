package roomescape.reservationtime.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @PostMapping
    public ResponseEntity<ReservationTimeResponse> create(@RequestBody @Valid ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.create(request);
        return ResponseEntity.created(URI.create("/times/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> read() {
        List<ReservationTimeResponse> responses = reservationTimeService.read();
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
