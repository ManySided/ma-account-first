package ru.make.account.core.arving.web.controller.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.make.account.core.arving.service.upload.ImportDataService;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/api/service/upload")
@RequiredArgsConstructor
public class ImportController {
    private final ImportDataService importDataService;

    @PostMapping("importDataFile")
    public ResponseEntity<Boolean> importDataFile(@RequestParam Long accountId,
                                                  @RequestParam MultipartFile file) {
        Boolean result;
        if (file.isEmpty())
            result = Boolean.FALSE;
        else try {
            result = importDataService.importData(accountId, file.getInputStream());
        } catch (IOException e) {
            result = Boolean.FALSE;
        }
        return ResponseEntity.ok(result);
    }
}
