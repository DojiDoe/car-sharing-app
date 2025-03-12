package doji.doe.carsharing.dto.rental;

import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.user.UserResponseDto;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalDetailedResponseDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private CarDetailedResponseDto carResponseDto;
    private UserResponseDto userResponseDto;
}
