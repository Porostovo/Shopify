package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findFirstByName(String name);
    Category findFirstById(Long id);
    Category findFirstByOrderByIdDesc();
}
