package com.nnikolaev.beercollection.dto.request;

import com.nnikolaev.beercollection.model.enums.*;

import java.util.*;

public record QueryParamsDto(
        String search,
        Double priceMin,
        Double priceMax,
        UserRole role,
        Set<BeerColor> beerColors,
        Set<BeerTag> tags,
        Set<BoxTag> boxTags,
        Set<UUID> companyIds,
        Set<UUID> countryIds,
        Set<UUID> beerIds) {
}
