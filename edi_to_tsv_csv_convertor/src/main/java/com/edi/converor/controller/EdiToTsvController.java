package com.edi.converor.controller;

import com.edi.converor.service.EdiToTsvService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/edi")
public class EdiToTsvController {

    @Autowired
    private EdiToTsvService ediToTsvService;

    @PostMapping(value = "/to-tsv",produces = "text/tsv")
    public ResponseEntity<Object> ediToCsv(@RequestPart(value = "ediFile") MultipartFile ediFile, HttpServletResponse response) {
        if (ediFile.isEmpty() || !Objects.equals(FilenameUtils.getExtension(ediFile.getOriginalFilename()), "edi")) {
            return new ResponseEntity<>("Invalid file, please try again", HttpStatus.BAD_REQUEST);
        }
        InputStreamResource tsvFileData;
        byte[] bytes;
        try {
            File convertedFile = ediToTsvService.ediToCsv(ediFile);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + convertedFile.getName() + "\"");
            bytes = Files.readAllBytes(convertedFile.toPath());
            Path path = Paths.get(convertedFile.getName());
            Files.deleteIfExists(path);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }
}
