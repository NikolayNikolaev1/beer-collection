package com.nnikolaev.beercollection.service;

import com.nnikolaev.beercollection.dto.request.CompanyUpsertDto;
import com.nnikolaev.beercollection.dto.request.QueryParamsDto;
import com.nnikolaev.beercollection.dto.response.CompanyDto;
import com.nnikolaev.beercollection.exception.company.*;
import com.nnikolaev.beercollection.mapper.CompanyMapper;
import com.nnikolaev.beercollection.model.Company;
import com.nnikolaev.beercollection.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyMapper companyMapper;

    public CompanyDto create(CompanyUpsertDto dto)
            throws CompanyExistsException {
        this.validateName(dto.name());

        final Company newCompany = new Company(dto.name(), dto.description());

        this.companyRepository.save(newCompany);

        return this.companyMapper.map(newCompany);
    }

    public Page<CompanyDto> getAll(QueryParamsDto params, Pageable pageable) {
        Specification<Company> spec = this.companyMapper.mapSpecifications(params);

        Page<Company> page = this.companyRepository.findAll(spec, pageable);

        return this.companyMapper.map(page);
    }

    public CompanyDto get(UUID id) {
        Company company = this.companyRepository
                .findById(id)
                .orElseThrow(CompanyNotFoundException::new);

        return this.companyMapper.map(company);
    }

    public Void delete(UUID... ids) {
        if (ids.length == 0) return null;

        this.companyRepository.deleteAllById(List.of(ids));
        return null;
    }

    public CompanyDto update(UUID id, CompanyUpsertDto dto)
            throws CompanyExistsException, CompanyNotFoundException {
        Company company = this.companyRepository
                .findById(id)
                .orElseThrow(CompanyNotFoundException::new);

        if (!company.getName().equals(dto.name())) this.validateName(dto.name());

        company.setName(dto.name());
        company.setDescription(dto.description());

        this.companyRepository.save(company);

        return this.companyMapper.map(company);
    }

    private void validateName(String name)
            throws CompanyExistsException {
        final boolean nameExists = this.companyRepository.findByName(name).isPresent();

        if (nameExists) throw new CompanyExistsException();
    }
}
