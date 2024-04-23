package com.edi.converor.controller;

import com.edi.converor.exception.InvalidFileException;
import com.edi.converor.service.EdiToTsvService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
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
import java.nio.file.Files;
import java.util.Objects;

@RestController
@RequestMapping("/edi")
public class EdiToTsvController {

    @Autowired
    private EdiToTsvService ediToTsvService;

    private static final Logger logger = LoggerFactory.getLogger(EdiToTsvController.class);

    @PostMapping(value = "/to-tsv", produces = "text/tsv")
    public ResponseEntity<Object> ediToCsv(@RequestPart(value = "ediFile") MultipartFile ediFile, HttpServletResponse response) {
        if (ediFile.isEmpty()) {
            logger.info("Invalid file or File is empty.");
            throw new InvalidFileException();
        }
        byte[] bytes;
        try {
            File tsvFile = ediToTsvService.ediToTsv(ediFile);
            if (tsvFile != null) {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + tsvFile.getName() + "\"");
                bytes = Files.readAllBytes(tsvFile.toPath());
                Files.deleteIfExists(tsvFile.toPath());
                logger.info("TSV file successfully send in response.");
            } else {
                logger.error("EDI file does not contain ISA header.");
                return new ResponseEntity<>("EDI file must have IAS Header.", HttpStatus.OK);
            }
        } catch (Exception e){
            logger.error("Runtime error: {}",e.getMessage());
            throw new RuntimeException();
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }
}
