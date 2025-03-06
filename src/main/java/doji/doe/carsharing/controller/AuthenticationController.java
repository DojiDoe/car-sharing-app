package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.UserLoginRequestDto;
import doji.doe.carsharing.dto.UserLoginResponseDto;
import doji.doe.carsharing.dto.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.UserResponseDto;
import doji.doe.carsharing.security.AuthenticationService;
import doji.doe.carsharing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserResponseDto registerUser(
            @RequestBody @Valid UserRegistrationRequestDto requestDto) {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
