package ru.make.account.load.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto {
    private Long id;
    private String name;
    private String comment;
    private BigDecimal startSum;
    private BigDecimal currentSum;
    private LocalDateTime created;
    private Boolean actual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getStartSum() {
        return startSum;
    }

    public void setStartSum(BigDecimal startSum) {
        this.startSum = startSum;
    }

    public BigDecimal getCurrentSum() {
        return currentSum;
    }

    public void setCurrentSum(BigDecimal currentSum) {
        this.currentSum = currentSum;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Boolean getActual() {
        return actual;
    }

    public void setActual(Boolean actual) {
        this.actual = actual;
    }
}
