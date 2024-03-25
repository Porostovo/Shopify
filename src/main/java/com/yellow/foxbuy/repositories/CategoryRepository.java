package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
