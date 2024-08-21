package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.make.account.core.arving.service.CategoryService;
import ru.make.account.core.arving.web.dto.category.DeleteCategoryDto;
import ru.make.account.core.arving.web.dto.operation.CategoryDto;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/service/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/getTreeCategories")
    public ResponseEntity<List<CategoryDto>> getTreeCategories(Long request) {
        return ResponseEntity.ok(categoryService.getCategoryTreeByAccountId(request));
    }

    @GetMapping("/getCategories")
    public ResponseEntity<List<CategoryDto>> getCategories(Long request) {
        return ResponseEntity.ok(categoryService.getCategoriesByAccountId(request));
    }

    @PostMapping
    public ResponseEntity<?> saveCategory(CategoryDto request) {
        return ResponseEntity.ok(categoryService.saveCategory(request));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCategory(DeleteCategoryDto request) {
        categoryService.deleteCategory(request.getCategoryId(), request.getReserveCategoryId());
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

}
