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

import java.util.*;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
public class BeerController {
    @Autowired
    private BeerService beerService;

    @GetMapping(Route.Beer.READ_ONE)
    // TODO: Consider making it for unauthorized users.
    public ResponseEntity<BeerDto> get(@PathVariable UUID id) {
        return ResponseHandler.success(this.beerService.get(id));
    }

    @PostMapping(Route.Beer.CREATE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<BeerDto> create(@Valid @RequestBody BeerUpsertDto req) {
        return ResponseHandler.success(this.beerService.create(req));
    }

    @DeleteMapping(Route.Beer.DELETE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<List<BeerDto>> delete(@RequestBody UUID... ids) {
        return ResponseHandler.success(this.beerService.changeDeleteStatus(true, ids));
    }

    @PutMapping(Route.Beer.UPDATE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<BeerDto> update(@PathVariable UUID id, @Valid @RequestBody BeerUpsertDto req) {
        return ResponseHandler.success(this.beerService.update(id, req));
    }
// TODO: http://localhost:8080/api/beers/restores returns status 500.
    @PatchMapping(Route.Beer.RESTORE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<List<BeerDto>> restore(@RequestBody UUID... ids) {
        return ResponseHandler.success(this.beerService.changeDeleteStatus( false, ids));
    }
}
