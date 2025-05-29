package doji.doe.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/users/remove-users.sql",
        "classpath:database/users/insert-users.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserRepositoryTest {
    public static final String USER_EMAIL = "dojidoe@gmail.com";
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Check if user exists by email")
    public void existsByEmail_ValidEmail_ReturnsTrue() {
        boolean result = userRepository.existsByEmail(USER_EMAIL);
        assertTrue(result);
    }

    @Test
    @DisplayName("Find user by email")
    public void findByEmail_ValidEmail_ReturnsUser() {
        Optional<User> result = userRepository.findByEmail(USER_EMAIL);
        assertTrue(result.isPresent());
        assertEquals(USER_EMAIL, result.get().getEmail());
    }
}
