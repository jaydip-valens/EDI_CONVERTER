package org.example.csv.csv.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.example.csv.csv.services.CSVToEdiServices;
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

@RestController
public class CSVToEdiController {

    @Autowired
    private CSVToEdiServices csvToEdiServices;

    @GetMapping(value = "/csv-to-edi846", produces = "text/edi")
    public ResponseEntity<Object> csvToEdi(@RequestParam(value = "csvFile") MultipartFile csvFile, HttpServletResponse response) throws IOException {
        File responseFile = null;
        try {
            responseFile = csvToEdiServices.csvToEdiConverter(csvFile);
            byte[] responseFileByte = Files.readAllBytes(responseFile.toPath());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + responseFile.getName() + "\"");
            FileUtils.delete(new File(csvFile.getOriginalFilename()));
            return ResponseEntity.status(HttpStatus.OK).body(responseFileByte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } finally {
            if (responseFile != null) {
                FileUtils.delete(responseFile);
            }
        }
    }
}
