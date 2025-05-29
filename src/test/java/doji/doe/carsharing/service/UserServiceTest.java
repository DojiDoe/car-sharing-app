package doji.doe.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import doji.doe.carsharing.dto.user.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.user.UserResponseDto;
import doji.doe.carsharing.dto.user.UserUpdateProfileInfoRequestDto;
import doji.doe.carsharing.dto.user.UserUpdateRoleRequestDto;
import doji.doe.carsharing.exception.RegistrationException;
import doji.doe.carsharing.mapper.UserMapper;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.user.UserRepository;
import doji.doe.carsharing.service.user.impl.UserServiceImpl;
import doji.doe.carsharing.util.UserTestUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    public static final String USER_REGISTRATION_EXCEPTION = "Can't register user";
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Verify whether user is registered when calling"
            + " register() method with invalid request")
    public void register_InvalidUserRegistrationRequestDto_ShouldRegistrationException() {
        // Given
        String invalidEmail = "invalidEmail@gmail.com";
        UserRegistrationRequestDto requestDto = UserTestUtil.getUserRegistrationRequestDto();
        requestDto.setEmail(invalidEmail);
        when(userRepository.existsByEmail(invalidEmail)).thenReturn(true);
        // When
        Exception exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(requestDto));
        // Then
        String expected = USER_REGISTRATION_EXCEPTION;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).existsByEmail(invalidEmail);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Verify whether UserResponseDto is valid when calling"
            + " register() method with valid request")
    public void register_ValidUserRegistrationRequestDto_ReturnsUserResponseDto() {
        // Given
        UserRegistrationRequestDto requestDto = UserTestUtil.getUserRegistrationRequestDto();
        User user = UserTestUtil.getUser();
        UserResponseDto responseDto = UserTestUtil.getUserResponseDto(user);
        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(encoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(responseDto);
        // When
        UserResponseDto result = userService.register(requestDto);
        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(responseDto);
        verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toModel(requestDto);
        verify(userMapper, times(1)).toUserResponseDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify whether role of user is updated when calling"
            + " updateRole() method with valid data")
    public void updateRole_ValidData_ShouldUpdateRole() {
        // Given
        Long id = 2L;
        UserUpdateRoleRequestDto requestDto = new UserUpdateRoleRequestDto(User.Role.ROLE_MANAGER);
        User user = UserTestUtil.getUser().setRole(User.Role.ROLE_MANAGER);
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
        // When
        userService.updateRole(id, requestDto);
        // Then
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Verify whether UserResponseDto is valid when calling"
            + " getUserById() method with valid data")
    public void getUserById_ValidData_ShouldReturnUserResponseDto() {
        // Given
        Long id = 2L;
        User user = UserTestUtil.getUser();
        UserResponseDto responseDto = UserTestUtil.getUserResponseDto(user);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(responseDto);
        // When
        UserResponseDto result = userService.getUserById(id);
        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(responseDto);
        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toUserResponseDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify whether UserResponseDto is valid when calling"
            + " updateProfileInfo() method with valid data")
    public void updateProfileInfo_ValidData_ShouldUserResponseDto() {
        // Given
        Long id = 2L;
        User user = UserTestUtil.getUser();
        UserUpdateProfileInfoRequestDto requestDto = UserTestUtil
                .getUserUpdateProfileInfoRequestDto();
        UserResponseDto responseDto = UserTestUtil.getUserResponseDto(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(responseDto);
        // When
        UserResponseDto result = userService.updateProfileInfo(id, requestDto);
        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(responseDto);
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponseDto(user);
        verifyNoMoreInteractions(userRepository);
    }
}
