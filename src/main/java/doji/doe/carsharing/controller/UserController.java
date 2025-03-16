package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.user.UserResponseDto;
import doji.doe.carsharing.dto.user.UserUpdateProfileInfoRequestDto;
import doji.doe.carsharing.dto.user.UserUpdateRoleRequestDto;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    @Operation(summary = "update a user role")
    public void updateRole(@PathVariable Long id,
                           @RequestBody @Valid UserUpdateRoleRequestDto requestDto) {
        userService.updateRole(id, requestDto);
    }

    @GetMapping("/me")
    @Operation(summary = "get user profile info",
            description = "get profile info of authenticated user")
    public UserResponseDto getProfileInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userService.getUserById(user.getId());
    }

    @PutMapping("/me")
    @Operation(summary = "update user profile info",
            description = "update profile info of authenticated user")
    public UserResponseDto updateProfileIfo(
            Authentication authentication,
            @RequestBody @Valid UserUpdateProfileInfoRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfileInfo(user.getId(), requestDto);
    }
}
