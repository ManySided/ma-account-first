package ru.make.account.core.arving.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import ru.make.account.core.arving.exception.ProcessException;

import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityHandler {
    public UUID getUserId() {
        Jwt defaultUser = (Jwt) Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .orElseThrow(() -> new ProcessException("Пользователь отсутствует"));

        return UUID.fromString(defaultUser.getSubject());
    }
}
