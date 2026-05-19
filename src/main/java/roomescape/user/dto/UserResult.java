package roomescape.user.dto;

import roomescape.user.model.User;

public record UserResult(
        Long id,
        String name
) {
    public static UserResult from(User user) {
        return new UserResult(user.getId(), user.getName());
    }
}
