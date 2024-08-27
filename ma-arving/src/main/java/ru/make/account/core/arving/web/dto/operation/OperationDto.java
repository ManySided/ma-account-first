package ru.make.account.core.arving.web.dto.operation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.make.account.core.arving.web.dto.category.CategoryDto;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationDto {
    private Long id;
    @NotNull(message = "Поле 'Сумма операции' должно быть заполнен")
    private BigDecimal sum;
    @NotNull(message = "Поле 'Название операции' должно быть заполнен")
    private String name;
    private String comment;
    @NotNull(message = "Поле 'Категория операции' должно быть заполнен")
    private CategoryDto category;
    private Long purchaseId;
    private Long ticketId;
    private Boolean isActive;
    private Boolean stuffFlag;
    private Boolean importFlag;
}
