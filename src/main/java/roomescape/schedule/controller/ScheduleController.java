package roomescape.schedule.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.schedule.dto.ScheduleRequest;
import roomescape.schedule.dto.ScheduleResult;
import roomescape.schedule.dto.SchedulesResponse;
import roomescape.schedule.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<SchedulesResponse> findAll(@Valid @ModelAttribute ScheduleRequest request) {
        List<ScheduleResult> results = scheduleService.findAll(request.themeId(), request.date());
        SchedulesResponse responses = SchedulesResponse.from(results);

        return ResponseEntity.ok(responses);
    }
}
