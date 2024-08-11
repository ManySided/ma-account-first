package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.repository.CategoryRepository;
import ru.make.account.core.arving.web.dto.operation.CategoryDto;
import ru.make.account.core.arving.web.mapper.CategoryMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final AccountService accountService;

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    public CategoryDto getCategoryById(Long request) {
        var result = categoryRepository.findById(request)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new ProcessException("Категория не найдена"));
        accountService.checkAccessToAccount(result.getAccountId());
        return result;
    }

    public List<CategoryDto> getCategoriesByAccountId(Long request) {
        accountService.checkAccessToAccount(request);
        return categoryRepository.findByAccountIdOrderByName(request).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getCategoryTreeByAccountId(Long request) {
        accountService.checkAccessToAccount(request);
        var upCategories = categoryRepository
                .findByAccountIdAndParentIsNullOrderByName(request)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        for (CategoryDto upCategory : upCategories) {
            var subCategories = categoryRepository.findByParentOrderByName(upCategory.getId())
                    .stream()
                    .map(categoryMapper::toDto)
                    .collect(Collectors.toList());
            upCategory.setSubCategories(subCategories);
        }
        return upCategories;
    }

    public List<CategoryDto> getCategory(Long accountId, String name) {
        accountService.checkAccessToAccount(accountId);
        return categoryRepository.findByAccountIdAndNameAndStuffFlagIsFalse(accountId, name).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
