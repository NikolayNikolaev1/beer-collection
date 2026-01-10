package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.BeerUpsertDto;
import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.BeerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface BeerService {
    BeerDto create(BeerUpsertDto dto);

    List<BeerDto> changeDeleteStatus(boolean deleteFlag, UUID... ids);

    BeerDto get(UUID id);

    Page<BeerDto> getAll(QueryParamsDto params, Pageable pageable);

    BeerDto update(UUID id, BeerUpsertDto dto);
}
