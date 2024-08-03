package ru.make.account.load.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TicketDto {
    private Long id;
    private TicketDirectionEnum ticketDirection;
    private LocalDate date;
    private Long accountId;
    private UUID user;
    private BigDecimal discount;
    private List<OperationDto> operations;

    private BigDecimal totalSum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TicketDirectionEnum getTicketDirection() {
        return ticketDirection;
    }

    public void setTicketDirection(TicketDirectionEnum ticketDirection) {
        this.ticketDirection = ticketDirection;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public List<OperationDto> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationDto> operations) {
        this.operations = operations;
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum;
    }
}
