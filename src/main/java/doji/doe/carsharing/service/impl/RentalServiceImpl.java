package doji.doe.carsharing.service.impl;

import doji.doe.carsharing.dto.rental.RentalCreateRequestDto;
import doji.doe.carsharing.dto.rental.RentalDetailedResponseDto;
import doji.doe.carsharing.dto.rental.RentalResponseDto;
import doji.doe.carsharing.dto.rental.RentalReturnRequestDto;
import doji.doe.carsharing.dto.rental.RentalSearchParametersDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.exception.RentalException;
import doji.doe.carsharing.mapper.RentalMapper;
import doji.doe.carsharing.model.Car;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.car.CarRepository;
import doji.doe.carsharing.repository.rental.RentalRepository;
import doji.doe.carsharing.repository.rental.RentalSpecificationBuilder;
import doji.doe.carsharing.service.RentalService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;

    @Transactional
    @Override
    public RentalDetailedResponseDto createRental(User user, RentalCreateRequestDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a car by id: "
                        + requestDto.getCarId()));
        if (car.getInventory() < 1) {
            throw new RentalException("Car is not available");
        }
        car.setInventory(car.getInventory() - 1);
        Rental rental = rentalMapper.toModel(requestDto, user, car);
        return rentalMapper.toDetailedResponseDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalResponseDto> search(User user,
                                          RentalSearchParametersDto params,
                                          Pageable pageable) {
        if (!user.getRole().equals(User.Role.ROLE_MANAGER)) {
            if (params.getUserId() != null) {
                throw new AccessDeniedException("This account with id: " + user.getId()
                        + " don't have permission to use userId as search parameter");
            }
            params.setUserId(user.getId().toString());
        }
        Specification<Rental> rentalSpecification = rentalSpecificationBuilder.build(params);
        return rentalRepository.findAll(rentalSpecification, pageable)
                .stream()
                .map(rentalMapper::toResponseDto)
                .toList();
    }

    @Override
    public RentalDetailedResponseDto getById(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a rental by id: " + id)
        );
        return rentalMapper.toDetailedResponseDto(rental);
    }

    @Transactional
    @Override
    public RentalDetailedResponseDto returnRental(User user,
                                                  RentalReturnRequestDto requestDto,
                                                  Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a rental by id: " + id)
        );
        if (rental.getActualReturnDate() != null) {
            throw new RentalException("Rental with id: " + id + " is already returned");
        }
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        rental.setCar(car);
        rental.setActualReturnDate(requestDto.actualReturnDate());
        return rentalMapper.toDetailedResponseDto(rentalRepository.save(rental));
    }
}
