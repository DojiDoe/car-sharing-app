package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.dto.car.CarUpdateRequestDto;
import doji.doe.carsharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    @Operation(summary = "Create a new car")
    public CarDetailedResponseDto createCar(@RequestBody @Valid CarCreateRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get all car", description = "Get a list of all cars")
    public List<CarResponseDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car by id")
    public CarDetailedResponseDto getCarById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "update a car")
    public CarDetailedResponseDto updateCar(@PathVariable Long id,
                                            @RequestBody @Valid CarCreateRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{id}")
    @Operation(summary = "update a car inventory",
            description = "Update the amount of available cars")
    public CarDetailedResponseDto updateCarInventory(
            @PathVariable Long id,
            @RequestBody @Valid CarUpdateRequestDto requestDto) {
        return carService.updateCarInventory(id, requestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    public void delete(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
