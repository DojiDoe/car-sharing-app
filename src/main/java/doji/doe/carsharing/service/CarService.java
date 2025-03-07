package doji.doe.carsharing.service;

import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.dto.car.CarUpdateRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDetailedResponseDto save(CarCreateRequestDto requestDto);

    List<CarResponseDto> findAll(Pageable pageable);

    CarDetailedResponseDto getById(Long id);

    CarDetailedResponseDto updateCar(Long id, CarCreateRequestDto requestDto);

    CarDetailedResponseDto updateCarInventory(Long id, CarUpdateRequestDto requestDto);

    void deleteById(Long id);
}
