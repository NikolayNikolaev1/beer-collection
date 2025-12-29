package com.nnikolaev.beercollection.controller;

import com.nnikolaev.beercollection.dto.request.*;
import com.nnikolaev.beercollection.dto.response.CompanyDto;
import com.nnikolaev.beercollection.security.ResponseHandler;
import com.nnikolaev.beercollection.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.nnikolaev.beercollection.common.Constant.Route;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @PostMapping(Route.Company.CREATE)
    public ResponseEntity<CompanyDto> create(@Valid @RequestBody CompanyUpsertDto req) {
        return ResponseHandler.success(this.companyService.create(req));
    }

    @GetMapping(Route.Company.READ_ONE)
    public ResponseEntity<CompanyDto> get(@PathVariable UUID id) {
        return ResponseHandler.success(this.companyService.get(id));
    }

    @GetMapping(Route.Company.READ_ALL)
    public ResponseEntity<Page<CompanyDto>> getAll(@ModelAttribute QueryParamsDto params, Pageable pageable) {
        return ResponseHandler.success(this.companyService.getAll(params, pageable));
    }

    @PutMapping(Route.Company.UPDATE)
    public ResponseEntity<CompanyDto> update(@PathVariable UUID id, @Valid @RequestBody CompanyUpsertDto req) {
        return ResponseHandler.success(this.companyService.update(id, req));
    }

    @DeleteMapping(Route.Company.DELETE)
    public ResponseEntity<?> delete(@RequestBody UUID... ids) {
        return ResponseHandler.success(this.companyService.delete(ids));
    }

    @DeleteMapping(Route.Company.DELETE_ONE)
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        return ResponseHandler.success(this.companyService.delete(id));
    }
}
