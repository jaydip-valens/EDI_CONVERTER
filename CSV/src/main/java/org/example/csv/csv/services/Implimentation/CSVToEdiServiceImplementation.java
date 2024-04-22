package org.example.csv.csv.services.Implimentation;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.ArrayUtils;
import org.example.csv.csv.domain.EDIConfigurationValues;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.services.CSVToEdiServices;
import org.example.csv.csv.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.example.csv.csv.utils.Util.validateFile;

@Service
public class CSVToEdiServiceImplementation implements CSVToEdiServices {

    @Autowired
    private EDIConfigurationValues ediConfigurationValues;

    @Autowired
    private Util util;


    public File csvToEdiConverter(MultipartFile csvFile) throws IOException {
        FileWriter writer = null;
        CSVReader reader = null;
        try {
            validateFile(csvFile, "csv");
            LocalDateTime DateTime = LocalDateTime.now();
            int linCount = 0;
            int segmentCount = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(csvFile.getOriginalFilename());
            fileOutputStream.write(csvFile.getBytes());
            fileOutputStream.close();

            reader = new CSVReader(new FileReader(csvFile.getOriginalFilename()));
            List<String[]> csvData = reader.readAll();

            String[] NameArray = ((csvFile.getOriginalFilename().split("[._]")));
            String vendorName = String.join(" ", ArrayUtils.removeAll(NameArray, 0, NameArray.length - 1));
            String fileName = ediConfigurationValues.getReceiverId() + "_" + vendorName.replace(" ", "_") + ".edi";
            writer = new FileWriter(fileName);

            List<String> segmentData = new ArrayList<>();
            segmentData.add("ISA");
            segmentData.add(ediConfigurationValues.getIsaAuthorizationInformationQualifier());
            segmentData.add(util.createStringForSegment(ediConfigurationValues.getIsaAuthorizationInformation(), 10));
            segmentData.add(ediConfigurationValues.getIsaSecurityInformationQualifier());
            segmentData.add(util.createStringForSegment(ediConfigurationValues.getIsaSecurityInformation(), 10));
            segmentData.add(ediConfigurationValues.getIsaInterchangeIDQualifier());
            segmentData.add(util.createStringForSegment(ediConfigurationValues.getSenderId(), 15));
            segmentData.add("12");
            segmentData.add(util.createStringForSegment(ediConfigurationValues.getReceiverId(), 15));
            segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyMMdd")));
            segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("HHmm")));
            segmentData.add("U");
            segmentData.add(ediConfigurationValues.getIsaInterchangeControlVersionNumber());
            segmentData.add(ediConfigurationValues.getIsaInterchangeControlNumber());
            segmentData.add(ediConfigurationValues.getIsaAcknowledgmentRequested());
            segmentData.add(ediConfigurationValues.getIsaUsageIndicator());
            segmentData.add("~");
            String ISA = String.join("*", segmentData);
            writer.write(ISA + "\n");
            segmentData.clear();

            segmentData.add("GS*IB");
            segmentData.add(ediConfigurationValues.getReceiverId());
            segmentData.add(ediConfigurationValues.getSenderId());
            segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("HHmm")));
            segmentData.add(ediConfigurationValues.getGsGroupControlNumber());
            segmentData.add(ediConfigurationValues.getGsResponsibleAgencyCode());
            segmentData.add(ediConfigurationValues.getGsVersionIdentifierCode());
            String GS = String.join("*", segmentData);
            writer.write(GS + "\n");
            segmentData.clear();

            segmentData.add("ST*846");
            segmentData.add(ediConfigurationValues.getStTransactionSetControlNumber());
            String ST = String.join("*", segmentData);
            writer.write(ST + "\n");
            segmentData.clear();

            segmentData.add("BIA");
            segmentData.add(ediConfigurationValues.getBiaTransactionSetPurposeCode());
            segmentData.add("SI");
            segmentData.add(ediConfigurationValues.getBiaReferenceIdentification());
            segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            String BIA = String.join("*", segmentData);
            writer.write(BIA + "\n");
            segmentData.clear();

            segmentData.add("REF");
            segmentData.add(ediConfigurationValues.getRefReferenceIdentificationQualifier());
            segmentData.add(ediConfigurationValues.getRefReferenceIdentification());
            String REF = String.join("*", segmentData);
            writer.write(REF + "\n");
            segmentData.clear();

            segmentData.add("N1");
            segmentData.add(ediConfigurationValues.getN1EntityIdentifierCode());
            segmentData.add(vendorName.isBlank() ? ediConfigurationValues.getReceiverId() : vendorName);
            String N1 = String.join("*", segmentData);
            writer.write(N1 + "\n");
            segmentData.clear();
            List<String> segmentNames = new ArrayList<>(Arrays.asList(csvData.get(0)));
            csvData.remove(0);
            for (String[] data : csvData) {
                if (segmentNames.contains("vendor")) {
                    Random random = new Random();
                    long randomValue = random.nextLong(1000000000000L);
                    linCount++;
                    segmentCount++;
                    segmentData.add("LIN*");
                    segmentData.add(ediConfigurationValues.getLinProductServiceIDQualifier());
                    segmentData.add(String.valueOf(randomValue));
                    segmentData.add("*");
                    segmentData.add(ediConfigurationValues.getLinProductServiceIDQualifierSecond());
                    segmentData.add(data[segmentNames.indexOf("vendor")]);
                    String LIN = String.join("*", segmentData);
                    writer.write(LIN + "\n");
                    segmentData.clear();
                }
                if (segmentNames.contains("Product Name")) {
                    segmentData.add("PID*F");
                    segmentData.add("08");
                    segmentData.add("*");
                    segmentData.add(data[segmentNames.indexOf("Product Name")]);
                    segmentCount++;
                    String PID = String.join("*", segmentData);
                    writer.write(PID + "\n");
                    segmentData.clear();
                }
                if (segmentNames.contains("Cost")) {
                    segmentData.add("CTP*");
                    segmentData.add(ediConfigurationValues.getCtpPriceIdentifierCode());
                    segmentData.add(data[segmentNames.indexOf("Cost")]);
                    segmentCount++;
                    String CTP = String.join("*", segmentData);
                    writer.write(CTP + "\n");
                    segmentData.clear();
                }
                if (segmentNames.contains("Quantity")) {
                    segmentData.add("QTY");
                    segmentData.add(ediConfigurationValues.getQtyQuantityQualifier());
                    segmentData.add(data[segmentNames.indexOf("Quantity")]);
                    segmentData.add(ediConfigurationValues.getQtyMeasurementCode());
                    segmentCount++;
                    String QTY = String.join("*", segmentData);
                    writer.write(QTY + "\n");
                    segmentData.clear();
                }
                segmentData.add("DTM");
                segmentData.add(ediConfigurationValues.getDtmDateTimeQualifier());
                segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                segmentCount++;
                String DTM = String.join("*", segmentData);
                writer.write(DTM + "\n");
                segmentData.clear();
            }
            segmentData.add("CTT");
            segmentData.add(String.valueOf(linCount));
            String CTT = String.join("*", segmentData);
            writer.write(CTT + "\n");
            segmentData.clear();

            segmentData.add("SE");
            segmentData.add(String.valueOf(segmentCount + 6));
            segmentData.add(ediConfigurationValues.getStTransactionSetControlNumber());
            String SE = String.join("*", segmentData);
            writer.write(SE + "\n");
            segmentData.clear();

            segmentData.add("GE");
            segmentData.add("1");
            segmentData.add(ediConfigurationValues.getGsGroupControlNumber());
            String GE = String.join("*", segmentData);
            writer.write(GE + "\n");
            segmentData.clear();

            segmentData.add("IEA");
            segmentData.add("1");
            segmentData.add(ediConfigurationValues.getIsaInterchangeControlNumber());
            String IEA = String.join("*", segmentData);
            writer.write(IEA + "\n");
            segmentData.clear();

            return new File(fileName);
        } catch (InvalidFileException e) {
            throw new InvalidFileException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null && reader != null) {
                writer.close();
                reader.close();
            }
        }
    }

}
