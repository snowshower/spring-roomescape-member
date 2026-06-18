package roomescape.theme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.theme.dto.ThemeResponse;
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
    public ResponseEntity<List<ThemeResponse>> read() {
        return ResponseEntity.ok().body(themeService.read());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ThemeResponse>> readPopularThemes(){
        return ResponseEntity.ok().body(themeService.readPopularThemes());
    }
}
