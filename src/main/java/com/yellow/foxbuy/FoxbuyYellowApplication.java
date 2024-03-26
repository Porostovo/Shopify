package com.yellow.foxbuy;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoxbuyYellowApplication implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Autowired
    public FoxbuyYellowApplication(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(FoxbuyYellowApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        categoryRepository.save(new Category("Beverage", "Buy some good beer."));
        categoryRepository.save(new Category("Nourishment", "Buy some good beef."));
    }
}
