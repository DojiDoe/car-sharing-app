package doji.doe.carsharing.repository.rental;

import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.repository.SpecificationProvider;
import doji.doe.carsharing.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationProviderManager implements SpecificationProviderManager<Rental> {
    private final List<SpecificationProvider<Rental>> rentalSpecificationProviders;

    @Override
    public SpecificationProvider<Rental> getSpecificationProvider(String key) {
        return rentalSpecificationProviders.stream()
                .filter(provider -> provider.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find correct "
                        + "specification provider for key: " + key));

    }
}
