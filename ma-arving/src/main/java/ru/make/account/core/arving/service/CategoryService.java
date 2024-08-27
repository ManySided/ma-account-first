package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.model.Category;
import ru.make.account.core.arving.repository.CategoryRepository;
import ru.make.account.core.arving.repository.OperationRepository;
import ru.make.account.core.arving.web.dto.category.CategoryDto;
import ru.make.account.core.arving.web.mapper.CategoryMapper;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final AccountService accountService;
    private final OperationRepository operationRepository;

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    public CategoryDto getCategoryById(Long request) {
        var result = categoryRepository.findByIdAndFlagActivityTrue(request)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new ProcessException("Категория не найдена"));
        accountService.checkAccessToAccount(result.getAccountId());
        return result;
    }

    public List<CategoryDto> getCategoriesByAccountId(Long request) {
        accountService.checkAccessToAccount(request);
        return categoryRepository.findByAccountIdAndFlagActivityTrueOrderByName(request).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getCategoryTreeByAccountId(Long request) {
        accountService.checkAccessToAccount(request);
        var upCategories = categoryRepository
                .findByAccountIdAndParentIsNullAndFlagActivityTrueOrderByName(request)
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        for (CategoryDto upCategory : upCategories) {
            var subCategories = categoryRepository.findByParentAndFlagActivityTrueOrderByName(upCategory.getId())
                    .stream()
                    .map(categoryMapper::toDto)
                    .collect(Collectors.toList());
            upCategory.setSubCategories(subCategories);
        }
        return upCategories;
    }

    public List<CategoryDto> getCategory(Long accountId, String name) {
        accountService.checkAccessToAccount(accountId);
        return categoryRepository.findByAccountIdAndNameAndStuffFlagFalseAndFlagActivityTrue(accountId, name).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public Long saveCategory(CategoryDto request) {
        log.info("сохранение категории");
        Category category = new Category();
        if (nonNull(request.getId())) {
            category = categoryRepository
                    .findById(request.getId())
                    .orElseThrow(() -> new ProcessException("Категория не найдена"));
            accountService.checkAccessToAccount(category.getAccountId());
            if (!category.getFlagActivity())
                throw new ProcessException("Категория \"" + category.getName() + "\" уже удалена");
        } else category.setFlagActivity(Boolean.TRUE);

        category.setName(request.getName());
        category.setAccountId(request.getAccountId());
        category.setParent(request.getParent());
        category.setStuffFlag(nonNull(request.getStuffFlag())
                ? request.getStuffFlag()
                : Boolean.FALSE);
        category.setName(request.getName());
        var response = categoryRepository.save(category);

        return response.getId();
    }

    /**
     * @param categoryId        ID удаляемой категории
     * @param reserveCategoryId ID категории на которую будут перемещены операции
     */
    @Transactional
    public void deleteCategory(Long categoryId, Long reserveCategoryId) {
        log.info("удаление категории [{}]", categoryId);
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ProcessException("Категория не найдена"));
        accountService.checkAccessToAccount(category.getAccountId());

        log.info("проверка, что категория [{}] активная", categoryId);
        if (!category.getFlagActivity())
            throw new ProcessException("Категория \"" + category.getName() + "\" уже удалена");

        log.info("проверка, что категория [{}] не имеет операции", categoryId);
        var foundActivityOperationIds = operationRepository.findActivityOperationIdsByCategoryId(categoryId);
        log.info("по категории [{}] найдено активных товаров [{}]", categoryId, foundActivityOperationIds.size());

        if (isNull(reserveCategoryId) && foundActivityOperationIds.size() > 0)
            throw new ProcessException("Невозможно удалить категорию, т.к. на ней записаны операции, а резервной категории не указано");

        if (nonNull(reserveCategoryId)) {
            if (categoryId.equals(reserveCategoryId))
                throw new ProcessException("Удаляемая и резервная категория не могут быть одинаковыми");
            log.info("перенос операций с категории [{}] на замещающую категорию [{}]", categoryId, reserveCategoryId);
            var reserveCategory = categoryRepository.findById(reserveCategoryId)
                    .orElseThrow(() -> new ProcessException("замещающая категория не найдена"));
            accountService.checkAccessToAccount(reserveCategory.getAccountId());
            foundActivityOperationIds
                    .forEach(operationId -> operationRepository.updateCategoryByOperationId(operationId, reserveCategoryId));
            log.info("операции перенесены на замещающую категорию [{}]", reserveCategoryId);
        }

        log.info("удаление категории [{}]", categoryId);
        category.setFlagActivity(Boolean.FALSE);
        categoryRepository.save(category);
    }
}
