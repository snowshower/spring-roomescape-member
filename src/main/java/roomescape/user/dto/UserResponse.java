package roomescape.user.dto;

public class UserResponse {

    private final Long id;
    private final String name;

    private UserResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static UserResponse from(UserResult result) {
        return new UserResponse(result.id(), result.name());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
