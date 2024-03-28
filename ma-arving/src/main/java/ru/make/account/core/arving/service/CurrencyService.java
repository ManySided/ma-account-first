package ru.make.account.core.arving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.make.account.core.arving.exception.ProcessException;
import ru.make.account.core.arving.repository.CurrencyRepository;
import ru.make.account.core.arving.web.dto.currency.CurrencyDto;
import ru.make.account.core.arving.web.mapper.CurrencyMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public CurrencyDto getCurrency(Long currencyId) {
        log.info("получение валюты по ID [{}]", currencyId);
        var currency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ProcessException(String.format("Валюта [%s] не найдена", currencyId)));
        return currencyMapper.toDto(currency);
    }
}
