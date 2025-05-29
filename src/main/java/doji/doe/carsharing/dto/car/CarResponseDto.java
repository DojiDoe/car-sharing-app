package doji.doe.carsharing.dto.car;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public record CarResponseDto(
        Long id,
        String model,
        String brand
) {
}
