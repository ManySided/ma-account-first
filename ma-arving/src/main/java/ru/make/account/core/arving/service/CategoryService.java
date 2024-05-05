package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.repository.CategoryRepository;
import ru.make.account.core.arving.web.dto.operation.CategoryDto;
import ru.make.account.core.arving.web.mapper.CategoryMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    public CategoryDto getCategoryById(Long request) {
        return categoryRepository.findById(request)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new ProcessException("Категория не найдена"));
    }
}
