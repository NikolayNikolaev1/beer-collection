package com.nnikolaev.beercollection.controller;

import com.nnikolaev.beercollection.common.Constant;
import com.nnikolaev.beercollection.dto.request.BeerUpsertDto;
import com.nnikolaev.beercollection.dto.response.BeerDto;
import com.nnikolaev.beercollection.security.ResponseHandler;
import com.nnikolaev.beercollection.service.BeerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
@PreAuthorize(Constant.ADMIN_AUTHORIZATION)
public class BeerController {
    @Autowired
    private BeerService beerService;

    @GetMapping(Route.Beer.READ_ONE)
    public ResponseEntity<BeerDto> get(@PathVariable UUID id) {
        return ResponseHandler.success(this.beerService.get(id));
    }

    @PostMapping(Route.Beer.CREATE)
    public ResponseEntity<BeerDto> create(@Valid @RequestBody BeerUpsertDto req) {
        return ResponseHandler.success(this.beerService.create(req));
    }
}
