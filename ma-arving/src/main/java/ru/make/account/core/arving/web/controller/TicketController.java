package ru.make.account.core.arving.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.make.account.core.arving.service.TicketService;
import ru.make.account.core.arving.web.dto.ticket.TicketDto;
import ru.make.account.core.arving.web.dto.ticket.TicketFilterDto;

@RestController
@CrossOrigin
@RequestMapping("/api/service/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody @Valid TicketDto request) {
        return ResponseEntity.ok(ticketService.saveTicket(request));
    }

    @PostMapping("/filter")
    public ResponseEntity<?> getTicketsByFilter(@RequestBody TicketFilterDto request) {
        return ResponseEntity.ok(ticketService.getTicketsGroupByDay(request));
    }

    @DeleteMapping("/operation")
    public ResponseEntity<?> removeOperation(@RequestParam Long operationId) {
        ticketService.removeOperationOfTicket(operationId);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }
}
