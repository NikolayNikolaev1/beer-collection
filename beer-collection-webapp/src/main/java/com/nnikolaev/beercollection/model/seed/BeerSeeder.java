package com.nnikolaev.beercollection.model.seed;

import com.nnikolaev.beercollection.model.*;
import com.nnikolaev.beercollection.model.enums.*;
import com.nnikolaev.beercollection.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(4)
public class BeerSeeder implements CommandLineRunner {
    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void run(String... args) throws Exception {
        if (this.beerRepository.count() > 0) return;

        List<Company> companies = this.companyRepository.findAll();
        List<Country> countries = this.countryRepository.findAll();

        if (companies.isEmpty() || countries.isEmpty()) return;

        List<Beer> beers = List.of(
                new Beer("Belgian Blonde", "Classic Belgian blonde ale with subtle spice.", 5.80, 330, 6.5, BeerColor.GOLD, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Munich Lager", "Traditional Bavarian lager with clean malt profile.", 4.90, 500, 5.0, BeerColor.GOLD, BeerTag.LAGER, randomCompany(companies), randomCountry(countries)),
                new Beer("London Porter", "Dark porter with roasted coffee and chocolate notes.", 5.60, 330, 6.2, BeerColor.BLACK, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Irish Red Ale", "Smooth red ale with caramel malt sweetness.", 5.40, 500, 5.4, BeerColor.RED, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Czech Pilsner", "Crisp and bitter pilsner with herbal hop aroma.", 4.70, 500, 4.8, BeerColor.GOLD, BeerTag.LAGER, randomCompany(companies), randomCountry(countries)),
                new Beer("Nordic Stout", "Rich stout brewed in Scandinavian style.", 6.20, 330, 8.0, BeerColor.BLACK, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Italian Grape Ale", "Hybrid beer fermented with Italian grape must.", 6.80, 375, 7.2, BeerColor.GOLD, BeerTag.FRUITED, randomCompany(companies), randomCountry(countries)),
                new Beer("Spanish Citrus IPA", "IPA with bright citrus hops and dry finish.", 6.50, 440, 6.7, BeerColor.GOLD, BeerTag.IPA, randomCompany(companies), randomCountry(countries)),
                new Beer("French Saison", "Farmhouse ale with peppery yeast character.", 5.90, 750, 6.3, BeerColor.GOLD, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Dutch Hoppy Lager", "Modern lager with expressive hop aroma.", 5.30, 500, 5.5, BeerColor.GOLD, BeerTag.LAGER, randomCompany(companies), randomCountry(countries)),
                new Beer("Baltic Porter", "Strong porter with dark fruit and toffee notes.", 6.90, 330, 8.5, BeerColor.BLACK, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Austrian Märzen", "Malty Märzen with toasted bread aroma.", 5.20, 500, 5.6, BeerColor.BROWN, BeerTag.LAGER, randomCompany(companies), randomCountry(countries)),
                new Beer("Swiss Amber Ale", "Balanced amber ale with nutty malt profile.", 5.70, 330, 5.8, BeerColor.BROWN, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Polish Grodziskie", "Light smoked wheat beer with crisp finish.", 4.80, 500, 3.8, BeerColor.GOLD, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Greek Honey Ale", "Ale brewed with local honey for floral sweetness.", 6.10, 330, 6.4, BeerColor.GOLD, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Portuguese Atlantic IPA", "West coast IPA with resinous bitterness.", 6.60, 440, 6.9, BeerColor.GOLD, BeerTag.IPA, randomCompany(companies), randomCountry(countries)),
                new Beer("Romanian Dark Lager", "Smooth dark lager with chocolate malt notes.", 5.10, 500, 5.2, BeerColor.BROWN, BeerTag.LAGER, randomCompany(companies), randomCountry(countries)),
                new Beer("Hungarian Plum Ale", "Fruited ale brewed with ripe plums.", 6.30, 330, 6.1, BeerColor.RED, BeerTag.FRUITED, randomCompany(companies), randomCountry(countries)),
                new Beer("Croatian Coastal Pale Ale", "Refreshing pale ale inspired by Adriatic coast.", 5.50, 440, 5.6, BeerColor.GOLD, BeerTag.ALE, randomCompany(companies), randomCountry(countries)),
                new Beer("Slovenian Forest Stout", "Stout with earthy tones and roasted bitterness.", 6.70, 330, 7.4, BeerColor.BLACK, BeerTag.ALE, randomCompany(companies), randomCountry(countries))
        );

        this.beerRepository.saveAll(beers);
    }

    private Company randomCompany(List<Company> companies) {
        return companies.get(ThreadLocalRandom.current().nextInt(companies.size()));
    }
    
    private Country randomCountry(List<Country> countries) {
        return countries.get(ThreadLocalRandom.current().nextInt(countries.size()));
    }
}
