package roomescape.theme.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.theme.dto.*;
import roomescape.theme.service.ThemeService;

import java.util.List;

@RestController
@RequestMapping("/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @GetMapping
    public ResponseEntity<ThemesResponse> findAll() {
        List<ThemeResult> results = themeService.findAll();
        ThemesResponse responses = ThemesResponse.from(results);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/popular")
    public ResponseEntity<PopularThemesResponse> findPopularThemes(
            @Valid @ModelAttribute PopularThemeRequest request) {
        List<PopularThemeResult> results = themeService.findPopularThemes(request.limit(), request.days());
        PopularThemesResponse response = PopularThemesResponse.from(results);

        return ResponseEntity.ok(response);
    }
}
