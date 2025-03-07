package doji.doe.carsharing.dto.car;

import doji.doe.carsharing.model.Car;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarDetailedResponseDto {
    private Long id;
    private String model;
    private String brand;
    private Car.Type type;
    private int inventory;
    private BigDecimal dailyFee;
}
