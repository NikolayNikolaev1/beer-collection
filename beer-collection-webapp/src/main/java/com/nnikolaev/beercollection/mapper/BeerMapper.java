package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.response.BeerDto;
import com.nnikolaev.beercollection.model.Beer;
import org.springframework.beans.factory.annotation.Autowired;
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
}
