package com.nnikolaev.beercollection.dto.request;

import com.nnikolaev.beercollection.model.enums.*;

import java.util.*;

public record QueryParamsDto(
        String search,
        UserRole role,
        Set<BeerColor> beerColors,
        Set<BeerTag> tags,
        Set<UUID> companyIds,
        Set<UUID> countryIds) {
}
