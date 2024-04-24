package org.example.csv.csv.controller.ridham_controller;

import org.example.csv.csv.services.ridham_service.CsvToEdiService;
import org.example.csv.csv.exceptionHandler.ridham_exceptionHandler.InvalidFileException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/csv")
public class CsvToEdiController {

    @Autowired
    private CsvToEdiService csvToEdiService;

    private static final Logger logger = LoggerFactory.getLogger(CsvToEdiController.class);

    @PostMapping(value = "/to-edi", produces = "text/edi")
    public ResponseEntity<Object> csvToEdi(@RequestPart(value = "csvFile") MultipartFile csvFile, HttpServletResponse response) throws IOException {
        if (csvFile.isEmpty()) {
            logger.error("Invalid file or file is empty.");
            throw new InvalidFileException();
        }
        byte[] fileContent;
        try {
            File ediFile = csvToEdiService.csvToEdi(csvFile);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ediFile.getName() + "\"");
            fileContent = Files.readAllBytes(ediFile.toPath());
            Files.deleteIfExists(ediFile.toPath());
            logger.info("EDI file successfully send in response.");
        } catch (Exception e) {
            logger.error("Runtime error: {}",e.getMessage());
            throw new RuntimeException();
        }
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }
}
