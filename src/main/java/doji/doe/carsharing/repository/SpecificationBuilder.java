package doji.doe.carsharing.repository;

import doji.doe.carsharing.dto.rental.RentalSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParametersDto searchParameters);
}
