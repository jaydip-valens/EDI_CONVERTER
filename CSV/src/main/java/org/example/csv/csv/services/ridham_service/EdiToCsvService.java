package org.example.csv.csv.services.ridham_service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public interface EdiToCsvService {

    public File ediToCsv(MultipartFile ediFile) throws IOException;
}
