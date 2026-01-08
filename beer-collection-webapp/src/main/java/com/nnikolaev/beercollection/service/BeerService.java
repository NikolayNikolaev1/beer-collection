package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.BeerUpsertDto;
import com.nnikolaev.beercollection.dto.response.BeerDto;

import java.util.*;

public interface BeerService {
    BeerDto create(BeerUpsertDto dto);

    List<BeerDto> changeDeleteStatus(boolean deleteFlag, UUID... ids);

    BeerDto get(UUID id);

    BeerDto update(UUID id, BeerUpsertDto dto);
}
