package doji.doe.carsharing.security;

import doji.doe.carsharing.dto.UserLoginRequestDto;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    public boolean authenticate(UserLoginRequestDto requestDto) {
        Optional<User> user = userRepository.findByEmail(requestDto.email());
        if (user.isEmpty()) {
            return false;
        }
        String userPasswordFromDb = user.get().getPassword();
        return requestDto.password().equals(userPasswordFromDb);
    }
}
