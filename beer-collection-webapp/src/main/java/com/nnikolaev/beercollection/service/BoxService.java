package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.BoxUpsertDto;
import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;
import org.springframework.data.domain.*;

import java.util.*;

public interface BoxService {
    List<BoxDto> changeDeleteStatus(boolean deleteFlag, UUID... ids);

    BoxDto create(BoxUpsertDto dto);

    BoxDto get(UUID id);

    Page<BoxDto> getAll(QueryParamsDto params, Pageable pageable);

    BoxDto update(UUID id, BoxUpsertDto dto);
}
