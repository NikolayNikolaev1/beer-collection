package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.BeerUpsertDto;
import com.nnikolaev.beercollection.dto.response.BeerDto;

import java.util.UUID;

public interface BeerService {
    BeerDto create(BeerUpsertDto dto);

    BeerDto get(UUID id);

    BeerDto update(UUID id, BeerUpsertDto dto);
}
