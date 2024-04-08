package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.make.account.core.arving.service.CurrencyService;
import ru.make.account.core.arving.web.dto.currency.CurrencyDto;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/service/dict/currency")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/list")
    public ResponseEntity<List<CurrencyDto>> getList() {
        return ResponseEntity.ok(currencyService.getAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CurrencyDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(currencyService.getCurrency(id));
    }
}
