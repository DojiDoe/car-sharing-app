package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.car.CarCreateRequestDto;
import doji.doe.carsharing.dto.car.CarDetailedResponseDto;
import doji.doe.carsharing.dto.car.CarResponseDto;
import doji.doe.carsharing.dto.car.CarUpdateRequestDto;
import doji.doe.carsharing.service.CarService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    public CarDetailedResponseDto createCar(@RequestBody @Valid CarCreateRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @GetMapping
    public List<CarResponseDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CarDetailedResponseDto getCarById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PutMapping("/{id}")
    public CarDetailedResponseDto updateCar(@PathVariable Long id,
                                            @RequestBody @Valid CarCreateRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }

    @PatchMapping("/{id}")
    public CarDetailedResponseDto updateCarInventory(
            @PathVariable Long id,
            @RequestBody @Valid CarUpdateRequestDto requestDto) {
        return carService.updateCarInventory(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
