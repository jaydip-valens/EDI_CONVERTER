package com.edi.converor.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;


public interface CsvToEdiService {

    File CsvToEdi(MultipartFile csvFile) throws Exception;

}
