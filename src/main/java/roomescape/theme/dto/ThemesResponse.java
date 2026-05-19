package roomescape.theme.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public class ThemesResponse {
    private final List<ThemeResponse> themeResponses;

    public ThemesResponse(List<ThemeResponse> themeResponses) {
        this.themeResponses = themeResponses;
    }

    public static ThemesResponse from(List<ThemeResult> results) {
        List<ThemeResponse> responses = results.stream()
                .map(ThemeResponse::from)
                .toList();

        return new ThemesResponse(responses);
    }

    @JsonValue
    public List<ThemeResponse> getThemeResponses() {
        return themeResponses;
    }
}
