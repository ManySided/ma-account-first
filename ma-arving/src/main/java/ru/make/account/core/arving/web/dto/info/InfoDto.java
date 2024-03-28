package ru.make.account.core.arving.web.dto.info;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InfoDto {
    private String projectName;
    private LocalDateTime now;
    private String version;
}
