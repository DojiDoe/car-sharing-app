package doji.doe.carsharing.repository.rental;

import doji.doe.carsharing.model.Rental;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    @EntityGraph(attributePaths = {"user", "car"})
    Optional<Rental> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"user", "car"})
    Page<Rental> findAll(Specification<Rental> spec, Pageable pageable);

}
