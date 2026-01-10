package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.BeerDto;
import com.nnikolaev.beercollection.model.Beer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeerMapper {
    @Autowired
    private CompanyMapper companyMapper;

    public BeerDto map(Beer beer) {
        if (beer == null) return null;

        return new BeerDto(
                beer.getId(),
                beer.getName(),
                beer.getDescription(),
                beer.getPrice(),
                beer.getVolume(),
                beer.getAlcohol(),
                beer.getColor(),
                beer.getTag(),
                beer.getCompany().getName(),
                beer.getCountry().getName(),
                beer.getDeletedAt() != null,
                beer.getCreatedAt(),
                beer.getUpdatedAt()
        );
    }

    public List<BeerDto> map(List<Beer> beers) {
        return beers
                .stream()
                .map(this::map)
                .toList();
    }

    public Page<BeerDto> map(Page<Beer> beers) {
        return beers.map(this::map);
    }

    public Specification<Beer> mapSpecifications(QueryParamsDto params) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (params.search() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + params.search().toLowerCase() + "%"));
            }

            if (params.beerColors() != null && !params.beerColors().isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        root.get("color").in(params.beerColors())
                );
            }

            if (params.tags() != null && !params.tags().isEmpty()){
                predicate = criteriaBuilder.and(
                        predicate,
                        root.get("tag").in(params.tags())
                );
            }

            if (params.companyIds() != null && !params.companyIds().isEmpty()){
                predicate = criteriaBuilder.and(
                        predicate,
                        root.get("companyId").in(params.companyIds())
                );
            }

            if (params.countryIds() != null && !params.countryIds().isEmpty()){
                predicate = criteriaBuilder.and(
                        predicate,
                        root.get("countryId").in(params.countryIds())
                );
            }

            return predicate;
        };
    }
}
