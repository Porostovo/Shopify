package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.CategDTO;
import com.yellow.foxbuy.models.DTOs.CategoryDTO;

import java.util.List;

public interface CategoryService {
    boolean isCategoryNameUnique(String name);

    CategDTO save(CategDTO categDTO);

    boolean categoryIdExists(Long id);

    void deleteCategory(Long id);

    String findNameById(Long id);

    CategDTO updateCategory(Long id, CategDTO categDTO);


    Category findCategoryById (Long id);

    List<Category> getCategories();

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getAllCategoriesWithAtleastOneAd();
}
