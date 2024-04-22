package org.example.csv.csv.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.example.csv.csv.exceptionHandler.InternalServerException;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.services.Implimentation.EdiToCSVServiceImplementation;
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
import java.util.Arrays;

@RestController
public class EdiToCSVController {

    @Autowired
    private EdiToCSVServiceImplementation ediToCSVServiceImplementation;

    @GetMapping(value = "/edi846-to-csv", produces = "text/csv")
    public ResponseEntity<Object> ediToCsv(@RequestParam(value = "ediFile") MultipartFile ediFile, HttpServletResponse response) throws IOException {
        File responseFile = null;
        try {
            responseFile = ediToCSVServiceImplementation.ediToCSVConvertor(ediFile);
            byte[] responseFileByte = Files.readAllBytes(responseFile.toPath());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + responseFile.getName() + "\"");
            return ResponseEntity.status(HttpStatus.OK).body(responseFileByte);
        } catch (InvalidFileException e) {
            throw new InvalidFileException();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } finally {
            if (responseFile != null) {
                FileUtils.delete(responseFile);
            }
        }
    }
}
