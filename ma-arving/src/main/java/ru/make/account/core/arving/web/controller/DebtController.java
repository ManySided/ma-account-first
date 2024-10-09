package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.make.account.core.arving.service.DebtService;
import ru.make.account.core.arving.web.dto.debt.DebtDto;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/service/debt")
@RequiredArgsConstructor
public class DebtController {
    private final DebtService debtService;

    @GetMapping("/all")
    public ResponseEntity<List<DebtDto>> getAllActiveDebsByAccountId(Long request) {
        return ResponseEntity.ok(debtService.getActiveDebts(request));
    }
}
