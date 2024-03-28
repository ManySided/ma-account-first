package ru.make.account.core.arving.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.make.account.core.arving.security.SecurityHandler;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final SecurityHandler securityHandler;

    @GetMapping("/currentUser")
    public ResponseEntity<?> getUser() {
        log.info("запрос пользователя");
        var userId = securityHandler.getUserId();
        log.info("получен пользователь [{}]", userId);
        return ResponseEntity.ok(userId);
    }
}
