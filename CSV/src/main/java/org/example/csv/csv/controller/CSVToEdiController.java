package org.example.csv.csv.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.example.csv.csv.services.Implimentation.CSVToEdiServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@RestController
public class CSVToEdiController {

    @Autowired
    private CSVToEdiServiceImplementation csvToEdiServiceImplementation;

    @GetMapping(value = "/csv-to-edi846" , produces = "text/edi")
    public ResponseEntity<Object> csvToEdi(@RequestParam(value = "csvFile" )MultipartFile csvFile, HttpServletResponse response) throws IOException {
        File responseFile = null;
        try {
            if (csvFile.isEmpty() || !Objects.equals(FilenameUtils.getExtension(csvFile.getOriginalFilename()), "csv")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid File");
            }
            responseFile = csvToEdiServiceImplementation.csvToEdiConverter(csvFile);
            byte[] responseFileByte = Files.readAllBytes(responseFile.toPath());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + responseFile.getName() + "\"");
            return ResponseEntity.status(HttpStatus.OK).body(responseFileByte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } finally {
            if (responseFile != null) {
                FileUtils.delete(responseFile);
                FileUtils.delete(new File(csvFile.getOriginalFilename()));
            }
        }
    }
}
