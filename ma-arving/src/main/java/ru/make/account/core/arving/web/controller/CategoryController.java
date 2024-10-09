package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.make.account.core.arving.service.CategoryService;
import ru.make.account.core.arving.web.dto.category.CategoryDto;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/service/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/getTreeCategories")
    public ResponseEntity<List<CategoryDto>> getTreeCategories(
            @RequestParam Long accountId,
            @RequestParam(required = false) Boolean showRelevantCategory) {
        return ResponseEntity.ok(
                categoryService.getCategoryTreeByAccountId(accountId, showRelevantCategory)
        );
    }

    @GetMapping("/getCategories")
    public ResponseEntity<List<CategoryDto>> getCategories(Long request) {
        return ResponseEntity.ok(categoryService.getCategoriesByAccountId(request));
    }

    @PostMapping
    public ResponseEntity<?> saveCategory(@RequestBody CategoryDto request) {
        return ResponseEntity.ok(categoryService.saveCategory(request));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCategory(
            @RequestParam Long categoryId,
            @RequestParam(required = false) Long reserveCategoryId) {
        categoryService.deleteCategory(categoryId, reserveCategoryId);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

}
