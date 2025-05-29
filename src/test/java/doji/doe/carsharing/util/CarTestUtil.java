package doji.doe.carsharing.util;

import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.model.Car;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CarTestUtil {

    public static final String FIRST_CAR_MODEL = "Civic";
    public static final String FIRST_CAR_BRAND = "Honda";
    public static final int FIRST_CAR_INVENTORY = 10;
    public static final BigDecimal FIRST_CAR_DAILY_FEE = BigDecimal.valueOf(13);
    public static final String SECOND_CAR_MODEL = "Jetta";
    public static final String SECOND_CAR_BRAND = "Volkswagen";
    public static final int SECOND_CAR_INVENTORY = 10;
    public static final BigDecimal SECOND_CAR_DAILY_FEE = BigDecimal.valueOf(7);

    public static CarCreateRequestDto getCarCreateRequestDto() {
        return new CarCreateRequestDto()
                .setModel(FIRST_CAR_MODEL)
                .setBrand(FIRST_CAR_BRAND)
                .setType(Car.Type.UNIVERSAL)
                .setInventory(FIRST_CAR_INVENTORY)
                .setDailyFee(FIRST_CAR_DAILY_FEE);
    }

    public static Car getCar() {
        return new Car()
                .setId(1L)
                .setModel(FIRST_CAR_MODEL)
                .setBrand(FIRST_CAR_BRAND)
                .setType(Car.Type.UNIVERSAL)
                .setInventory(FIRST_CAR_INVENTORY)
                .setDailyFee(FIRST_CAR_DAILY_FEE);
    }

    public static Car getSecondCar() {
        return new Car()
                .setId(2L)
                .setModel(SECOND_CAR_MODEL)
                .setBrand(SECOND_CAR_BRAND)
                .setType(Car.Type.SEDAN)
                .setInventory(SECOND_CAR_INVENTORY)
                .setDailyFee(SECOND_CAR_DAILY_FEE);
    }

    public static CarDetailedResponseDto getCarDetailedResponseDto(Car car) {
        return new CarDetailedResponseDto()
                .setId(car.getId())
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());
    }

    public static CarDetailedResponseDto getCarDetailedResponseDto(CarCreateRequestDto car) {
        return new CarDetailedResponseDto()
                .setId(1L)
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());
    }

    public static CarResponseDto getCarResponseDto(Car car) {
        return new CarResponseDto(car.getId(), car.getModel(), car.getBrand());
    }

    public static List<Car> getListOfCars() {
        return Collections.singletonList(getCar());
    }

    public static List<CarResponseDto> getListOfCarResponseDto() {
        return Arrays.asList(
                getCarResponseDto(getCar()),
                getCarResponseDto(getSecondCar())
        );
    }
}
