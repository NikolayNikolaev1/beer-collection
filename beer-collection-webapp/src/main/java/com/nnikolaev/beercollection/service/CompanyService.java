package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.*;
import com.nnikolaev.beercollection.dto.response.CompanyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface CompanyService {
    CompanyDto create(CompanyUpsertDto dto);

    Page<CompanyDto> getAll(QueryParamsDto params, Pageable pageable);

    CompanyDto get(UUID id);

    Void delete(UUID id);

    CompanyDto update(UUID id, CompanyUpsertDto dto);
}
