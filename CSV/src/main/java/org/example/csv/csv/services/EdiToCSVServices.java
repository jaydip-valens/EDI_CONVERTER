package org.example.csv.csv.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface EdiToCSVServices {
    Map<String, byte[]> ediToCSVConvertor(MultipartFile file) throws IOException;
}
