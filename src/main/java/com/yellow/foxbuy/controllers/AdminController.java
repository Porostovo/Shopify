package com.yellow.foxbuy.controllers;


import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.CategDTO;
import com.yellow.foxbuy.services.CategoryService;
import com.yellow.foxbuy.services.ErrorsHandling;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Create new Category", description = "Create new Category with name and description.")
    @ApiResponse(responseCode = "200", description = "Category created.")
    @ApiResponse(responseCode = "400", description = "Invalid input or Category name already exists.")
    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategDTO categDTO,BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        if(!categoryService.isCategoryNameUnique(categDTO.getName())){
            result.put("error", "This category name is already taken.");
            return ResponseEntity.status(400).body(result);
        }
        CategDTO categDTO1 = categoryService.save(categDTO);
        result.put("name", categDTO1.getName());
        result.put("description", categDTO1.getDescription());
        result.put("id", categDTO1.getId());
        return ResponseEntity.status(200).body(result);
    }
    @Operation(summary = "Update Category", description = "Update Category {id} with name and description.")
    @ApiResponse(responseCode = "200", description = "Category Updated.")
    @ApiResponse(responseCode = "400", description = "Invalid input.")
    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody CategDTO categDTO, BindingResult bindingResult,
                                            @PathVariable Long id )  {

        Map<String, Object> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        if(!categoryService.categoryIdExists(id)){
            result.put("error", "This category id doesn't exist.");
            return ResponseEntity.status(400).body(result);
        }
        String name = categoryService.findNameById(id);
        System.out.println(name);
        System.out.println(categDTO.getName());
        if(!categoryService.isCategoryNameUnique(categDTO.getName()) && !name.equals(categDTO.getName()) ){
            result.put("error", "This category name is already taken.");
            return ResponseEntity.status(400).body(result);
        }
        CategDTO categDTO1 = categoryService.updateCategory(id, categDTO);
        result.put("name", categDTO1.getName());
        result.put("description", categDTO1.getDescription());
        result.put("id", categDTO1.getId());
        return ResponseEntity.status(200).body(result);
    }
    @Operation(summary = "Delete Category", description = "Delete Category {id}.")
    @ApiResponse(responseCode = "200", description = "Category Deleted")
    @ApiResponse(responseCode = "400", description = "Unable to delete this category with this Id does not exists")
    @DeleteMapping ("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id)  {
        Map<String, Object> result = new HashMap<>();
        if(!categoryService.categoryIdExists(id)){
            result.put("error", "This category id doesn't exist.");
            return ResponseEntity.status(400).body(result);
        }
        Category category = categoryService.findCategoryById(id);
        if(category.getName().equals("Uncategorized")){
            result.put("error", "This category is not possible to delete");
            return ResponseEntity.status(400).body(result);
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.status(200).body("Category was deleted.");
    }
}



