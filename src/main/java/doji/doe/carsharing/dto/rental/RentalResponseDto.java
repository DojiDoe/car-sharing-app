package doji.doe.carsharing.dto.rental;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalResponseDto {
    private Long id;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private LocalDateTime actualReturnDate;
    private Long carId;
    private Long userId;
}
