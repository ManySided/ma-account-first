package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.make.account.core.arving.service.CategoryService;
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
}
