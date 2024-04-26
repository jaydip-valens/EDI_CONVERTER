package org.example.csv.csv.services.Implimentation;

import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.services.EdiToCSVServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.example.csv.csv.utils.Util.validateFile;

@Service
public class EdiToCSVServiceImplementation implements EdiToCSVServices {

    private static final Logger logger = LoggerFactory.getLogger(EdiToCSVServiceImplementation.class);
    @Override
    public Map<String, String> ediToCSVConvertor(MultipartFile ediFile) {
        try {
            logger.info("Starting EDI to CSV conversion");
            validateFile(ediFile);
            String content = new String(ediFile.getBytes());
            String[] contentList = content.contains("~GS") ? content.split("~") : content.contains("~\nGS") ? content.replace("~","").split("\n") : content.split("\n") ;
            String receiverId = "";
            String vendorName = "";
            String fileName = "";
            StringBuilder stringBuilder = new StringBuilder();
            LinkedHashSet<String> headerSet = new LinkedHashSet<>();
            List<String> tempCsvDataList = new ArrayList<>();
            try {
                for (String data : contentList) {
                    if (data.startsWith("ISA*")) {
                        receiverId = data.split("\\*")[8].trim();
                    } else if (data.startsWith("N1*")) {
                        String[] temp = data.split("\\*");
                        vendorName = temp[temp.length - 1].replace(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
                    } else if (data.startsWith("LIN*")) {
                        if (!tempCsvDataList.isEmpty()) {
                            stringBuilder.append(String.join(",", tempCsvDataList)).append("\n");
                            tempCsvDataList.clear();
                        }
                        headerSet.add("Vendor");
                        String[] temp = data.split("\\*");
                        tempCsvDataList.add(temp[temp.length - 1]);
                    } else if (data.startsWith("PID*F*08")) {
                        headerSet.add("Product Name");
                        String[] temp = data.split("\\*");
                        tempCsvDataList.add(temp[temp.length - 1].trim());
                    } else if (data.startsWith("CTP*")) {
                        headerSet.add("Cost");
                        String[] temp = data.split("\\*");
                        tempCsvDataList.add(temp.length < 4 ? "" : temp[temp.length - 1]);
                    } else if (data.startsWith("QTY*")) {
                        headerSet.add("Quantity");
                        String[] temp = data.split("\\*");
                        tempCsvDataList.add(temp[temp.length - 2]);
                    }
                }
            } catch (Exception e) {
                logger.error("Error occurred during EDI to CSV conversion.", e);
                throw new RuntimeException(e);
            }
            fileName = getFileName(receiverId, vendorName, fileName);
            logger.info("EDI to CSV conversion completed successfully.");
            stringBuilder.append(String.join(",", tempCsvDataList)).append("\n");
            stringBuilder.insert(0,String.join(",", headerSet)+"\n");
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("result",stringBuilder.toString());
            resultMap.put("fileName",fileName);
            return resultMap;
        } catch (InvalidFileException e) {
            logger.error("Invalid file encountered during EDI to CSV conversion.", e);
            throw new InvalidFileException();
        } catch (Exception e) {
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
