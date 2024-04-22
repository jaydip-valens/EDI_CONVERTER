package org.example.csv.csv.services.Implimentation;


import com.opencsv.CSVWriter;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.services.EdiToCSVServices;
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

    public File ediToCSVConvertor(MultipartFile ediFile) throws IOException {
        CSVWriter writer = null;
        try {
            validateFile(ediFile, "edi");
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
                    if (!receiverId.isBlank()) {
                        if (!vendorName.isBlank()) {
                            fileName = receiverId + "_" + vendorName + ".csv";
                        } else {
                            fileName = receiverId + ".csv";
                        }
                    }
                    writer = new CSVWriter(new FileWriter(fileName));
                    writer.writeNext(new ArrayList<>(headerSet.stream().toList()).toArray(new String[0]));
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
                    if (headerSet.contains("vendor")) {
                        tempCsvDataList.add(temp[temp.length - 1]);
                    }
                } else if (data.startsWith("PID*F*08")) {
                    if (!headerSet.contains("Product Name")) {
                        headerSet.add("Product Name");
                    }
                    String[] temp = data.split("\\*");
                    if (headerSet.contains("Product Name")) {
                        tempCsvDataList.add(temp[temp.length - 1].trim());
                    }
                } else if (data.startsWith("CTP*")) {
                    if (!headerSet.contains("Cost")) {
                        headerSet.add("Cost");
                    }
                    String[] temp = data.split("\\*");
                    if (headerSet.contains("Cost")) {
                        tempCsvDataList.add(temp.length < 4 ? "" : temp[temp.length - 1]);
                    }
                } else if (data.startsWith("QTY*")) {
                    if (!headerSet.contains("Quantity")) {
                        headerSet.add("Quantity");
                    }
                    String[] temp = data.split("\\*");
                    if (headerSet.contains("Quantity")) {
                        tempCsvDataList.add(temp[temp.length - 2]);
                    }
                }
                if (headerSet.size() == tempCsvDataList.size() && writer != null) {
                    writer.writeNext(tempCsvDataList.toArray(new String[headerSet.size()]));
                    tempCsvDataList.clear();
                }
            }
            return new File(fileName);
        } catch (InvalidFileException e) {
            throw new InvalidFileException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}






