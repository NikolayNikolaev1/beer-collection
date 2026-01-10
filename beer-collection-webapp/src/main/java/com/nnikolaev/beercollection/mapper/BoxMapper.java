package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.model.Beer;
import com.nnikolaev.beercollection.model.Box;
import com.nnikolaev.beercollection.model.enums.BoxTag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoxMapper {
    @Autowired
    private BeerMapper beerMapper;

    public BoxDto map(Box box) {
        if (box == null) return null;

        return new BoxDto(
                box.getId(),
                box.getName(),
                box.getDescription(),
                box.getPrice(),
                box.getDeletedAt() != null,
                box.getUpdatedAt(),
                box.getTags().stream().toList(),
                this.beerMapper.map(box.getBeers().stream().toList())
        );
    }

    public List<BoxDto> map(List<Box> boxes) {
        return boxes
                .stream()
                .map(this::map)
                .toList();
    }

    public Page<BoxDto> map(Page<Box> boxes) {
        return boxes.map(this::map);
    }

    public Specification<Box> mapSpecifications(QueryParamsDto params) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (params.search() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + params.search().toLowerCase() + "%"));
            }

            if (params.priceMin() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("priceEu"),
                                params.priceMin()
                        )
                );
            }

            if (params.priceMax() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("priceEu"),
                                params.priceMax()
                        )
                );
            }

            if (params.boxTags() != null && !params.boxTags().isEmpty()) {
                Join<Box, BoxTag> tagJoin = root.join("tags");

                predicate = criteriaBuilder.and(
                        predicate,
                        tagJoin.in(params.boxTags())
                );
            }

            if (params.beerIds() != null && !params.beerIds().isEmpty()) {
                Join<Box, Beer> beerJoin = root.join("beers");

                predicate = criteriaBuilder.and(
                        predicate,
                        beerJoin.get("id").in(params.beerIds())
                );
            }

            return predicate;
        };
    }
}
