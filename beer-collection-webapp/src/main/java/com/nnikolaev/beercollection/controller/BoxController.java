package com.nnikolaev.beercollection.controller;

import com.nnikolaev.beercollection.common.Constant;
import com.nnikolaev.beercollection.dto.request.*;
import com.nnikolaev.beercollection.dto.response.BoxDto;
import com.nnikolaev.beercollection.security.ResponseHandler;
import com.nnikolaev.beercollection.service.BoxService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
public class BoxController {
    @Autowired
    private BoxService boxService;

    @GetMapping(Route.Box.READ_ONE)
    public ResponseEntity<BoxDto> get(@PathVariable UUID id){
        return ResponseHandler.success(this.boxService.get(id));
    }

    @GetMapping(Route.Box.READ_ALL)
    public ResponseEntity<Page<BoxDto>> get(@ModelAttribute QueryParamsDto params, Pageable pageable) {
        return ResponseHandler.success(this.boxService.getAll(params, pageable));
    }

    @PostMapping(Route.Box.CREATE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<BoxDto> create(@Valid @RequestBody BoxUpsertDto req) {
        return ResponseHandler.success(this.boxService.create(req));
    }

    @DeleteMapping(Route.Box.DELETE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<List<BoxDto>> delete(@RequestBody UUID... ids) {
        return ResponseHandler.success(this.boxService.changeDeleteStatus(true, ids));
    }

    @PutMapping(Route.Box.UPDATE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<BoxDto> create(@PathVariable UUID id, @Valid @RequestBody BoxUpsertDto req) {
        return ResponseHandler.success(this.boxService.update(id, req));
    }

    @PatchMapping(Route.Box.RESTORE)
    @PreAuthorize(Constant.ADMIN_AUTHORIZATION)
    public ResponseEntity<List<BoxDto>> restore(@RequestBody UUID... ids) {
        return ResponseHandler.success(this.boxService.changeDeleteStatus( false, ids));
    }
}
