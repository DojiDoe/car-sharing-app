package doji.doe.carsharing.mapper;

import doji.doe.carsharing.config.MapperConfig;
import doji.doe.carsharing.dto.rental.RentalCreateRequestDto;
import doji.doe.carsharing.dto.rental.RentalDetailedResponseDto;
import doji.doe.carsharing.dto.rental.RentalResponseDto;
import doji.doe.carsharing.model.Car;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class, UserMapper.class})
public interface RentalMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "car", source = "car")
    Rental toModel(RentalCreateRequestDto requestDto, User user, Car car);

    @Mapping(target = "carId", source = "rental.car.id")
    @Mapping(target = "userId", source = "rental.user.id")
    RentalResponseDto toResponseDto(Rental rental);

    @Mapping(target = "carResponseDto", source = "car")
    @Mapping(target = "userResponseDto", source = "user")
    RentalDetailedResponseDto toDetailedResponseDto(Rental rental);
}
