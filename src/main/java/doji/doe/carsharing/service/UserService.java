package doji.doe.carsharing.service;

import doji.doe.carsharing.dto.user.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.user.UserResponseDto;
import doji.doe.carsharing.dto.user.UserUpdateProfileInfoRequestDto;
import doji.doe.carsharing.dto.user.UserUpdateRoleRequestDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    void updateRole(Long id, UserUpdateRoleRequestDto requestDto);

    UserResponseDto getUserById(Long id);

    UserResponseDto updateProfileInfo(Long id, UserUpdateProfileInfoRequestDto requestDto);
}
