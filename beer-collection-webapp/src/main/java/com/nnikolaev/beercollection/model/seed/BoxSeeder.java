package com.nnikolaev.beercollection.model.seed;

import com.nnikolaev.beercollection.model.*;
import com.nnikolaev.beercollection.model.enums.BoxTag;
import com.nnikolaev.beercollection.repository.*;
import com.nnikolaev.beercollection.service.BoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Order(5)
public class BoxSeeder implements CommandLineRunner {
    @Autowired
    private BoxService boxService;
    @Autowired
    private BoxRepository boxRepository;
    @Autowired
    private BeerRepository beerRepository;

    @Override
    public void run(String... args) throws Exception {
        if (boxRepository.count() > 0) return;

        List<Beer> beers = this.beerRepository.findAll();

        if (beers.isEmpty()) return;

        List<Box> boxes = List.of(
                new Box("European Discovery Box", "A curated selection of diverse European craft beers.", 39.99, randomTags(1, 4), randomBeers(10, 20)),
                new Box("Dark & Bold Box", "Rich, dark, and intense beers for seasoned enthusiasts.", 29.99, randomTags(1, 4), randomBeers(15, 20)),
                new Box("Hop Lovers Box", "Hoppy ales and IPAs bursting with aroma.", 29.99, randomTags(1, 4), randomBeers(15, 20)),
                new Box("Lager Classics Box", "Clean, crisp, and refreshing lager styles.", 19.99, randomTags(1, 4), randomBeers(20, 30)),
                new Box("Regional Favorites Box", "Beers inspired by distinct European regions.", 59.99, randomTags(4, 6), randomBeers(6, 10))
        );

        this.boxRepository.saveAll(boxes);
    }

    private Set<Beer> randomBeers(int min, int max) {
        List<Beer> beers = new ArrayList<>(beerRepository.findAll());
        Collections.shuffle(beers);

        int count = ThreadLocalRandom.current().nextInt(min, max + 1);
        return beers.stream()
                .limit(count)
                .collect(Collectors.toSet());
    }

    private Set<BoxTag> randomTags(int min, int max) {
        List<BoxTag> tags = new ArrayList<>(Arrays.stream(BoxTag.values())
                .filter(tag -> tag != BoxTag.NONE)
                .toList());

        Collections.shuffle(tags);

        int count = ThreadLocalRandom.current().nextInt(min, max + 1);
        return tags.stream()
                .limit(count)
                .collect(Collectors.toSet());
    }
}
