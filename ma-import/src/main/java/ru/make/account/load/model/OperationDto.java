package ru.make.account.load.model;

import java.math.BigDecimal;

public class OperationDto {
    private Long id;
    private BigDecimal sum;
    private String name;
    private String comment;
    private CategoryDto category;
    private Long purchaseId;
    private Long ticketId;
    private Boolean isActive;
    private Boolean stuffFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
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

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getStuffFlag() {
        return stuffFlag;
    }

    public void setStuffFlag(Boolean stuffFlag) {
        this.stuffFlag = stuffFlag;
    }
}
