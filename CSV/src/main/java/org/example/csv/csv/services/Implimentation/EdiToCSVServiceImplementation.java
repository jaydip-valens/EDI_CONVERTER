package org.example.csv.csv.services.Implimentation;

import com.opencsv.CSVWriter;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.services.EdiToCSVServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.csv.csv.utils.Util.validateFile;

@Service
public class EdiToCSVServiceImplementation implements EdiToCSVServices {

    private static final Logger logger = LoggerFactory.getLogger(EdiToCSVServiceImplementation.class);

    @Override
    public File ediToCSVConvertor(MultipartFile ediFile) {
        try {
            logger.info("Starting EDI to CSV conversion");
            validateFile(ediFile);
            CSVWriter writer = null;
            String content = new String(ediFile.getBytes());
            String[] contentList = content.contains("~GS") ? content.split("~") : content.split("\n");

            String receiverId = "";
            String vendorName = "";
            String fileName = "";
            int count = 0;
            List<String> headerSet = new ArrayList<>();
            List<String> tempCsvDataList = new ArrayList<>();

            for (String data : contentList) {
                if (count == 2) {
                    count++;
                    fileName = getFileName(receiverId, vendorName, fileName);
                    writer = new CSVWriter(new FileWriter(fileName));
                    writer.writeNext(headerSet.toArray(new String[0]));
                    writer.writeNext(tempCsvDataList.subList(0, tempCsvDataList.size() - 1).toArray(new String[0]));
                    Collections.reverse(tempCsvDataList);
                    String nextVendor = tempCsvDataList.get(0);
                    tempCsvDataList.clear();
                    tempCsvDataList.add(nextVendor);
                }
                if (data.startsWith("ISA*")) {
                    receiverId = data.split("\\*")[8].trim();
                } else if (data.startsWith("N1*")) {
                    String[] temp = data.split("\\*");
                    vendorName = temp[temp.length - 1].replace(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
                } else if (data.startsWith("LIN*")) {
                    if (!headerSet.contains("vendor")) {
                        headerSet.add("vendor");
                    }
                    count++;
                    String[] temp = data.split("\\*");
                    tempCsvDataList.add(temp[temp.length - 1]);
                } else if (data.startsWith("PID*F*08")) {
                    if (!headerSet.contains("Product Name")) {
                        headerSet.add("Product Name");
                    }
                    String[] temp = data.split("\\*");
                    tempCsvDataList.add(temp[temp.length - 1].trim());
                } else if (data.startsWith("CTP*")) {
                    if (!headerSet.contains("Cost")) {
                        headerSet.add("Cost");
                    }
                    String[] temp = data.split("\\*");
                    tempCsvDataList.add(temp.length < 4 ? "" : temp[temp.length - 1]);
                } else if (data.startsWith("QTY*")) {
                    if (!headerSet.contains("Quantity")) {
                        headerSet.add("Quantity");
                    }
                    String[] temp = data.split("\\*");
                    tempCsvDataList.add(temp[temp.length - 2]);
                }
                if (headerSet.size() == tempCsvDataList.size() && writer != null) {
                    writer.writeNext(tempCsvDataList.toArray(new String[headerSet.size()]));
                    tempCsvDataList.clear();
                }
            }
            if (count < 2) {
                fileName = getFileName(receiverId, vendorName, fileName);
                writer = new CSVWriter(new FileWriter(fileName));
                writer.writeNext(headerSet.toArray(new String[0]));
                writer.writeNext(tempCsvDataList.toArray(new String[0]));
            }
            writer.close();
            logger.info("EDI to CSV conversion completed successfully.");
            return new File(fileName);
        } catch (InvalidFileException e) {
            logger.error("Invalid file encountered during EDI to CSV conversion.", e);
            throw new InvalidFileException();
        } catch (IOException e) {
            logger.error("Error occurred during EDI to CSV conversion.", e);
            throw new RuntimeException(e);
        }
    }

    private static String getFileName(String receiverId, String vendorName, String fileName) {
        if (!receiverId.isBlank()) {
            if (!vendorName.isBlank()) {
                fileName = receiverId + "_" + vendorName + ".csv";
            } else {
                fileName = receiverId + ".csv";
            }
        }
        return fileName;
    }
}
