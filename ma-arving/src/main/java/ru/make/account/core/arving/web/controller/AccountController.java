package ru.make.account.core.arving.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.make.account.core.arving.service.AccountService;
import ru.make.account.core.arving.web.dto.account.AccountDto;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/service/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/getAccounts")
    public ResponseEntity<List<AccountDto>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

    @GetMapping("/getAccountById")
    public ResponseEntity<AccountDto> getAccountById(Long request) {
        return ResponseEntity.ok(accountService.getAccount(request));
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountDto request) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    @PutMapping
    public ResponseEntity<?> updateAccount(@RequestBody AccountDto request) {
        return ResponseEntity.ok(accountService.updateAccount(request));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAccount(@RequestParam Long accountId) {
        return ResponseEntity.ok(accountService.deleteAccount(accountId));
    }
}
