package com.yellow.foxbuy;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoxbuyYellowApplication implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;

    @Autowired
    public FoxbuyYellowApplication(CategoryRepository categoryRepository, AdRepository adRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(FoxbuyYellowApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        Category nourishmentCategory = new Category("Nourishment", "Buy some good beef.");

        categoryRepository.save(beverageCategory);
        categoryRepository.save(nourishmentCategory);

        User user = new User();
        userRepository.save(user);
        User user1 = new User("user", "email@email.com", "Password123%");
        userRepository.save(user1);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user1, beverageCategory);
        Ad ad1 = new Ad("Budweiser", "Good beer.", 2000.00, "23456", user1, beverageCategory);
        adRepository.save(ad);
        adRepository.save(ad1);

        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, nourishmentCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1000, "12345", user1, beverageCategory));
    }
}
