package doji.doe.carsharing.repository.rental;

import doji.doe.carsharing.dto.rental.RentalSearchParametersDto;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.repository.SpecificationBuilder;
import doji.doe.carsharing.repository.SpecificationProviderManager;
import doji.doe.carsharing.repository.rental.spec.IsActiveSpecificationProvider;
import doji.doe.carsharing.repository.rental.spec.UserIdSpecificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchParametersDto searchParameters) {
        Specification<Rental> spec = Specification.where(null);
        if (searchParameters.getUserId() != null) {
            spec = spec.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(UserIdSpecificationProvider.USER_ID)
                    .getSpecification(searchParameters.getUserId()));
        }
        if (searchParameters.getIsActive() != null) {
            spec = spec.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(IsActiveSpecificationProvider.IS_ACTIVE)
                    .getSpecification(searchParameters.getIsActive()));
        }
        return spec;
    }
}
