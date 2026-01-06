package com.nnikolaev.beercollection.service.implementation;

import com.nnikolaev.beercollection.dto.request.BeerUpsertDto;
import com.nnikolaev.beercollection.dto.response.BeerDto;
import com.nnikolaev.beercollection.mapper.BeerMapper;
import com.nnikolaev.beercollection.model.*;
import com.nnikolaev.beercollection.repository.*;
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
    private CompanyRepository companyRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private BeerMapper beerMapper;

    public BeerDto create(BeerUpsertDto dto) {
        Company company = this.getCompanyById(dto.companyId());

        Country country = this.countryRepository
                .findById(dto.countryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        ExceptionMessage.Country.NOT_FOUND.concat(dto.countryId().toString())));

        Beer newBeer = new Beer(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.volume(),
                dto.alcohol(),
                dto.color(),
                dto.tag(),
                company,
                country
        );

        this.beerRepository.save(newBeer);

        return beerMapper.map(newBeer);
    }

    public BeerDto get(UUID id) {
        return this.beerMapper.map(this.getById(id));
    }

    public BeerDto update(UUID id, BeerUpsertDto dto) {
        Beer beer = this.getById(id);
        // TODO: Finish business logic.
        return this.beerMapper.map(beer);
    }

    private Beer getById(UUID id) {
        return this.beerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ExceptionMessage.Beer.NOT_FOUND.concat(id.toString())));
    }

    private Company getCompanyById(UUID id) {
        return this.companyRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ExceptionMessage.Company.NOT_FOUND.concat(id.toString())));
    }
}
