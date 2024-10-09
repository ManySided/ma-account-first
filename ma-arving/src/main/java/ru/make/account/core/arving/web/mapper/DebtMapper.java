package ru.make.account.core.arving.web.mapper;

import org.mapstruct.Mapper;
import ru.make.account.core.arving.model.debt.Debt;
import ru.make.account.core.arving.model.debt.DebtOperation;
import ru.make.account.core.arving.web.dto.debt.DebtDto;
import ru.make.account.core.arving.web.dto.debt.DebtOperationDto;

@Mapper
public interface DebtMapper {
    DebtDto toDto(Debt entity);
    Debt toEntity(DebtDto dto);

    DebtOperationDto toDto(DebtOperation entity);
}
