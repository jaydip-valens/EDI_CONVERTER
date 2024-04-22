package com.edi.converor.controller;

import com.edi.converor.ServiceImpl.CsvToEdiServiceImpl;
import com.edi.converor.exception.CustomIOException;
import com.edi.converor.exception.CustomRuntimeException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
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
import java.util.Objects;

@RestController
@RequestMapping("/csv")
public class CsvToEdiController {

    @Autowired
    private CsvToEdiServiceImpl csvToEdiServiceImpl;

    @PostMapping(value = "/to-edi", produces = "text/edi")
    public ResponseEntity<Object> csvToEdi(@RequestPart(value = "csvFile") MultipartFile csvFile, HttpServletResponse response) throws IOException {
        if (csvFile.isEmpty() || !Objects.equals(FilenameUtils.getExtension(csvFile.getOriginalFilename()), "csv")) {
            throw new CustomIOException();
        }
        byte[] fileContent;
        try {
            File ediFile = csvToEdiServiceImpl.CsvToEdi(csvFile);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ediFile.getName() + "\"");
            fileContent = Files.readAllBytes(ediFile.toPath());
            Files.deleteIfExists(ediFile.toPath());
        } catch (CustomIOException e) {
            throw new CustomIOException();
        } catch (Exception e) {
            throw new CustomRuntimeException();
        }
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }
}