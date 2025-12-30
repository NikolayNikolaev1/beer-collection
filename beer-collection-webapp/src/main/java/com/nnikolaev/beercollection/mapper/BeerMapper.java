package com.nnikolaev.beercollection.mapper;

import com.nnikolaev.beercollection.dto.response.BeerDto;
import com.nnikolaev.beercollection.model.Beer;
import org.springframework.stereotype.Component;

@Component
public class BeerMapper {
    public BeerDto map(Beer beer) {
        if (beer == null) return null;

        return new BeerDto(
                beer.getId(),
                beer.getName(),
                beer.getDescription(),
                beer.getPrice(),
                beer.getVolume(),
                beer.getColor(),
                beer.getTag()
        );
    }
}
