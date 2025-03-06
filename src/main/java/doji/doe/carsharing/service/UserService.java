package doji.doe.carsharing.service;

import doji.doe.carsharing.dto.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.UserResponseDto;
import doji.doe.carsharing.dto.UserUpdateProfileInfoRequestDto;
import doji.doe.carsharing.dto.UserUpdateRoleRequestDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    void updateRole(Long id, UserUpdateRoleRequestDto requestDto);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateProfileInfo(Long id, UserUpdateProfileInfoRequestDto requestDto);
}
