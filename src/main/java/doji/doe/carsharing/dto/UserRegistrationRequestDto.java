package doji.doe.carsharing.dto;

import doji.doe.carsharing.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(first = "password", second = "repeatPassword",
        message = "The password fields don't match")
public class UserRegistrationRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Length(min = 8, max = 35)
    @NotBlank
    private String password;
    @Length(min = 8, max = 35)
    @NotBlank
    private String repeatPassword;
}
