package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.CategoryDTO;

import java.util.List;

public interface CategoryService {
    boolean isCategoryNameUnique(String name);

    Category save(Category category);

    boolean categoryIdExists(Long id);

    void deleteCategory(Long id);

    String findNameById(Long id);

    Category updateCategory(Long id, Category category);

    List<Category> getCategories();

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getAllCategoriesWithAtleastOneAd();
}
