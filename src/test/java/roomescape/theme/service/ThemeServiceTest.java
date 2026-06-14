package roomescape.theme.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class ThemeServiceTest {

    @Autowired
    private ThemeService themeService;

    @Test
    void create_test() {
        // given
        ThemeRequest request = new ThemeRequest("테마1", "설명1", "썸네일1");

        // when
        ThemeResponse response = themeService.create(request);
        Long id = response.getId();

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void read_test() {
        // given
        ThemeRequest request1 = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeRequest request2 = new ThemeRequest("테마2", "설명2", "썸네일2");

        themeService.create(request1);
        themeService.create(request2);

        // when
        List<ThemeResponse> responses = themeService.read();

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses).isNotNull(),
                () -> assertThat(responses.get(0).getName()).isEqualTo("테마1"),
                () -> assertThat(responses.get(1).getDescription()).isEqualTo("설명2")
        );
    }

    @Test
    void delete_test() {
        // given
        ThemeRequest request = new ThemeRequest("테마1", "설명1", "썸네일1");
        ThemeResponse response= themeService.create(request);
        Long id = response.getId();

        // when
        themeService.delete(id);
        List<ThemeResponse> responses = themeService.read();

        // then
        assertThat(responses).isEmpty();
    }
}
