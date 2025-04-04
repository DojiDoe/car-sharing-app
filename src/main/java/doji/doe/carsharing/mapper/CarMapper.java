package doji.doe.carsharing.mapper;

import doji.doe.carsharing.config.MapperConfig;
import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.dto.car.CarUpdateRequestDto;
import doji.doe.carsharing.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    @Mapping(target = "id", ignore = true)
    Car toModel(CarCreateRequestDto requestDto);

    CarResponseDto toResponseDto(Car car);

    CarDetailedResponseDto toDetailedResponseDto(Car car);

    @Mapping(target = "id", ignore = true)
    void updateCar(@MappingTarget Car car, CarCreateRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "dailyFee", ignore = true)
    void updateCarInventory(@MappingTarget Car car, CarUpdateRequestDto requestDto);
}
