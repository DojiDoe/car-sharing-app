package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.user.UserLoginRequestDto;
import doji.doe.carsharing.dto.user.UserLoginResponseDto;
import doji.doe.carsharing.dto.user.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.user.UserResponseDto;
import doji.doe.carsharing.security.AuthenticationService;
import doji.doe.carsharing.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User authentication", description = "Endpoints for user authentication")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "register User")
    public UserResponseDto registerUser(
            @RequestBody @Valid UserRegistrationRequestDto requestDto) {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "login User")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
