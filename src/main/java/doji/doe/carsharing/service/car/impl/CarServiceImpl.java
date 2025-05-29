package doji.doe.carsharing.service.car.impl;

import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.dto.car.CarUpdateRequestDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.mapper.CarMapper;
import doji.doe.carsharing.model.Car;
import doji.doe.carsharing.repository.car.CarRepository;
import doji.doe.carsharing.service.car.CarService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDetailedResponseDto save(CarCreateRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        return carMapper.toDetailedResponseDto(carRepository.save(car));
    }

    @Override
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toResponseDto)
                .toList();
    }

    @Override
    public CarDetailedResponseDto getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a car by id: " + id)
        );
        return carMapper.toDetailedResponseDto(car);
    }

    @Transactional
    @Override
    public CarDetailedResponseDto updateCar(Long id, CarCreateRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a car by id: " + id)
        );
        carMapper.updateCar(car, requestDto);
        return carMapper.toDetailedResponseDto(carRepository.save(car));
    }

    @Transactional
    @Override
    public CarDetailedResponseDto updateCarInventory(Long id, CarUpdateRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a car by id: " + id)
        );
        carMapper.updateCarInventory(car, requestDto);
        return carMapper.toDetailedResponseDto(carRepository.save(car));
    }

    @Override
    public void deleteById(Long id) {
        carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a car by id: " + id)
        );
        carRepository.deleteById(id);
    }
}
