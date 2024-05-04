package ru.make.account.core.arving.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.make.account.core.arving.model.Operation;
import ru.make.account.core.arving.web.dto.operation.OperationDto;

@Mapper
public interface OperationMapper {
    @Mapping(target = "categoryId", source = "category.id")
    Operation toEntity(OperationDto dto);

    @Mapping(target = "category.id", source = "categoryId")
    OperationDto toDto(Operation entity);
}
