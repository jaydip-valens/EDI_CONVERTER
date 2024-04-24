package org.example.csv.csv.services.Implimentation.ridham_implimentation;

import org.example.csv.csv.services.ridham_service.EdiToCsvService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class EdiToCsvServiceImpl implements EdiToCsvService {

    private static final Logger logger = LoggerFactory.getLogger(EdiToCsvServiceImpl.class);

    @Override
    public File ediToCsv(MultipartFile ediFile) throws IOException {
        String name = "";
        String receiverNumber;
        String fileName  = "";
        File csvFile = null;
        CSVWriter csvWriter = null;
        String[] csvRow = new String[4];
        int count = 0;
        logger.info("Start reading data from EDI file.");
        String content = new String(ediFile.getBytes());
        String[] lines = content.contains("~GS") ? content.split("~") : content.split("\n");
        int i=0;
        try {
            while (i < lines.length) {
                if (count == 0) {
                    if (lines[i].startsWith("ISA")) {
                        String[] singleData = lines[i].split("\\*");
                        receiverNumber = singleData[8].trim();
                        count++;
                        fileName = receiverNumber;

                        String csvFileName = fileName + ".csv";
                        csvFile = new File(csvFileName);
                        try {
                            csvWriter = new CSVWriter(new FileWriter(csvFile));
                            String[] header = {"ProductCode", "ProductName", "Price", "Quantity"};
                            logger.info("Start writing data into CSV file.");
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
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
            logger.info("EDI file successfully converted into CSV file.");
            String csvFileName = fileName + name + ".csv";
            csvFile.renameTo(new File(csvFileName));
            return csvFile;
        } catch (Exception e) {
            logger.error("Runtime error: {}",e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (csvWriter != null) {
                csvWriter.flush();
                csvWriter.close();
            }
        }
    }
}
