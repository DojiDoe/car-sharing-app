package doji.doe.carsharing.repository.rental.spec;

import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    public static final String IS_ACTIVE = "isActive";
    public static final String ACTUAL_RETURN_DATE = "actualReturnDate";

    @Override
    public String getKey() {
        return IS_ACTIVE;
    }

    @Override
    public Specification<Rental> getSpecification(String param) {
        return (root, query, criteriaBuilder) -> {
            if (param.equals("true")) {
                return criteriaBuilder.isNull(root.get(ACTUAL_RETURN_DATE));
            } else if (param.equals("false")) {
                return criteriaBuilder.isNotNull(root.get(ACTUAL_RETURN_DATE));
            } else {
                throw new IllegalArgumentException("isActive can equal only true or false");
            }
        };
    }
}
