package doji.doe.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import doji.doe.carsharing.dto.rental.RentalCreateRequestDto;
import doji.doe.carsharing.dto.rental.RentalDetailedResponseDto;
import doji.doe.carsharing.dto.rental.RentalReturnRequestDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.exception.RentalException;
import doji.doe.carsharing.mapper.RentalMapper;
import doji.doe.carsharing.model.Car;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.car.CarRepository;
import doji.doe.carsharing.repository.rental.RentalRepository;
import doji.doe.carsharing.service.notification.TelegramNotificationService;
import doji.doe.carsharing.service.rental.impl.RentalServiceImpl;
import doji.doe.carsharing.util.CarTestUtil;
import doji.doe.carsharing.util.RentalTestUtil;
import doji.doe.carsharing.util.UserTestUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    public static final String CAR_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE
            = "Can't find a car by id: ";
    public static final String RENTAL_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE
            = "Can't find a rental by id: ";
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private TelegramNotificationService notificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " createRental() method with invalid car id")
    public void createRental_InvalidCarId_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidCarId = 100L;
        User user = UserTestUtil.getUser();
        RentalCreateRequestDto requestDto = RentalTestUtil.getRentalCreateRequestDto();
        requestDto.setCarId(invalidCarId);
        when(carRepository.findById(invalidCarId)).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.createRental(user, requestDto)
        );
        // Then
        String expected = CAR_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidCarId;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(carRepository).findById(invalidCarId);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " createRental() method with invalid car inventory")
    public void createRental_InvalidInventory_ShouldThrowRentalException() {
        // Given
        int invalidInventory = 0;
        User user = UserTestUtil.getUser();
        RentalCreateRequestDto requestDto = RentalTestUtil.getRentalCreateRequestDto();
        Car car = CarTestUtil.getCar();
        car.setInventory(invalidInventory);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        // When
        Exception exception = assertThrows(
                RentalException.class,
                () -> rentalService.createRental(user, requestDto)
        );
        // Then
        String expected = "Car is not available";
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(carRepository).findById(car.getId());
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    @DisplayName("Verify whether RentalDetailedResponseDto is valid when calling"
            + " createRental() method")
    public void createRental_ValidRentalCreateRequestDto_ReturnsRentalDetailedResponseDto() {
        // Given
        RentalCreateRequestDto requestDto = RentalTestUtil.getRentalCreateRequestDto();
        Rental rental = RentalTestUtil.getRental();
        RentalDetailedResponseDto responseDto = RentalTestUtil.getRentalDetailedResponseDto(rental);
        User user = rental.getUser();
        Car car = rental.getCar();
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalMapper.toModel(requestDto, user, car)).thenReturn(rental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDetailedResponseDto(rental)).thenReturn(responseDto);
        // When
        RentalDetailedResponseDto result = rentalService.createRental(user, requestDto);
        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(responseDto);
        verify(carRepository, times(1)).findById(car.getId());
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(notificationService, times(1)).notifyAdmin(anyString());
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " getById() method with invalid rental id")
    public void getById_InvalidId_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidId = 100L;
        when(rentalRepository.findById(invalidId)).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.getById(invalidId)
        );
        // Then
        String expected = RENTAL_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidId;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(rentalRepository, times(1)).findById(invalidId);
        verifyNoMoreInteractions(rentalRepository);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " returnRental() method with invalid rental id")
    public void returnRental_InvalidId_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidId = 100L;
        Rental rental = RentalTestUtil.getRental();
        RentalReturnRequestDto requestDto = new RentalReturnRequestDto(rental.getReturnDate());
        User user = rental.getUser();
        when(rentalRepository.findById(invalidId)).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> rentalService.returnRental(user, requestDto, invalidId)
        );
        // Then
        String expected = RENTAL_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidId;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(rentalRepository, times(1)).findById(invalidId);
        verifyNoMoreInteractions(rentalRepository);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " returnRental() method with invalid actual return date")
    public void returnRental_InvalidActualReturnDate_ShouldThrowRentalException() {
        // Given
        Rental rental = RentalTestUtil.getRental();
        rental.setActualReturnDate(rental.getReturnDate());
        RentalReturnRequestDto requestDto = new RentalReturnRequestDto(
                rental.getActualReturnDate());
        User user = rental.getUser();
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        // When
        Exception exception = assertThrows(
                RentalException.class,
                () -> rentalService.returnRental(user, requestDto, rental.getId())
        );
        // Then
        String expected = "Rental with id: " + rental.getId() + " is already returned";
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(rentalRepository, times(1)).findById(rental.getId());
        verifyNoMoreInteractions(rentalRepository);
    }

    @Test
    @DisplayName("Verify whether RentalDetailedResponseDto is valid when calling"
            + " returnRental() method with valid rental id")
    public void returnRental_ValidId_ReturnsRentalDetailedResponseDto() {
        // Given
        Rental rental = RentalTestUtil.getRental();
        RentalReturnRequestDto requestDto = new RentalReturnRequestDto(rental.getReturnDate());
        User user = rental.getUser();
        RentalDetailedResponseDto responseDto = RentalTestUtil.getRentalDetailedResponseDto(rental);
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental rentalReturned = invocation.getArgument(0);
            rentalReturned.setActualReturnDate(rental.getReturnDate());
            return rentalReturned;
        });
        when(rentalMapper.toDetailedResponseDto(rental)).thenReturn(responseDto);
        // When
        RentalDetailedResponseDto result = rentalService
                .returnRental(user, requestDto, rental.getId());
        // Then
        assertNotNull(rental);
        assertThat(result).isEqualTo(responseDto);
        verify(rentalRepository, times(1)).findById(rental.getId());
        verify(rentalMapper, times(1)).toDetailedResponseDto(any(Rental.class));
        verifyNoMoreInteractions(rentalRepository);
    }
}
