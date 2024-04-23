package com.edi.converor.controller;

import com.edi.converor.exception.InvalidFileException;
import com.edi.converor.service.EdiToCsvService;
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
@RequestMapping("/edi")
public class EdiToCsvController {

    @Autowired
    private EdiToCsvService ediToCsvService;
    private static final Logger logger = LoggerFactory.getLogger(EdiToCsvController.class);

    @PostMapping(value = "/to-csv", produces = "text/csv")
    public ResponseEntity<Object> ediToCsv(@RequestPart(value = "ediFile") MultipartFile ediFile, HttpServletResponse response) {
        if (ediFile.isEmpty()) {
            logger.error("Invalid file or File is empty.");
            throw new InvalidFileException();
        }
        byte[] fileContent;
        try {
            File csvFile = ediToCsvService.ediToCsv(ediFile);
            if (csvFile != null) {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + csvFile.getName() + "\"");
                fileContent = Files.readAllBytes(csvFile.toPath());
                Files.deleteIfExists(csvFile.toPath());
                logger.info("CSV file successfully send in response.");
            } else {
                logger.error("EDI file does not contain ISA header.");
                return new ResponseEntity<>("EDI file must have ISA Header.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Runtime error: {}",e.getMessage());
            throw new RuntimeException();
        }
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }
}
