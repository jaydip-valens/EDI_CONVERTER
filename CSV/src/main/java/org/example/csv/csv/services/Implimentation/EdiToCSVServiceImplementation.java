package org.example.csv.csv.services.Implimentation;

import com.opencsv.CSVWriter;
import org.apache.commons.io.FileUtils;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.services.EdiToCSVServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.example.csv.csv.utils.Util.validateFile;

@Service
public class EdiToCSVServiceImplementation implements EdiToCSVServices {

    private static final Logger logger = LoggerFactory.getLogger(EdiToCSVServiceImplementation.class);
    private static final Random random = new Random(100000000000000000L);
    @Override
    public Map<String, byte[]> ediToCSVConvertor(MultipartFile ediFile) throws IOException {
        long randomVal = random.nextLong();
        try {
            logger.info("Starting EDI to CSV conversion");
            validateFile(ediFile);
            String content = new String(ediFile.getBytes());
            String[] contentList = content.contains("~GS") ? content.split("~") : content.split("\n");
            String receiverId = "";
            String vendorName = "";
            String fileName = "";
            List<String> headerSet = new ArrayList<>();
            List<String> tempCsvDataList = new ArrayList<>();
            try (CSVWriter writer = new CSVWriter(new FileWriter(randomVal + ".csv"))) {
                for (String data : contentList) {
                    if (data.startsWith("ISA*")) {
                        receiverId = data.split("\\*")[8].trim();
                    } else if (data.startsWith("N1*")) {
                        String[] temp = data.split("\\*");
                        vendorName = temp[temp.length - 1].replace(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
                    } else if (data.startsWith("LIN*")) {
                        if (!headerSet.isEmpty() && headerSet.size() == tempCsvDataList.size()) {
                            writer.writeNext(headerSet.toArray(new String[0]));
                            writer.writeNext(tempCsvDataList.toArray(new String[0]));
                            tempCsvDataList.clear();
                        }
                        if (!tempCsvDataList.isEmpty()) {
                            writer.writeNext(tempCsvDataList.toArray(new String[0]));
                            tempCsvDataList.clear();
                        }
                        headerSet.add("vendor");
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
                long linCount = Arrays.stream(contentList).filter(x -> x.startsWith("LIN*")).count();
                if (linCount == 1) {
                    writer.writeNext(headerSet.toArray(new String[0]));
                }
                if (!tempCsvDataList.isEmpty()) {
                    writer.writeNext(tempCsvDataList.toArray(new String[0]));
                    headerSet.clear();
                    tempCsvDataList.clear();
                }
            } catch (Exception e) {
                logger.error("Error occurred during EDI to CSV conversion.", e);
                throw new RuntimeException(e);
            }
            fileName = getFileName(receiverId, vendorName, fileName);
            logger.info("EDI to CSV conversion completed successfully.");
            Map<String, byte[]> resultMap = new HashMap<>();
            resultMap.put("bytes", Files.readAllBytes(Path.of(randomVal + ".csv")));
            resultMap.put("fileName", fileName.getBytes());
            return resultMap;
        } catch (InvalidFileException e) {
            logger.error("Invalid file encountered during EDI to CSV conversion.", e);
            throw new InvalidFileException();
        } catch (Exception e) {
            logger.error("Error occurred during EDI to CSV conversion.", e);
            throw new RuntimeException(e);
        } finally {
            FileUtils.delete(new File(randomVal + ".csv"));
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
