package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final AdRepository adRepository;

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository, AdRepository adRepository) {
        this.categoryRepository = categoryRepository;
        this.adRepository = adRepository;
    }

    @Override
    public boolean isCategoryNameUnique(String name) {
        return (categoryRepository.findFirstByName(name) == null);
    }

    @Override
    public Category save(Category category) {
        if(category.getId()==null) {
            return categoryRepository.save(new Category(category.getName(), category.getDescription()));
        }else {
            return categoryRepository.save(category);
        }
    }

    @Override
    public boolean categoryIdExists(Long id) {
        return categoryRepository.findById(id).isPresent();
    }

    @Override
    public void deleteCategory(Long id) {
        List<Ad> adList = adRepository.findAllByCategoryId(id);

        if (categoryRepository.findFirstByName("Uncategorized") == null) {
            Category category = new Category("Uncategorized", "N/A");
            categoryRepository.save(category);
        }
        Category category = categoryRepository.findFirstByName("Uncategorized");

        adList.stream().forEach(ad -> {
            ad.setCategory(category);
            adRepository.save(ad);
        });
       Category category1 =  categoryRepository.findFirstById(id);
        categoryRepository.delete(category1);
    }

    @Override
    public String findNameById(Long id) {
        return categoryRepository.findFirstById(id).getName();
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Category upDateCat = categoryRepository.findFirstById(id);
        upDateCat.setName(category.getName());
        upDateCat.setDescription(category.getDescription());
        return categoryRepository.save(upDateCat);
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findFirstById(id);
    }

}
