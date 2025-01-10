package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.make.account.core.arving.service.OperationTagService;
import ru.make.account.core.arving.web.dto.operation.OperationTagDto;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/service/tag")
@RequiredArgsConstructor
public class OperationTagController {
    private final OperationTagService operationTagService;

    @GetMapping("/all")
    public ResponseEntity<List<OperationTagDto>> getCategories(Long request) {
        return ResponseEntity.ok(operationTagService.getTags(request));
    }
}
