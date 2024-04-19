package com.yellow.foxbuy;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;

import com.yellow.foxbuy.models.Role;

import com.yellow.foxbuy.repositories.RoleRepository;

import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
import com.yellow.foxbuy.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class FoxbuyYellowApplication implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public FoxbuyYellowApplication(CategoryRepository categoryRepository, AdRepository adRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.categoryRepository = categoryRepository;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(FoxbuyYellowApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Role roleUser = roleRepository.save(new Role("ROLE_USER"));
        Role roleVipUser = roleRepository.save(new Role("ROLE_VIP"));
        Role roleAdmin = roleRepository.save(new Role("ROLE_ADMIN"));

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        Category nourishmentCategory = new Category("Nourishment", "Buy some good beef.");


        categoryRepository.save(beverageCategory);
        categoryRepository.save(nourishmentCategory);

        User user1 = new User("JohnUSER",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("password"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user1.setVerified(true);
        userRepository.save(user1);

        User user2 = new User("JohnADMIN",
                "emailA@email.com",
                SecurityConfig.passwordEncoder().encode("password"),
                new HashSet<>(Collections.singletonList(roleAdmin)));
        user2.setVerified(true);
        userRepository.save(user2);

        User user3 = new User("JohnVIP",
                "emailA@email.com",
                SecurityConfig.passwordEncoder().encode("password"),
                new HashSet<>(Collections.singletonList(roleVipUser)));
        user3.setVerified(true);
        userRepository.save(user3);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user1, beverageCategory);
        Ad ad1 = new Ad("Budweiser", "Good beer.", 2000.00, "23456", user1, beverageCategory);
        adRepository.save(ad);
        adRepository.save(ad1);

        adRepository.save(new Ad("Ad1","Description1", 1001, "12345", user2, nourishmentCategory));
        adRepository.save(new Ad("Ad1","Description1", 1002, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1003, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1004, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1005, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1006, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1007, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1008, "12345", user2, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1009, "12345", user2, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1010, "12345", user2, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1011, "12345", user2, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1012, "12345", user2, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1013, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1014, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1015, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1016, "12345", user1, beverageCategory));
        adRepository.save(new Ad("Ad1","Description1", 1017, "12345", user1, beverageCategory));

        for (int i = 0; i < 10; i++) {
            User user = new User("test_user",
                    "email@email.com",
                    SecurityConfig.passwordEncoder().encode("password"),
                    new HashSet<>(Collections.singletonList(roleUser)));
            user1.setVerified(true);
            userRepository.save(user);
        }
        System.out.println();
        System.out.println("username: JohnUSER      id: "+user1.getId());
        System.out.println("username: JohnADMIN     id: "+user2.getId());
        System.out.println();
    }
}
