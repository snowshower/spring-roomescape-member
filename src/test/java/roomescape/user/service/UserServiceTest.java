package roomescape.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.support.DatabaseHelper;
import roomescape.user.dto.UserResult;
import roomescape.user.exception.UserException;
import roomescape.user.model.User;
import roomescape.user.repository.UserRepository;

import java.util.Optional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseHelper databaseHelper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        databaseHelper.cleanUp();
    }

    @Test
    void 유저를_생성하면_DB에_실제로_저장되다() {
        UserResult result = userService.create("루크");

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("루크");
    }

    @Test
    void 기존_유저가_로그인하면_새로_생성하지_않고_조회한다() {
        databaseHelper.insertUser(1L, "소낙눈", "USER");
        UserResult result = userService.getOrCreateUserByName("소낙눈");

        assertThat(result.name()).isEqualTo("소낙눈");
    }

    @Test
    void 새로운_유저가_로그인하면_DB에_새로_저장된다() {
        UserResult result = userService.getOrCreateUserByName("피노");

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("피노");

        Optional<User> foundUser = userRepository.findById(result.id());
        assertThat(foundUser).isPresent();
    }

    @Test
    void 이미_존재하는_이름으로_생성하려고_하면_예외가_발생한다() {
        databaseHelper.insertUser(1L, "루크", "USER");

        assertThatThrownBy(() -> userService.create("루크"))
                .isInstanceOf(UserException.class);
    }
}
