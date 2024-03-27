package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.services.CategoryService;
import com.yellow.foxbuy.services.ErrorsHandling;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminController {
    private final CategoryService categoryService;

    @Autowired
    public AdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody Category category,BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("id " + category.getId() +category.getName()+category.getDescription());

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        if(!categoryService.isCategoryNameUnique(category.getName())){
            result.put("error", "This category name is already taken.");
            return ResponseEntity.status(400).body(result);
        }
        Category category1 = categoryService.save(category);
        result.put("name", category1.getName());
        result.put("description", category1.getDescription());
        result.put("id", category1.getId());
        return ResponseEntity.status(200).body(result);
    }
    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody Category category,
                                            @PathVariable Long id,
                                            BindingResult bindingResult)  {

        Map<String, Object> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        if(!categoryService.categoryIdExists(id)){
            result.put("error", "This category id doesn't exist.");
            return ResponseEntity.status(400).body(result);
        }
        String name = categoryService.findNameById(id);
        if(!categoryService.isCategoryNameUnique(category.getName()) && !name.equals(category.getName()) ){
            result.put("error", "This category name is already taken.");
            return ResponseEntity.status(400).body(result);
        }
        Category category1 = categoryService.updateCategory(id, category);
        result.put("name", category1.getName());
        result.put("description", category1.getDescription());
        result.put("id", category1.getId());
        return ResponseEntity.status(200).body(result);
    }
    @DeleteMapping ("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id)  {
        Map<String, Object> result = new HashMap<>();
        System.out.println("id " + id);
        if(!categoryService.categoryIdExists(id)){
            result.put("error", "This category id doesn't exist.");
            return ResponseEntity.status(400).body(result);
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.status(200).body("Category was deleted.");
    }
}



