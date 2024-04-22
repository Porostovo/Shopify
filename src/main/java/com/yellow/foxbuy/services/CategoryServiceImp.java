package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.CategDTO;
import com.yellow.foxbuy.models.DTOs.CategoryDTO;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final AdRepository adRepository;

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository,
                              AdRepository adRepository) {
        this.categoryRepository = categoryRepository;
        this.adRepository = adRepository;
    }

    @Override
    public boolean isCategoryNameUnique(String name) {
        return (categoryRepository.findFirstByName(name) == null);
    }

    public CategDTO save(CategDTO categDTO) {
        Category category = categoryRepository.save(new Category(categDTO.getName(), categDTO.getDescription()));
        categDTO.setId(category.getId());
        return categDTO;
    }

    @Override
    public boolean categoryIdExists(Long id) {
        return categoryRepository.findById(id).isPresent();
    }

    @Override
    public void deleteCategory(Long id) {
        List<Ad> adList = adRepository.findAllByCategoryId(id);

        Category category = categoryRepository.findFirstByName("Uncategorized");
        if (category == null) {
            category = new Category("Uncategorized", "N/A");
            category = categoryRepository.save(category);
        }

        Category finalCategory = category;
        adList.forEach(ad -> {
            ad.setCategory(finalCategory);
            adRepository.save(ad);
        });
        Category category1 = categoryRepository.findFirstById(id);
        categoryRepository.delete(category1);
    }

    @Override
    public String findNameById(Long id) {
        return categoryRepository.findFirstById(id).getName();
    }

    @Override
    public CategDTO updateCategory(Long id, CategDTO categDTO) {
        Category upDateCat = categoryRepository.findFirstById(id);
        upDateCat.setName(categDTO.getName());
        upDateCat.setDescription(categDTO.getDescription());
        Category category = categoryRepository.save(upDateCat);
        categDTO.setId(category.getId());
        return categDTO;
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findFirstById(id);
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> listOfCategories = new ArrayList<>();
        for (Category category : getCategories()) {
            listOfCategories.add(loadCategories(category));
        }
        return listOfCategories;
    }

    @Override
    public List<CategoryDTO> getAllCategoriesWithAtleastOneAd() {
        List<CategoryDTO> listOfCategories = new ArrayList<>();
        for (Category category : getCategories()) {
            if (!category.getAds().isEmpty()) {
                listOfCategories.add(loadCategories(category));
            }
        }
        return listOfCategories;
    }

    private static CategoryDTO loadCategories(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setAds(category.getAds().size());
        return categoryDTO;
    }
}
