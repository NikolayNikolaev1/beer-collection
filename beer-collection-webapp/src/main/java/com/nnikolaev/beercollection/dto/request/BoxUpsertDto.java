package com.nnikolaev.beercollection.dto.request;

import com.nnikolaev.beercollection.model.enums.BoxTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.*;

import static com.nnikolaev.beercollection.common.Constant.Validation;

public record BoxUpsertDto(
        @NotBlank
        @Size(min = Validation.TEXT_MIN_LENGTH, max = Validation.TEXT_MAX_LENGTH)
        String name,
        String description,
        Double price,
        Set<BoxTag> tags,
        Set<UUID> beerIds) {
}
