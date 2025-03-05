package doji.doe.carsharing.service;

import doji.doe.carsharing.dto.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.UserRegistrationResponseDto;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);
}
