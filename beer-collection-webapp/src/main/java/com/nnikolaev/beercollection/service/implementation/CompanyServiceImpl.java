package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.dto.request.*;
import com.nnikolaev.beercollection.dto.response.CompanyDto;
import com.nnikolaev.beercollection.exception.EntityInUseException;
import com.nnikolaev.beercollection.exception.company.*;
import com.nnikolaev.beercollection.mapper.CompanyMapper;
import com.nnikolaev.beercollection.model.Company;
import com.nnikolaev.beercollection.repository.*;
import com.nnikolaev.beercollection.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.nnikolaev.beercollection.common.Constant.ExceptionMessage;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BeerRepository beerRepository;
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
        return this.companyMapper.map(this.getById(id));
    }

    public Void delete(UUID id)
            throws EntityInUseException, EntityNotFoundException {
        Company company = this.getById(id);

        boolean companyUsed = this.beerRepository.existsByCompanyId(id);

        if (companyUsed) throw new EntityInUseException(ExceptionMessage.Company.IN_USE);

        this.companyRepository.delete(company);

        return null;
    }

    public CompanyDto update(UUID id, CompanyUpsertDto dto)
            throws CompanyExistsException, EntityNotFoundException {
        Company company = this.getById(id);

        if (!company.getName().equals(dto.name())) this.validateName(dto.name());

        company.setName(dto.name());
        company.setDescription(dto.description());

        this.companyRepository.save(company);

        return this.companyMapper.map(company);
    }

    private Company getById(UUID id) {
        return this.companyRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessage.Company.NOT_FOUND.concat(id.toString())));
    }

    private void validateName(String name)
            throws CompanyExistsException {
        final boolean nameExists = this.companyRepository.findByName(name).isPresent();

        if (nameExists) throw new CompanyExistsException();
    }
}
