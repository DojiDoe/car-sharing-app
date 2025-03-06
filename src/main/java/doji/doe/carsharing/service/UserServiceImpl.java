package doji.doe.carsharing.service;

import doji.doe.carsharing.dto.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.UserRegistrationResponseDto;
import doji.doe.carsharing.mapper.UserMapper;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.UserRepository;
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
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        User user = userMapper.toModel(requestDto);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(User.Role.CUSTOMER);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
