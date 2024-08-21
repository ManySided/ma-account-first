package ru.make.account.core.arving.web.dto.category;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCategoryDto {
    @NotNull(message = "Поле 'ID категории' должно быть заполнен")
    private Long categoryId;
    private Long reserveCategoryId;
}
