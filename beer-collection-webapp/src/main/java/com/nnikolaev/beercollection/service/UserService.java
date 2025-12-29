package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.UserDto;
import com.nnikolaev.beercollection.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    void create(String email, String password, UserRole role);

    Page<UserDto> getAll(QueryParamsDto params, Pageable pageable);

    UserDto get(UUID id);

    Void deactivate(UUID id);
}
