package com.edi.converor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public interface EdiToTsvService {

    public File ediToTsv(MultipartFile ediFile) throws IOException;

}
