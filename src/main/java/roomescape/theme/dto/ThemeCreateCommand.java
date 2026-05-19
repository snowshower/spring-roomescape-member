package roomescape.theme.dto;

import java.time.LocalTime;

public record ThemeCreateCommand(
        String name,
        String description,
        String imageUrl,
        LocalTime requiredTime
) {
}
