package doji.doe.carsharing.dto.car;

import jakarta.validation.constraints.Positive;

public record CarUpdateRequestDto(@Positive int inventory) {
}
