package doji.doe.carsharing.service;

import doji.doe.carsharing.dto.rental.RentalCreateRequestDto;
import doji.doe.carsharing.dto.rental.RentalDetailedResponseDto;
import doji.doe.carsharing.dto.rental.RentalResponseDto;
import doji.doe.carsharing.dto.rental.RentalReturnRequestDto;
import doji.doe.carsharing.dto.rental.RentalSearchParametersDto;
import doji.doe.carsharing.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDetailedResponseDto createRental(User user, RentalCreateRequestDto requestDto);

    List<RentalResponseDto> search(User user, RentalSearchParametersDto params, Pageable pageable);

    RentalDetailedResponseDto getById(Long id);

    RentalDetailedResponseDto returnRental(User user, RentalReturnRequestDto requestDto, Long id);
}
