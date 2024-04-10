package org.example.csv.csv.controller;

import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.example.csv.csv.services.EdiToCSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@RestController
public class EdiToCSVController {

    @Autowired
    private EdiToCSVService ediToCSVService;

    @GetMapping("/edi846-to-csv")
    public Object ediToCsv(@RequestParam(value = "ediFile")MultipartFile ediFile, HttpServletResponse response) {
        try {
            if (ediFile.isEmpty() && !Objects.equals(FilenameUtils.getExtension(ediFile.getOriginalFilename()), "edi")) {
                return "invalid File";
            }
            Map<String, Object> result = ediToCSVService.fileRead(ediFile);
            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + result.get("fileName") + "\"");
            CSVWriter writer = new CSVWriter(response.getWriter());
            String[] header = {"Product Name","Cost","Quality","Vendor"};
            writer.writeNext(header);
            writer.writeAll((Iterable<String[]>) result.get("csvData"));
        } catch (Exception e) {
            return "Filed To Convert : " + e.getMessage();
        }
        return null;
    }
}
