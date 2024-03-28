package ru.make.account.core.arving.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.make.account.core.arving.web.dto.info.InfoDto;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/public")
public class HomeController {
    @GetMapping("/info")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok(InfoDto.builder()
                .projectName("Arving")
                .version("0.0.1")
                .now(LocalDateTime.now())
                .build());
    }
}
