package doji.doe.carsharing.repository.rental;

import doji.doe.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    @EntityGraph(attributePaths = {"user", "car"})
    Optional<Rental> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"user", "car"})
    Page<Rental> findAll(Specification<Rental> spec, Pageable pageable);

    @Query("SELECT r FROM Rental r JOIN FETCH r.car c JOIN FETCH r.user u "
            + "WHERE r.actualReturnDate IS NULL "
            + "AND r.returnDate <= :currentDate ")
    List<Rental> findOverDueRentals(LocalDate currentDate);

}
