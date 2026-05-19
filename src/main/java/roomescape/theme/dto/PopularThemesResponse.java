package roomescape.theme.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public class PopularThemesResponse {

    private final List<PopularThemeResponse> popularThemeResponses;

    private PopularThemesResponse(List<PopularThemeResponse> popularThemeResponses) {
        this.popularThemeResponses = popularThemeResponses;
    }

    public static PopularThemesResponse from(List<PopularThemeResult> results) {
        List<PopularThemeResponse> responses = results.stream()
                .map(PopularThemeResponse::from)
                .toList();

        return new PopularThemesResponse(responses);
    }

    @JsonValue
    public List<PopularThemeResponse> getPopularThemeResponses() {
        return popularThemeResponses;
    }
}
