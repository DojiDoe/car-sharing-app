package doji.doe.carsharing.service.user.impl;

import doji.doe.carsharing.dto.user.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.user.UserResponseDto;
import doji.doe.carsharing.dto.user.UserUpdateProfileInfoRequestDto;
import doji.doe.carsharing.dto.user.UserUpdateRoleRequestDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.exception.RegistrationException;
import doji.doe.carsharing.mapper.UserMapper;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.user.UserRepository;
import doji.doe.carsharing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(User.Role.ROLE_CUSTOMER);
        User saved = userRepository.save(user);
        return userMapper.toUserResponseDto(saved);
    }

    @Override
    public void updateRole(Long id, UserUpdateRoleRequestDto requestDto) {
        User user = getUser(id);
        user.setRole(requestDto.role());
        userRepository.save(user);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return userMapper.toUserResponseDto(getUser(id));
    }

    @Override
    public UserResponseDto updateProfileInfo(
            Long id,
            UserUpdateProfileInfoRequestDto requestDto) {
        User user = getUser(id);
        userMapper.updateUser(user, requestDto);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't get user by id: " + id)
        );
    }
}
