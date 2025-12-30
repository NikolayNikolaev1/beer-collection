package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.response.BeerDto;

import java.util.UUID;

public interface BeerService {
    BeerDto get(UUID id);
}
