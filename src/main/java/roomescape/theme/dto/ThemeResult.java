package roomescape.theme.dto;

import roomescape.theme.model.Theme;

import java.time.LocalTime;

public record ThemeResult(
        Long id,
        String name,
        String description,
        String imageUrl,
        LocalTime requiredTime
) {
    public static ThemeResult from(Theme theme) {
        return new ThemeResult(theme.getId(), theme.getName(),
                theme.getDescription(), theme.getImageUrl(), theme.getRequiredTime());
    }
}
