package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.BoxUpsertDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;

import java.util.*;

public interface BoxService {
    List<BoxDto> changeDeleteStatus(boolean deleteFlag, UUID... ids);

    BoxDto create(BoxUpsertDto dto);

    BoxDto get(UUID id);

    BoxDto update(UUID id, BoxUpsertDto dto);
}
