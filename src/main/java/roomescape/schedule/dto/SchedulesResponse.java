package roomescape.schedule.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public class SchedulesResponse {
    private final List<ScheduleResponse> scheduleResponses;

    private SchedulesResponse(List<ScheduleResponse> scheduleResponses) {
        this.scheduleResponses = scheduleResponses;
    }

    public static SchedulesResponse from(List<ScheduleResult> results) {
        List<ScheduleResponse> responses = results.stream()
                .map(ScheduleResponse::of)
                .toList();

        return new SchedulesResponse(responses);
    }

    @JsonValue
    public List<ScheduleResponse> getScheduleResponses() {
        return scheduleResponses;
    }
}
