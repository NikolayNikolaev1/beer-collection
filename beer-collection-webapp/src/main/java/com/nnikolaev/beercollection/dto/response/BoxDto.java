package com.nnikolaev.beercollection.dto.response;

import com.nnikolaev.beercollection.model.enums.BoxTag;

import java.time.Instant;
import java.util.*;

public record BoxDto(
        UUID id,
        String name,
        String description,
        Double price,
        boolean isDeleted,
        Instant modifiedAt,
        List<BoxTag> tags,
        List<BeerDto> beerPool) {
}
