package ru.make.account.core.arving.web.mapper;

import org.mapstruct.Mapper;
import ru.make.account.core.arving.model.OperationTag;
import ru.make.account.core.arving.web.dto.operation.OperationTagDto;

@Mapper
public interface OperationTagMapper {
    OperationTag toEntity(OperationTagDto dto);

    OperationTagDto toDto(OperationTag entity);
}
