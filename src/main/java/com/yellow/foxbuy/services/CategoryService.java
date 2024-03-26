package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Category;

public interface CategoryService {
    boolean isCategoryNameUnique(String name);

    Category save(Category category);

    boolean categoryIdExists(Long id);

    void deleteCategory(Long id);

    String findNameById(Long id);

    Category updateCategory(Long id, Category category);

    Category findCategoryById (Long id);
}
