package com.yellow.foxbuy;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.repositories.CategoryRepository;
import com.yellow.foxbuy.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoxbuyYellowApplication implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public FoxbuyYellowApplication(CategoryRepository categoryRepository, RoleRepository roleRepository) {
        this.categoryRepository = categoryRepository;
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(FoxbuyYellowApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        categoryRepository.save(new Category("Beverage", "Buy some good beer."));
        categoryRepository.save(new Category("Nourishment", "Buy some good beef."));
        roleRepository.save(new Role("ROLE_USER"));
        roleRepository.save(new Role("ROLE_VIP_USER"));
        roleRepository.save(new Role("ROLE_ADMIN"));


    }
}
