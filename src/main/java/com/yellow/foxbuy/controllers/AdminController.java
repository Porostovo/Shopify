package com.yellow.foxbuy.controllers;


import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.BanDTO;
import com.yellow.foxbuy.models.DTOs.CategDTO;
import com.yellow.foxbuy.services.BanService;
import com.yellow.foxbuy.services.CategoryService;
import com.yellow.foxbuy.services.ErrorsHandling;
import com.yellow.foxbuy.services.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class AdminController {
    private final CategoryService categoryService;
    private final BanService banService;
    private final LogService logService;

    @Autowired
    public AdminController(CategoryService categoryService,
                           BanService banService,
                           LogService logService) {
        this.categoryService = categoryService;
        this.banService = banService;
        this.logService = logService;
    }

    @Operation(summary = "Create new Category", description = "Create new Category with name and description.")
    @ApiResponse(responseCode = "200", description = "Category created.")
    @ApiResponse(responseCode = "400", description = "Invalid input or Category name already exists.")
    @PostMapping("/category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategDTO categDTO, BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            logService.addLog("POST /category", "ERROR", categDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        if (!categoryService.isCategoryNameUnique(categDTO.getName())) {
            result.put("error", "This category name is already taken.");
            logService.addLog("POST /category", "ERROR", categDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        CategDTO categDTO1 = categoryService.save(categDTO);
        logService.addLog("POST /category", "INFO", categDTO.toString());
        return ResponseEntity.status(200).body(categDTO1);
    }

    @Operation(summary = "Update Category", description = "Update Category {id} with name and description.")
    @ApiResponse(responseCode = "200", description = "Category Updated.")
    @ApiResponse(responseCode = "400", description = "Invalid input.")
    @PutMapping("/category/{id}")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody CategDTO categDTO, BindingResult bindingResult,
                                            @PathVariable Long id) {

        Map<String, Object> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            logService.addLog("PUT /category/{id}", "ERROR", categDTO.toString());
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        if (!categoryService.categoryIdExists(id)) {
            result.put("error", "This category id doesn't exist.");
            logService.addLog("PUT /category/{id}", "ERROR", categDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        String name = categoryService.findNameById(id);
        if (!categoryService.isCategoryNameUnique(categDTO.getName()) && !name.equals(categDTO.getName())) {
            result.put("error", "This category name is already taken.");
            logService.addLog("PUT /category/{id}", "ERROR", categDTO.toString());
            return ResponseEntity.status(400).body(result);
        }
        CategDTO categDTO1 = categoryService.updateCategory(id, categDTO);
        logService.addLog("PUT /category/{id}", "INFO", categDTO.toString());
        return ResponseEntity.status(200).body(categDTO1);
    }

    @Operation(summary = "Delete Category", description = "Delete Category {id}.")
    @ApiResponse(responseCode = "200", description = "Category Deleted")
    @ApiResponse(responseCode = "400", description = "Unable to delete this category with this Id does not exists")
    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        if (!categoryService.categoryIdExists(id)) {
            result.put("error", "This category id doesn't exist.");
            logService.addLog("DELETE /category/{id}", "ERROR", "id = " + id);
            return ResponseEntity.status(400).body(result);
        }
        Category category = categoryService.findCategoryById(id);
        if (category.getName().equals("Uncategorized")) {
            result.put("error", "This category is not possible to delete");
            logService.addLog("DELETE /category/{id}", "ERROR", "id = " + id);
            return ResponseEntity.status(400).body(result);
        }
        categoryService.deleteCategory(id);
        result.put("message", "Category was deleted.");
        logService.addLog("DELETE /category/{id}", "INFO", "id = " + id);
        return ResponseEntity.status(200).body(result);
    }

    @Operation(summary = "Get list of logs", description = "Get list of logs by date, only admin can see this.")
    @ApiResponse(responseCode = "200", description = "Logs successfully shown.")
    @ApiResponse(responseCode = "400", description = "No logs in chosen date.")
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDateTime startOfTheDay = date.atStartOfDay();
        LocalDateTime endOfTheDay = date.atTime(23, 59, 59);
        if (logService.findAllByDate(startOfTheDay, endOfTheDay).isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No logs on this day.");
            logService.addLog("GET /logs", "ERROR", "date = " + date);
            return ResponseEntity.status(400).body(error);
        } else {
            logService.addLog("GET /logs", "INFO", "date = " + date);
            return ResponseEntity.status(200).body(logService.findAllByDate(startOfTheDay, endOfTheDay));
        }
    }

    @Operation(summary = "Ban user", description = "Ban user with uuid: {id}.")
    @ApiResponse(responseCode = "200", description = "User banned, ads hidden")
    @ApiResponse(responseCode = "400", description = "User not banned, ads not hidden")
    @PostMapping("/user/{id}/ban")
    public ResponseEntity<?> banUser(@Valid @RequestBody BanDTO banDTO, BindingResult bindingResult,
                                     @PathVariable UUID id) {
        if (bindingResult.hasErrors()) {
            return ErrorsHandling.handleValidationErrors(bindingResult);
        }
        Map<String,String> result = banService.banUser(id, banDTO.getDuration());
        if (result.containsKey("error")){
            return  ResponseEntity.status(400).body(result);
        }
        return  ResponseEntity.status(200).body(result);
    }
}



