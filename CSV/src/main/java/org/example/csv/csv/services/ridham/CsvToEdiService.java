package org.example.csv.csv.services.ridham;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;


public interface CsvToEdiService {

    File csvToEdi(MultipartFile csvFile);

}
