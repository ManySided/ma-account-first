package ru.make.account.load.model;

import java.util.List;

public class CategoryDto {
    private Long id;
    private String name;
    private Long parent;
    private Long accountId;
    private Boolean stuffFlag;
    private List<CategoryDto> subCategories;

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
}
