package doji.doe.carsharing.repository.rental.spec;

import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserIdSpecificationProvider implements SpecificationProvider<Rental> {
    public static final String USER_ID = "id";
    public static final String USER = "user";

    @Override
    public String getKey() {
        return USER_ID;
    }

    @Override
    public Specification<Rental> getSpecification(String param) {
        return (root, query, criteriaBuilder)
                -> root.get(USER).get(USER_ID).in(param);
    }
}
