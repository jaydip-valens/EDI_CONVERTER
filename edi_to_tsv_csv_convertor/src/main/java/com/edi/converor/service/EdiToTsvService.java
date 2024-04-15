package com.edi.converor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Service
public class EdiToTsvService {

    public File ediToCsv(MultipartFile ediFile) throws IOException {

        String name;
        String receiverNumber = null;
        String fileName;
        StringBuilder tsvRow = new StringBuilder();
        File tsvFile = null;
        FileWriter tsvWriter = null;
        int count = 0;
        try (InputStream stream = ediFile.getInputStream(); Scanner scanner = new Scanner(stream)) {

            while (scanner.hasNext()) {
                String dataLine = scanner.nextLine();
                if (count == 0) {
                    if (dataLine.startsWith("ISA")) {
                        String[] singleData = dataLine.split("\\*");
                        receiverNumber = singleData[8].trim();
                    } else {
                        return null;
                    }
                } else if (dataLine.startsWith("N1")) {
                    String[] singleData = dataLine.split("\\*");
                    name = singleData[2].replaceAll("[^a-zA-Z0-9.]", "_");
                    name = name.replace(".", "");

                    fileName = receiverNumber + "_" + name;

                    String tsvFileName = fileName + ".tsv";
                    tsvFile = new File(tsvFileName);
                    try {
                        tsvWriter = new FileWriter(tsvFile);
                        tsvWriter.write("ProductCode\tProductName\tPrice\tQuantity");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    if (dataLine.startsWith("PID*F*08")) {
                        String[] singleData = dataLine.split("\\*");
                        tsvRow.append(singleData[singleData.length - 1].trim()).append("\t");
                    } else if (dataLine.startsWith("LIN**") && dataLine.contains("VP")) {
                        tsvWriter.write("\n");
                        String[] singleData = dataLine.split("\\*");
                        tsvRow.append(singleData[singleData.length - 1].trim()).append("\t");
                    } else if (dataLine.startsWith("CTP**")) {
                        String[] singleData = dataLine.split("\\*");
                        tsvRow.append(singleData[singleData.length - 1].trim()).append("\t");
                    } else if (dataLine.startsWith("QTY*")) {
                        String[] singleData = dataLine.split("\\*");
                        tsvRow.append(singleData[singleData.length - 2].trim());
                        if (tsvWriter != null) {
                            tsvWriter.write(String.valueOf(tsvRow));
                        }
                        tsvRow.delete(0, tsvRow.length());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return tsvFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (tsvWriter != null) {
                tsvWriter.flush();
                tsvWriter.close();
            }
        }
    }
}
