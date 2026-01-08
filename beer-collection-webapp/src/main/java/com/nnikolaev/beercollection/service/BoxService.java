package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.BoxUpsertDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;

public interface BoxService {
    BoxDto create(BoxUpsertDto dto);
}
