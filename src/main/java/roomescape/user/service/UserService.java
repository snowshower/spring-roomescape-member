package roomescape.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.user.exception.UserErrorCode;
import roomescape.user.exception.UserException;
import roomescape.user.dto.UserResult;
import roomescape.user.model.Role;
import roomescape.user.model.User;
import roomescape.user.repository.UserRepository;

@Service
public class UserService {

    private static final Role DEFAULT = Role.USER;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResult create(String name) {
        if (userRepository.findByName(name).isPresent()) {
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User user = new User(name, DEFAULT);
        User savedUser = userRepository.create(user);

        return UserResult.from(savedUser);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public UserResult getOrCreateUserByName(String name) {
        User user = userRepository.findByName(name)
                .orElseGet(() -> userRepository.create(new User(name, Role.USER)));

        return UserResult.from(user);
    }
}
