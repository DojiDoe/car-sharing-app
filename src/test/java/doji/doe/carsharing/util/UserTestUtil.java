package doji.doe.carsharing.util;

import doji.doe.carsharing.dto.user.UserRegistrationRequestDto;
import doji.doe.carsharing.dto.user.UserResponseDto;
import doji.doe.carsharing.dto.user.UserUpdateProfileInfoRequestDto;
import doji.doe.carsharing.model.User;

public class UserTestUtil {

    public static final String CUSTOMER_EMAIL = "dojidoe@gmail.com";
    public static final String CUSTOMER_FIRST_NAME = "Doji";
    public static final String CUSTOMER_LAST_NAME = "Doe";
    public static final String CUSTOMER_PASSWORD = "qwerty123";

    public static User getUser() {
        return new User()
                .setId(2L)
                .setEmail(CUSTOMER_EMAIL)
                .setFirstName(CUSTOMER_FIRST_NAME)
                .setLastName(CUSTOMER_LAST_NAME)
                .setPassword(CUSTOMER_PASSWORD)
                .setRole(User.Role.ROLE_CUSTOMER);
    }

    public static UserResponseDto getUserResponseDto(User user) {
        return new UserResponseDto()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());
    }

    public static UserRegistrationRequestDto getUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setEmail(CUSTOMER_EMAIL)
                .setFirstName(CUSTOMER_FIRST_NAME)
                .setLastName(CUSTOMER_LAST_NAME)
                .setPassword(CUSTOMER_PASSWORD)
                .setRepeatPassword(CUSTOMER_PASSWORD);
    }

    public static UserUpdateProfileInfoRequestDto getUserUpdateProfileInfoRequestDto() {
        return new UserUpdateProfileInfoRequestDto()
                .setEmail(CUSTOMER_EMAIL)
                .setFirstName(CUSTOMER_FIRST_NAME)
                .setLastName(CUSTOMER_LAST_NAME);
    }
}
