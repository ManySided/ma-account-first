package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.make.account.core.arving.service.OperationSearchService;

@RestController
@CrossOrigin
@RequestMapping("/api/service/operation")
@RequiredArgsConstructor
public class OperationController {
    private final OperationSearchService operationSearchService;

    @GetMapping("/findGroupOperationsByName")
    public ResponseEntity<?> findGroupOperations(@RequestParam Long accountId, @RequestParam String name) {
        return ResponseEntity.ok(operationSearchService.findGroupOperationsByName(name, accountId));
    }

    @GetMapping("/findLastOperationByName")
    public ResponseEntity<?> findLastOperationByName(@RequestParam Long accountId, @RequestParam String name) {
        return ResponseEntity.ok(operationSearchService.findLastOperationByName(name, accountId));
    }
}
