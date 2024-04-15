package com.tsv.convertor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class TsvConvertorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TsvConvertorApplication.class, args);
    }

    @Override
    public void run(String[] args) throws Exception {

        File resource = ResourceUtils.getFile("classpath:EDI_files/239_GoGoTech_846_163863.edi");
        InputStream stream = new FileInputStream(resource);
        Scanner scanner = new Scanner(stream);

        List<String> dataLines = new ArrayList<>();

        while (scanner.hasNextLine()) {
            dataLines.add(scanner.nextLine());
        }

        int cont = (int) dataLines.stream().filter(data -> data.contains("LIN*")).count();
        String name = null;
        String receiverNumber = null;

        for (int i = 0; i < dataLines.size(); i++) {
            if (i == 0) {
                String singleLine = dataLines.get(i);
                String[] singleData = singleLine.split("\\*");
                receiverNumber = singleData[8].trim();
            } else if (i == 5) {
                String singleLine = dataLines.get(i);
                String[] singleData = singleLine.split("\\*");
                name = singleData[2].replaceAll("[^a-zA-Z0-9.]", "_");
                break;
            }
        }
        String fileName = receiverNumber + "_" + name.replace(".","");
//        System.out.println(fileName);

        StringBuilder tsv = new StringBuilder();

        String tsvFileName = "src/main/resources/TSV_files/" + fileName + ".tsv";
        File tsvFile = new File(tsvFileName);

        try(FileWriter tsvWriter = new FileWriter(tsvFile)) {
            tsvWriter.write("ProductCode\tProductName\tPrice\tQuantity\n");

            for (int i = 6; i < dataLines.size(); i++) {
                String singleLine = dataLines.get(i);
                if (singleLine.contains("PID") && singleLine.contains("08")) {
                    String[] singleData = singleLine.split("\\*");
                    tsv.append(singleData[singleData.length - 1].trim()).append("\t");
                } else if (singleLine.contains("LIN") && singleLine.contains("VP")) {
                    String[] singleData = singleLine.split("\\*");
                    tsv.append(singleData[singleData.length - 1].trim()).append("\t");
                } else if (singleLine.contains("CTP")) {
                    String[] singleData = singleLine.split("\\*");
                    tsv.append(singleData[singleData.length - 1].trim()).append("\t");
                } else if (singleLine.contains("QTY")) {
                    String[] singleData = singleLine.split("\\*");
                    tsv.append(singleData[singleData.length - 2].trim()).append("\n");
                    tsvWriter.write(String.valueOf(tsv));
                    System.out.println(tsv);
                    tsv.delete(0, tsv.length());
                }
            }
        }
        scanner.close();
        stream.close();
    }
}
