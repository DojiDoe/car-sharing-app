package doji.doe.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.repository.rental.RentalRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/clean-up-data.sql",
        "classpath:database/users/insert-users.sql",
        "classpath:database/cars/insert-cars.sql",
        "classpath:database/rentals/insert-rentals.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("find rental by id")
    public void findById_ValidId_ReturnsRental() {
        Long rentalId = 1L;
        Optional<Rental> result = rentalRepository.findById(rentalId);
        assertTrue(result.isPresent());
        assertEquals(rentalId, result.get().getId());
        assertNotNull(result.get().getCar());
    }

    @Test
    @DisplayName("find all rentals")
    public void findAll_ValidData_ReturnsListOfRental() {
        List<Rental> rentals = rentalRepository.findAll();
        assertNotNull(rentals);
        assertFalse(rentals.isEmpty());
        assertEquals(2, rentals.size());
    }

    @Test
    @DisplayName("find all overdue rentals")
    public void findOverDueRentals() {
        LocalDate localDate = LocalDate.of(2025, 7, 1);
        List<Rental> overDueRentals = rentalRepository.findOverDueRentals(localDate);
        assertNotNull(overDueRentals);
        assertFalse(overDueRentals.isEmpty());
        assertEquals(2, overDueRentals.size());
    }
}
