package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.dto.response.BeerDto;
import com.nnikolaev.beercollection.mapper.BeerMapper;
import com.nnikolaev.beercollection.model.Beer;
import com.nnikolaev.beercollection.repository.BeerRepository;
import com.nnikolaev.beercollection.service.BeerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.nnikolaev.beercollection.common.Constant.ExceptionMessage;

@Service
public class BeerServiceImpl implements BeerService {
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private BeerMapper beerMapper;

    public BeerDto get(UUID id) {
        Beer beer = this.beerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.Beer.NOT_FOUND));

        return this.beerMapper.map(beer);
    }
}
