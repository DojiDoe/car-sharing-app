package doji.doe.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalCreateRequestDto {
    @NotNull
    private LocalDateTime rentalDate;
    @NotNull
    private LocalDateTime returnDate;
    @Positive
    private Long carId;
}
