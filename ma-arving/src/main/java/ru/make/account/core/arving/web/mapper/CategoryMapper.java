package ru.make.account.core.arving.web.mapper;

import org.mapstruct.Mapper;
import ru.make.account.core.arving.model.Category;
import ru.make.account.core.arving.web.dto.category.CategoryDto;

@Mapper
public interface CategoryMapper {
    Category toEntity(CategoryDto dto);

    CategoryDto toDto(Category entity);
}
