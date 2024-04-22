package com.edi.converor.service;

import com.opencsv.CSVWriter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

@Service
public class EdiToCsvService {

    public File ediToCsv(MultipartFile ediFile) throws IOException {
        String name = "";
        String receiverNumber = null;
        String fileName = "";
        File csvFile = null;
        CSVWriter csvWriter = null;
        String[] csvRow = new String[4];
        int count = 0;
        String content = new String(ediFile.getBytes());
        String[] lines = content.contains("~GS") ? content.split("~") : content.split("\n");
//        System.out.println(Arrays.toString(lines));
        int i=0;
        try {
            while (i < lines.length) {
//                String dataLine = scanner.nextLine();
                if (count == 0) {
                    if (lines[i].startsWith("ISA")) {
                        String[] singleData = lines[i].split("\\*");
                        receiverNumber = singleData[8].trim();
                        System.out.println("number" + receiverNumber);
                        count++;
                        fileName = receiverNumber;

                        String csvFileName = fileName + ".csv";
                        csvFile = new File(csvFileName);
                        try {
                            csvWriter = new CSVWriter(new FileWriter(csvFile));
                            String[] header = {"ProductCode", "ProductName", "Price", "Quantity"};
                            csvWriter.writeNext(header);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        return null;
                    }
                } else if (lines[i].startsWith("N1")) {
                    String[] singleData = lines[i].split("\\*");
                    name = singleData[2].replaceAll("[^a-zA-Z0-9.]", "_");
                    name = name.replace(".", "");
                }
                try {
                    if (lines[i].startsWith("PID*F*08")) {
                        String[] singleData = lines[i].split("\\*");
                        csvRow[1] = singleData[singleData.length - 1].trim();
                    } else if (lines[i].startsWith("LIN**") && lines[i].contains("VP")) {
                        String[] singleData = lines[i].split("\\*");
                        csvRow[0] = (singleData[singleData.length - 1].trim());
                    } else if (lines[i].startsWith("CTP**")) {
                        String[] singleData = lines[i].split("\\*");
                        csvRow[2] = singleData[singleData.length - 1].trim();
                    } else if (lines[i].startsWith("QTY*")) {
                        String[] singleData = lines[i].split("\\*");
                        csvRow[3] = (singleData[singleData.length - 2].trim());
                        csvWriter.writeNext(csvRow);
                        System.out.println(Arrays.toString(csvRow));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
            return csvFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (csvWriter != null) {
                csvWriter.flush();
                csvWriter.close();
            }
        }
    }
}
