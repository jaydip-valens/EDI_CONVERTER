package org.example.csv.csv.services;

import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface CSVToEdiServices {
    File csvToEdiConverter(MultipartFile file) throws IOException, CsvException;
}
