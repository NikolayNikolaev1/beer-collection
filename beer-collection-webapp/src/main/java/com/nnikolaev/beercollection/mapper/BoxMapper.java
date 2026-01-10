package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.model.Box;
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

            if (params.tags() != null && !params.tags().isEmpty()) {
                // TODO: Fix this filter
                predicate = criteriaBuilder.and(
                        predicate,
                        root.get("tags").in(params.tags())
                );
            }

            return predicate;
        };
    }
}
