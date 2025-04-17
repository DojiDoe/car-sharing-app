package doji.doe.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RentalReturnRequestDto(
        @NotNull
        LocalDate actualReturnDate
) {
}
