package doji.doe.carsharing.dto.rental;

import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.user.UserResponseDto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDetailedResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private CarDetailedResponseDto carResponseDto;
    private UserResponseDto userResponseDto;
}
