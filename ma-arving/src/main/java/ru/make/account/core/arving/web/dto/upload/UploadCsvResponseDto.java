package ru.make.account.core.arving.web.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadCsvResponseDto {
    private Boolean statusLoad;
    private String errorText;
    private Long accountId;
    private List<UploadRecordDto> records;
}
