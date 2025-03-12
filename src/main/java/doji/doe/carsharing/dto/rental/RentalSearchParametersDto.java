package doji.doe.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalSearchParametersDto {
    private String userId;
    @NotNull
    private String isActive;
}
