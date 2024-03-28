package ru.make.account.core.arving.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.make.account.core.arving.model.Account;
import ru.make.account.core.arving.web.dto.account.AccountDto;

@Mapper
public interface AccountMapper {
    @Mapping(target = "currencyId", source = "currency.id")
    @Mapping(target = "accountCreator", ignore = true)
    Account toEntity(AccountDto dto);

    @Mapping(target = "currency.id", source = "currencyId")
    AccountDto toDto(Account entity);
}
