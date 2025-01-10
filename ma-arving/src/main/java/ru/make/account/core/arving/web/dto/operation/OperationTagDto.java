package ru.make.account.core.arving.web.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationTagDto {
    private Long id;
    private String name;
    private String color;
    private String sign;
    private Long accountId;
}
