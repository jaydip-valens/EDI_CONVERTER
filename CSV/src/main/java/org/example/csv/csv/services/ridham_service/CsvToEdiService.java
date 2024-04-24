package org.example.csv.csv.services.ridham_service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;


public interface CsvToEdiService {

    File csvToEdi(MultipartFile csvFile);

}
