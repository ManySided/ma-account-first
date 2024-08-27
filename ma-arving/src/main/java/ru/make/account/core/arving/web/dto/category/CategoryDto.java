package ru.make.account.core.arving.web.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private Long parent;
    private Long accountId;
    private Boolean stuffFlag;
    private List<CategoryDto> subCategories;
}
