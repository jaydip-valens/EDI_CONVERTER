package org.example.csv.csv.services;


import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class EdiToCSVService {

    public File ediToCSVConvertor(MultipartFile ediFile) throws IOException {
        CSVWriter writer = null;
        try {
            String content = new String(ediFile.getBytes());
            String[] contentList = content.split("\n");
            String receiverId = "";
            String vendorName = "";
            String fileName = null;
            int count = 0;
            String[] tempCsvDataArray = new String[4];
            for (String data : contentList) {
                if (!receiverId.isBlank() && !vendorName.isBlank() && count == 0) {
                    fileName = receiverId + "_" + vendorName + ".csv";
                    writer = new CSVWriter(new FileWriter(fileName));
                    String[] header = {"Product Name", "Cost", "Quality", "Vendor"};
                    writer.writeNext(header);
                }
                if (data.startsWith("ISA*")) {
                    receiverId = data.split("\\*")[8].trim();
                } else if (data.startsWith("N1*")) {
                    String[] temp = data.split("\\*");
                    vendorName = temp[temp.length - 1].replace(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
                } else if (data.startsWith("LIN*")) {
                    count++;
                    String[] temp = data.split("\\*");
                    tempCsvDataArray[3] = temp[temp.length - 1];
                } else if (data.startsWith("PID*F*08")) {
                    String[] temp = data.split("\\*");
                    tempCsvDataArray[0] = temp[temp.length - 1].trim();
                } else if (data.startsWith("CTP*")) {
                    String[] temp = data.split("\\*");
                    tempCsvDataArray[1] = temp[temp.length - 1];
                } else if (data.startsWith("QTY*")) {
                    String[] temp = data.split("\\*");
                    tempCsvDataArray[2] = temp[1];
                    writer.writeNext(tempCsvDataArray);
                }
            }
            return new File(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}






