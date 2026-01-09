package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.dto.request.BoxUpsertDto;
import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.mapper.BoxMapper;
import com.nnikolaev.beercollection.model.*;
import com.nnikolaev.beercollection.repository.*;
import com.nnikolaev.beercollection.service.BoxService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static com.nnikolaev.beercollection.common.Constant.ExceptionMessage;

@Service
@Transactional
public class BoxServiceImpl implements BoxService {
    @Autowired
    private BoxRepository boxRepository;
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private BoxMapper boxMapper;

    public List<BoxDto> changeDeleteStatus(boolean deleteFlag, UUID... ids) {
        if (ids.length == 0) return null;

        List<Box> boxes = this.boxRepository.findAllById(List.of(ids));

        boxes.forEach(b -> b.setDeletedAt(deleteFlag ? Instant.now() : null));

        this.boxRepository.saveAll(boxes);
        return this.boxMapper.map(boxes);
    }

    public BoxDto create(BoxUpsertDto dto) {
        this.validateByName(dto.name());

        Box newBox = this.createNewBox(dto);

        this.boxRepository.save(newBox);
        return this.boxMapper.map(newBox);
    }

    public BoxDto get(UUID id) {
        return this.boxMapper.map(this.getById(id));
    }

    public BoxDto update(UUID id, BoxUpsertDto dto) {
        Box box = this.getById(id);

        if (!box.getName().equals(dto.name())) this.validateByName(dto.name());

        box = this.createNewBox(dto);
        box.setId(id);

        this.boxRepository.save(box);
        return this.boxMapper.map(box);
    }

    private Box createNewBox(BoxUpsertDto dto) {
        Set<Beer> availableBeers = new HashSet<>(
                this.beerRepository.findAllByIdInAndDeletedAtIsNull(dto.beerIds()));

        return new Box(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.tags(),
                availableBeers
        );
    }

    private void validateByName(String name) {
        boolean boxExists = this.boxRepository
                .findByName(name)
                .isPresent();

        if (boxExists) {
            throw new EntityExistsException(ExceptionMessage.Box.EXISTS.concat(name));
        }
    }

    private Box getById(UUID id) {
        return this.boxRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.Box.NOT_FOUND.concat(id.toString())));
    }
}
