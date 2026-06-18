package roomescape.reservationtime.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> readAvailableTimes(@RequestParam Long themeId, @RequestParam LocalDate date) {
        List<ReservationTimeResponse> responses = reservationTimeService.readAvailableTimes(themeId, date);
        return ResponseEntity.ok().body(responses);
    }
}
