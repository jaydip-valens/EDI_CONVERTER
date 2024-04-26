package org.example.csv.csv.controller;

import org.example.csv.csv.services.EdiToCSVServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class EdiToCSVController {

    @Autowired
    private EdiToCSVServices ediToCSVServices;

    @GetMapping(value = "/edi846-to-csv", produces = "text/csv")
    public ResponseEntity<String> ediToCsv(@RequestParam(value = "ediFile") MultipartFile ediFile) {
        try {
            Map<String, String> resultMap = ediToCSVServices.ediToCSVConvertor(ediFile);
            return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resultMap.get("fileName") + "\"").body(resultMap.get("result"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
