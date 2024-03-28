package ru.make.account.core.arving.web.mapper;

import org.mapstruct.Mapper;
import ru.make.account.core.arving.model.Currency;
import ru.make.account.core.arving.web.dto.currency.CurrencyDto;

@Mapper
public interface CurrencyMapper {
    CurrencyDto toDto(Currency entity);
}
