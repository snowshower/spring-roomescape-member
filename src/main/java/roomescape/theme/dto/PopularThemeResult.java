package roomescape.theme.dto;

public record PopularThemeResult(
        String themeName,
        int reservationCount
) {
    public static PopularThemeResult of(String themeName, int reservationCount) {
        return new PopularThemeResult(themeName, reservationCount);
    }
}
