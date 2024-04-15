package com.edi.converor.service;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Service
public class EdiToCsvService {

    public File ediToCsv(MultipartFile ediFile) throws IOException {
        String name;
        String receiverNumber = null;
        String fileName;
        File csvFile = null;
        CSVWriter csvWriter = null;
        String[] csvRow = new String[4];
        try (InputStream stream = ediFile.getInputStream(); Scanner scanner = new Scanner(stream)) {
            while (scanner.hasNext()) {
//                System.out.println(scanner.next());
                String dataLine = scanner.nextLine();

                if (dataLine.startsWith("ISA")) {
                    String[] singleData = dataLine.split("\\*");
                    receiverNumber = singleData[8].trim();
                } else if (dataLine.startsWith("N1")) {
                    String[] singleData = dataLine.split("\\*");
                    name = singleData[2].replaceAll("[^a-zA-Z0-9.]", "_");
                    name = name.replace(".", "");

                    fileName = receiverNumber + "_" + name;

                    String csvFileName = fileName + ".csv";
                    csvFile = new File(csvFileName);
                    try{
                        csvWriter = new CSVWriter(new FileWriter(csvFile));
                        String[] header = {"ProductCode", "ProductName", "Price", "Quantity"};
                        csvWriter.writeNext(header);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    if (dataLine.startsWith("PID*F*08")) {
                        String[] singleData = dataLine.split("\\*");
                        csvRow[1] = singleData[singleData.length - 1].trim();
                    } else if (dataLine.startsWith("LIN**") && dataLine.contains("VP")) {
                        String[] singleData = dataLine.split("\\*");
                        csvRow[0] = (singleData[singleData.length - 1].trim());
                    } else if (dataLine.startsWith("CTP**")) {
                        String[] singleData = dataLine.split("\\*");
                        csvRow[2] = singleData[singleData.length - 1].trim();
                    } else if (dataLine.startsWith("QTY*")) {
                        String[] singleData = dataLine.split("\\*");
                        csvRow[3] = (singleData[singleData.length - 2].trim());
                        if (csvWriter != null) {
                            csvWriter.writeNext(csvRow);
                        }
//                        System.out.println(Arrays.toString(csvRow));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return csvFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(csvWriter != null){
                csvWriter.flush();
                csvWriter.close();
            }
        }
    }
}
