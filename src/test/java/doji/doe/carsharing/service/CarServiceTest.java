package doji.doe.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.dto.car.CarUpdateRequestDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.mapper.CarMapper;
import doji.doe.carsharing.model.Car;
import doji.doe.carsharing.repository.car.CarRepository;
import doji.doe.carsharing.service.car.impl.CarServiceImpl;
import doji.doe.carsharing.util.CarTestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    public static final String ENTITY_NOT_FOUND_EXCEPTION_MESSAGE = "Can't find a car by id: ";
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Verify whether CarDetailedResponseDto is valid when calling"
            + " save()  method with valid CarCreateRequestDto")
    public void save_ValidCarCreateRequestDto_ReturnsCarDetailedResponseDto() {
        // Given
        CarCreateRequestDto requestDto = CarTestUtil.getCarCreateRequestDto();
        Car car = CarTestUtil.getCar();
        CarDetailedResponseDto responseDto = CarTestUtil.getCarDetailedResponseDto(car);
        when(carMapper.toModel(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDetailedResponseDto(car)).thenReturn(responseDto);
        // When
        CarDetailedResponseDto savedCarDto = carService.save(requestDto);
        // Then
        assertThat(savedCarDto).isEqualTo(responseDto);
        verify(carRepository, times(1)).save(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify whether list of CarResponseDto is valid when calling"
            + " findAll() method with valid data")
    public void findAll_ValidPageable_ShouldReturnListOfCarResponseDto() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Car> carsFromDB = CarTestUtil.getListOfCars();
        when(carRepository.findAll(pageable)).thenReturn(new PageImpl<>(carsFromDB));
        when(carMapper.toResponseDto(any(Car.class))).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            return CarTestUtil.getCarResponseDto(car);
        });
        // When
        List<CarResponseDto> resultsDto = carService.findAll(pageable);
        // Then
        assertThat(resultsDto).hasSize(1);
        assertThat(resultsDto.getFirst().model())
                .isEqualTo(carsFromDB.getFirst().getModel());
        assertThat(resultsDto.getFirst().brand())
                .isEqualTo(carsFromDB.getFirst().getBrand());
        verify(carRepository).findAll(pageable);
        verify(carMapper, times(1)).toResponseDto(any(Car.class));
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify whether CarDetailedResponseDto is valid when calling"
            + " getById() method with valid car id")
    public void getById_ValidCarId_ShouldReturnCarDetailedResponseDto() {
        // Given
        Car car = CarTestUtil.getCar();
        CarDetailedResponseDto responseDto = CarTestUtil.getCarDetailedResponseDto(car);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toDetailedResponseDto(car)).thenReturn(responseDto);
        // When
        CarDetailedResponseDto actual = carService.getById(1L);
        // Then
        assertThat(actual).isEqualTo(responseDto);
        verify(carRepository).findById(1L);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " getById() method with invalid car id")
    public void getById_InvalidCarId_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidId = 100L;
        when(carRepository.findById(invalidId)).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.getById(invalidId)
        );
        // Then
        String expected = ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(carRepository).findById(invalidId);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " updateCar() method with invalid car id")
    public void updateCar_InvalidCarId_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidId = 100L;
        CarCreateRequestDto requestDto = CarTestUtil.getCarCreateRequestDto();
        when(carRepository.findById(invalidId)).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.updateCar(invalidId, requestDto)
        );
        // Then
        String expected = ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidId;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(carRepository).findById(invalidId);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " updateCarInventory() method with invalid car id")
    public void updateCarInventory_InvalidId_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidId = 100L;
        CarUpdateRequestDto requestDto = new CarUpdateRequestDto(10);
        when(carRepository.findById(invalidId)).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.updateCarInventory(invalidId, requestDto)
        );
        // Then
        String expected = ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidId;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(carRepository).findById(invalidId);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Verify whether car is deleted when calling"
            + " deleteCar() method with valid car id")
    public void deleteCar_ValidCarId_ShouldDeleteCar() {
        // Given
        Long carId = 1L;
        Car car = CarTestUtil.getCar();
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        // When
        carService.deleteById(carId);
        // Then
        verify(carRepository).deleteById(carId);
        verifyNoMoreInteractions(carRepository);
    }
}
