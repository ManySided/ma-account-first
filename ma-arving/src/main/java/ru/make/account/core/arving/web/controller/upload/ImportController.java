package ru.make.account.core.arving.web.controller.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.make.account.core.arving.service.upload.ImportDataService;
import ru.make.account.core.arving.web.dto.upload.UploadCsvResponseDto;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api/service/upload")
@RequiredArgsConstructor
public class ImportController {
    private final ImportDataService importDataService;

    @PostMapping("prepareDataModel")
    public ResponseEntity<UploadCsvResponseDto> prepareDataModel(@RequestParam Long accountId,
                                                                 @RequestParam MultipartFile file) {
        UploadCsvResponseDto result;
        if (file.isEmpty())
            result = UploadCsvResponseDto.builder()
                    .statusLoad(Boolean.FALSE)
                    .errorText("Отсутствует файл")
                    .build();
        else try (var inputStream = file.getInputStream()) {
            result = importDataService.prepareModel(accountId, inputStream);
        } catch (IOException e) {
            result = UploadCsvResponseDto.builder()
                    .statusLoad(Boolean.FALSE)
                    .errorText("Ошибка: " + e.getMessage())
                    .build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("importDataModel")
    public ResponseEntity<Boolean> importDataModel(@RequestBody UploadCsvResponseDto request) {
        var result = importDataService.saveModel(request);
        return ResponseEntity.ok(result);
    }
}
