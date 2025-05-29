package doji.doe.carsharing.util;

import doji.doe.carsharing.dto.rental.RentalCreateRequestDto;
import doji.doe.carsharing.dto.rental.RentalDetailedResponseDto;
import doji.doe.carsharing.model.Rental;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RentalTestUtil {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final LocalDate RETURN_DATE = LocalDate.parse("2025-06-01", DATE_FORMATTER);
    public static final LocalDate RENTAL_DATE = LocalDate.parse("2025-05-28", DATE_FORMATTER);

    public static Rental getRental() {
        return new Rental()
                .setId(1L)
                .setRentalDate(RENTAL_DATE)
                .setReturnDate(RETURN_DATE)
                .setCar(CarTestUtil.getCar())
                .setUser(UserTestUtil.getUser());
    }

    public static RentalCreateRequestDto getRentalCreateRequestDto() {
        return new RentalCreateRequestDto()
                .setRentalDate(RENTAL_DATE)
                .setReturnDate(RETURN_DATE)
                .setCarId(1L);
    }

    public static RentalDetailedResponseDto getRentalDetailedResponseDto(Rental rental) {
        return new RentalDetailedResponseDto()
                .setId(rental.getId())
                .setRentalDate(rental.getRentalDate())
                .setReturnDate(rental.getReturnDate())
                .setCarResponseDto(CarTestUtil.getCarDetailedResponseDto(rental.getCar()))
                .setUserResponseDto(UserTestUtil.getUserResponseDto(rental.getUser()));
    }
}
