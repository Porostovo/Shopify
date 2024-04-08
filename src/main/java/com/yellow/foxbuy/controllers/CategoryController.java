package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.services.interfaces.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/category")
    @Operation(summary = "List categories", description = "Without parameter - lists categories with atleast one ad, with parameter empty = 'true' lists all categories (even empty)")
    @ApiResponse(responseCode = "200", description = "List of categories shown.")
    @ApiResponse(responseCode = "400", description = "Invalid parameter - empty can be only 'true' or empty.")
    public ResponseEntity<?> getAllCategories(@RequestParam (required = false) String empty) {
        Map<String, String> error = new HashMap<>();
        if (Objects.equals(empty, "true")) return ResponseEntity.status(200).body(categoryService.getAllCategories());
        else if (empty == null) return ResponseEntity.status(200).body(categoryService.getAllCategoriesWithAtleastOneAd());
        else {
            error.put("error", "Invalid parameter");
            return ResponseEntity.status(400).body(error);
        }
    }
}
