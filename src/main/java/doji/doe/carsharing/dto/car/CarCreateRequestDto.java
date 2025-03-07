package doji.doe.carsharing.dto.car;

import doji.doe.carsharing.model.Car;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarCreateRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    private Car.Type type;
    @Positive
    private int inventory;
    @Min(0)
    @NotNull
    private BigDecimal dailyFee;
}
