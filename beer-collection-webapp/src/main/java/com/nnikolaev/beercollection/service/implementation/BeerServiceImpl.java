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

import java.time.Instant;
import java.util.List;
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
        Beer newBeer = this.createNewBeer(dto);

        this.beerRepository.save(newBeer);

        return beerMapper.map(newBeer);
    }

    public List<BeerDto> changeDeleteStatus(boolean deleteFlag, UUID... ids) {
        if (ids.length == 0) return null;

        List<Beer> beers = this.beerRepository
                .findAllById(List.of(ids));

        beers.forEach(b -> b.setDeletedAt(deleteFlag ? Instant.now() : null));

        this.beerRepository.saveAll(beers);
        return this.beerMapper.map(beers);
    }

    public BeerDto get(UUID id) {
        return this.beerMapper.map(this.getById(id));
    }

    public BeerDto update(UUID id, BeerUpsertDto dto) {
        this.getById(id);

        Beer beer = this.createNewBeer(dto);
        beer.setId(id);

        this.beerRepository.save(beer);

        return this.beerMapper.map(beer);
    }

    private Beer createNewBeer(BeerUpsertDto dto) {
        Company company = this.companyRepository
                .findById(dto.companyId())
                .orElseThrow(() -> new EntityNotFoundException(
                        ExceptionMessage.Company.NOT_FOUND.concat(dto.companyId().toString())));

        Country country = this.countryRepository
                .findById(dto.countryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        ExceptionMessage.Country.NOT_FOUND.concat(dto.countryId().toString())));

        return new Beer(
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
    }

    private Beer getById(UUID id) {
        return this.beerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        ExceptionMessage.Beer.NOT_FOUND.concat(id.toString())));
    }
}
