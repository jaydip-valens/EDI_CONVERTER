package org.example.csv.csv.services;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class EdiToCSVService {

    public Map<String, Object> fileRead(MultipartFile ediFile) throws IOException {
        try {
            //        File file = ResourceUtils.getFile("classpath:static/239_GoGoTech_846_163863.edi");
            String content = new String(ediFile.getBytes());
            String[] contentList = content.split("\n");
            long size = Arrays.stream(contentList).filter(data -> data.startsWith("LIN*")).count();
            String[][] csvData = new String[(int) size][4];
            String receiverId = "";
            String vendorName = "";
            int count = 0;
            for (int i = 0; i < contentList.length; i++) {
                if (contentList[i].contains("ISA")) {
                    receiverId = contentList[i].split("\\*")[8].trim();
                } else if (contentList[i].contains("N1")) {
                    String[] temp = contentList[i].split("\\*");
                    vendorName = temp[temp.length - 1].replace(" ", "_").replaceAll("[^a-zA-Z0-9_]", "");
                } else if (contentList[i].contains("LIN")) {
                    count = i;
                    break;
                }
            }

            int j = 0;
            for (int i = count; i < contentList.length; i++) {
                if (contentList[i].startsWith("LIN*")) {
                    String[] temp = contentList[i].split("\\*");
                    csvData[j][3] = temp[temp.length - 1];
                } else if (contentList[i].startsWith("PID*") && contentList[i].contains("08")) {
                    String[] temp = contentList[i].split("\\*");
                    csvData[j][0] = temp[temp.length - 1].trim();
                } else if (contentList[i].startsWith("CTP*")) {
                    String[] temp = contentList[i].split("\\*");
                    csvData[j][1] = temp[temp.length - 1];
                } else if (contentList[i].startsWith("QTY*")) {
                    String[] temp = contentList[i].split("\\*");
                    csvData[j][2] = temp[1];
                    j++;
                }
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("fileName", vendorName + receiverId + ".csv");
            resultMap.put("csvData", new ArrayList<>(Arrays.asList(csvData)));
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}






