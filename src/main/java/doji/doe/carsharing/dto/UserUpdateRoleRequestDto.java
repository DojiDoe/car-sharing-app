package doji.doe.carsharing.dto;

import doji.doe.carsharing.model.User;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRoleRequestDto(
        @NotNull
        User.Role role
) {
}
