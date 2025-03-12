package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.rental.RentalCreateRequestDto;
import doji.doe.carsharing.dto.rental.RentalDetailedResponseDto;
import doji.doe.carsharing.dto.rental.RentalResponseDto;
import doji.doe.carsharing.dto.rental.RentalReturnRequestDto;
import doji.doe.carsharing.dto.rental.RentalSearchParametersDto;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rentals management", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @Operation(summary = "Create a new rental")
    public RentalDetailedResponseDto createRental(
            Authentication authentication,
            @RequestBody @Valid RentalCreateRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.createRental(user, requestDto);
    }

    @GetMapping
    @Operation(summary = "get rentals by params", description = "Get a list of all"
            + " rentals with sent params: for customer - {isActive} "
            + "for manager - {userId(optional), isActive}")
    public List<RentalResponseDto> searchRentals(Authentication authentication,
                                                 RentalSearchParametersDto params,
                                                 Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return rentalService.search(user, params, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rental by id")
    public RentalDetailedResponseDto getRentalById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "return Rental")
    public RentalDetailedResponseDto returnRental(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody @Valid RentalReturnRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.returnRental(user, requestDto, id);
    }
}
