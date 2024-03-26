package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/category")
    public ResponseEntity<?> getAllCategories(@RequestParam (required = false) String empty) {
        if (Objects.equals(empty, "true")) return ResponseEntity.status(200).body(categoryService.getAllCategories());
        else return ResponseEntity.status(200).body(categoryService.getAllCategoriesWithAtleastOneAd());
    }
}
