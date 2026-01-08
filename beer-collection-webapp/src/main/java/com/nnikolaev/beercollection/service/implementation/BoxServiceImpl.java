package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.dto.request.BoxUpsertDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.mapper.BoxMapper;
import com.nnikolaev.beercollection.model.*;
import com.nnikolaev.beercollection.repository.*;
import com.nnikolaev.beercollection.service.BoxService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.nnikolaev.beercollection.common.Constant.ExceptionMessage;

@Service
public class BoxServiceImpl implements BoxService {
    @Autowired
    private BoxRepository boxRepository;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private BoxMapper boxMapper;

    public BoxDto create(BoxUpsertDto dto) {
        boolean boxExists = this.boxRepository
                .findByName(dto.name())
                .isPresent();

        if (boxExists) {
            throw new EntityExistsException(ExceptionMessage.Box.EXISTS.concat(dto.name()));
        }

        // TODO: Filter deleted beers.
        Set<Beer> beers = new HashSet<>(this.beerRepository.findAllById(dto.beerIds()));

        Box newBox = new Box(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.tags(),
                beers);

        this.boxRepository.save(newBox);

        return this.boxMapper.map(newBox);
    }
}
