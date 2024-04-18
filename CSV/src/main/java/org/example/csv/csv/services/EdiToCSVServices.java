package org.example.csv.csv.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface EdiToCSVServices {
    File ediToCSVConvertor(MultipartFile file) throws IOException;
}
