package org.example.csv.csv.utils;

import org.apache.commons.io.FilenameUtils;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Component
public class Util {
    public static void validateFile(MultipartFile ediFile, String fileType) {
        if (ediFile.isEmpty() || !Objects.equals(FilenameUtils.getExtension(ediFile.getOriginalFilename()), fileType)) {
            throw new InvalidFileException();
        }
    }

    public String createStringForSegment(String value, int size) {
        StringBuilder stringBuilder = new StringBuilder(size);
        stringBuilder.append(value);
        if (value.length() < size) {
            stringBuilder.append(" ".repeat(size - value.length()));
        }
        return stringBuilder.toString();
    }
}
