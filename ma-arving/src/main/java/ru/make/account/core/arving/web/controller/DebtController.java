package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.make.account.core.arving.service.DebtService;
import ru.make.account.core.arving.web.dto.IdRequestDto;
import ru.make.account.core.arving.web.dto.debt.DebtDto;
import ru.make.account.core.arving.web.dto.debt.DebtOperationCreateRequestDto;
import ru.make.account.core.arving.web.dto.debt.DebtOperationDto;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;

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

    @GetMapping
    public ResponseEntity<DebtDto> getDebt(@RequestParam Long id) {
        return ResponseEntity.ok(debtService.getDebt(id));
    }

    @PostMapping
    public ResponseEntity<Long> createDebt(@RequestBody DebtDto request) {
        return ResponseEntity.ok(debtService.createDebt(request));
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeDebt(@RequestBody IdRequestDto request) {
        debtService.closeDebt(DebtDto.builder()
                .id(request.getId())
                .build());
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteDebt(@RequestParam Long id) {
        debtService.deleteDebt(id);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @PostMapping("/operation")
    public ResponseEntity<?> addDebtOperation(@RequestBody DebtOperationCreateRequestDto request) {
        debtService.addDebtOperation(DebtOperationDto.builder()
                .debtId(request.getDebtId())
                .ticket(TicketDto.builder()
                        .date(request.getDate())
                        .totalSum(request.getSum())
                        .build())
                .build());
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }
}
