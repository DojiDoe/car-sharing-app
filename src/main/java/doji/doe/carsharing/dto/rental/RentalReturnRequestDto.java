package doji.doe.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RentalReturnRequestDto(
        @NotNull
        LocalDateTime actualReturnDate
) {
}
