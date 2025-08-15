package telran.project.gardenshop.utilities;

import telran.project.gardenshop.entity.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> filterProducts(
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Boolean discount,
            String sort) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.ge(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.le(root.get("price"), maxPrice));
            }

            if (discount != null && discount) {
                predicates.add(criteriaBuilder.isNotNull(root.get("discountPrice")));
            }

            if (sort != null) {
                Path<?> sortPath = root.get("price");
                if (sort.equalsIgnoreCase("ASC")) {
                    assert query != null;
                    query.orderBy(criteriaBuilder.asc(sortPath));
                } else if (sort.equalsIgnoreCase("DESC")) {
                    assert query != null;
                    query.orderBy(criteriaBuilder.desc(sortPath));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
