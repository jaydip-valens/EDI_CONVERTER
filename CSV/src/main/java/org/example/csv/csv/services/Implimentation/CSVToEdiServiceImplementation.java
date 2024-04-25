package org.example.csv.csv.services.Implimentation;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.ArrayUtils;
import org.example.csv.csv.domain.EDIConfigurationValues;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.services.CSVToEdiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.example.csv.csv.utils.Util.createStringForSegment;
import static org.example.csv.csv.utils.Util.validateFile;

@Service
public class CSVToEdiServiceImplementation implements CSVToEdiServices {

    @Autowired
    private EDIConfigurationValues ediConfigurationValues;

    private static final Logger logger = LoggerFactory.getLogger(CSVToEdiServiceImplementation.class);
    private static final Random random = new Random();

    public synchronized File csvToEdiConverter(MultipartFile csvFile) throws FileNotFoundException {
        try {
            validateFile(csvFile);
            logger.info("Starting CSV to EDI conversion");
            LocalDateTime DateTime = LocalDateTime.now();
            int linCount = 0;
            int segmentCount = 0;
            FileOutputStream fileOutputStream = new FileOutputStream(csvFile.getOriginalFilename());
            fileOutputStream.write(csvFile.getBytes());
            fileOutputStream.close();
            try (CSVReader reader = new CSVReader(new FileReader(csvFile.getOriginalFilename()))) {
                List<String[]> csvData = reader.readAll();
                String[] nameArray = ((csvFile.getOriginalFilename().split("[._]")));
                String vendorName = String.join(" ", ArrayUtils.removeAll(nameArray, 0, nameArray.length - 1));
                String fileName = ediConfigurationValues.getReceiverId() + "_" + vendorName.replace(" ", "_") + ".edi";
                try (FileWriter writer = new FileWriter(fileName)) {
                    List<String> segmentData = new ArrayList<>();
                    isaSegmentWriter(segmentData, DateTime, writer);
                    gsSegmentWriter(segmentData, DateTime, writer);
                    stSegmentWriter(segmentData, writer);
                    biaSegmentWriter(segmentData, DateTime, writer);
                    refSegmentWriter(segmentData, writer);
                    n1SegmentWriter(segmentData, vendorName, writer);
                    List<String> segmentNames = new ArrayList<>(Arrays.asList(csvData.get(0)));
                    csvData.remove(0);
                    for (String[] data : csvData) {
                        if (segmentNames.contains("Vendor")) {
                            long randomValue = random.nextLong(1000000000000L);
                            linCount++;
                            segmentCount++;
                            linSegmentWriter(data, segmentData, randomValue, segmentNames, writer);
                        }
                        if (segmentNames.contains("Product Name")) {
                            segmentCount = pidSegmentWriter(data, segmentData, segmentNames, segmentCount, writer);
                        }
                        if (segmentNames.contains("Cost")) {
                            segmentCount = ctpSegmentWriter(data, segmentData, segmentNames, segmentCount, writer);
                        }
                        if (segmentNames.contains("Quantity")) {
                            segmentCount = qtySegmentWriter(data, segmentData, segmentNames, segmentCount, writer);
                        }
                        segmentCount = dtmSegmentWriter(segmentData, DateTime, segmentCount, writer);
                    }
                    cttSegmentWriter(segmentData, linCount, writer);
                    seSegmentWriter(segmentData, segmentCount, writer);
                    geSegmentWriter(segmentData, writer);
                    ieaSegmentWriter(segmentData, writer);
                } catch (Exception e) {
                    logger.error("Error occurred during CSV to EDI conversion.", e);
                    throw new RuntimeException(e);
                }
                logger.info("CSV to EDI conversion completed successfully.");
                return new File(fileName);
            } catch (IOException e) {
                logger.error("Error occurred during CSV to EDI conversion.", e);
                throw new RuntimeException(e);
            }
        } catch (InvalidFileException e) {
            logger.error("Invalid file encountered during CSV to EDI conversion.", e);
            throw new InvalidFileException();
        } catch (Exception e) {
            logger.error("Error occurred during CSV to EDI conversion.", e);
            throw new RuntimeException(e);
        }
    }

    private void linSegmentWriter(String[] data, List<String> segmentData, long randomValue, List<String> segmentNames, FileWriter writer) throws IOException {
        segmentData.add("LIN*");
        segmentData.add(ediConfigurationValues.getLinProductServiceIDQualifier());
        segmentData.add(String.valueOf(randomValue));
        segmentData.add("*");
        segmentData.add(ediConfigurationValues.getLinProductServiceIDQualifierSecond());
        segmentData.add(data[segmentNames.indexOf("vendor")]);
        String LIN = String.join("*", segmentData);
        writer.write(LIN + "~\n");
        segmentData.clear();
    }

    private void ieaSegmentWriter(List<String> segmentData, FileWriter writer) throws IOException {
        segmentData.add("IEA");
        segmentData.add("1");
        segmentData.add(ediConfigurationValues.getIsaInterchangeControlNumber());
        String IEA = String.join("*", segmentData);
        writer.write(IEA + "~\n");
        segmentData.clear();
    }

    private void geSegmentWriter(List<String> segmentData, FileWriter writer) throws IOException {
        segmentData.add("GE");
        segmentData.add("1");
        segmentData.add(ediConfigurationValues.getGsGroupControlNumber());
        String GE = String.join("*", segmentData);
        writer.write(GE + "~\n");
        segmentData.clear();
    }

    private void seSegmentWriter(List<String> segmentData, int segmentCount, FileWriter writer) throws IOException {
        segmentData.add("SE");
        segmentData.add(String.valueOf(segmentCount + 6));
        segmentData.add(ediConfigurationValues.getStTransactionSetControlNumber());
        String SE = String.join("*", segmentData);
        writer.write(SE + "~\n");
        segmentData.clear();
    }

    private int dtmSegmentWriter(List<String> segmentData, LocalDateTime DateTime, int segmentCount, FileWriter writer) throws IOException {
        segmentData.add("DTM");
        segmentData.add(ediConfigurationValues.getDtmDateTimeQualifier());
        segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        segmentCount++;
        String DTM = String.join("*", segmentData);
        writer.write(DTM + "~\n");
        segmentData.clear();
        return segmentCount;
    }

    private int qtySegmentWriter(String[] data, List<String> segmentData, List<String> segmentNames, int segmentCount, FileWriter writer) throws IOException {
        segmentData.add("QTY");
        segmentData.add(ediConfigurationValues.getQtyQuantityQualifier());
        segmentData.add(data[segmentNames.indexOf("Quantity")]);
        segmentData.add(ediConfigurationValues.getQtyMeasurementCode());
        segmentCount++;
        String QTY = String.join("*", segmentData);
        writer.write(QTY + "~\n");
        segmentData.clear();
        return segmentCount;
    }

    private int ctpSegmentWriter(String[] data, List<String> segmentData, List<String> segmentNames, int segmentCount, FileWriter writer) throws IOException {
        segmentData.add("CTP*");
        segmentData.add(ediConfigurationValues.getCtpPriceIdentifierCode());
        segmentData.add(data[segmentNames.indexOf("Cost")]);
        segmentCount++;
        String CTP = String.join("*", segmentData);
        writer.write(CTP + "~\n");
        segmentData.clear();
        return segmentCount;
    }

    private void n1SegmentWriter(List<String> segmentData, String vendorName, FileWriter writer) throws IOException {
        segmentData.add("N1");
        segmentData.add(ediConfigurationValues.getN1EntityIdentifierCode());
        segmentData.add(vendorName.isBlank() ? ediConfigurationValues.getReceiverId() : vendorName);
        String N1 = String.join("*", segmentData);
        writer.write(N1 + "~\n");
        segmentData.clear();
    }

    private void refSegmentWriter(List<String> segmentData, FileWriter writer) throws IOException {
        segmentData.add("REF");
        segmentData.add(ediConfigurationValues.getRefReferenceIdentificationQualifier());
        segmentData.add(ediConfigurationValues.getRefReferenceIdentification());
        String REF = String.join("*", segmentData);
        writer.write(REF + "~\n");
        segmentData.clear();
    }

    private void biaSegmentWriter(List<String> segmentData, LocalDateTime DateTime, FileWriter writer) throws IOException {
        segmentData.add("BIA");
        segmentData.add(ediConfigurationValues.getBiaTransactionSetPurposeCode());
        segmentData.add("SI");
        segmentData.add(ediConfigurationValues.getBiaReferenceIdentification());
        segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String BIA = String.join("*", segmentData);
        writer.write(BIA + "~\n");
        segmentData.clear();
    }

    private void stSegmentWriter(List<String> segmentData, FileWriter writer) throws IOException {
        segmentData.add("ST*846");
        segmentData.add(ediConfigurationValues.getStTransactionSetControlNumber());
        String ST = String.join("*", segmentData);
        writer.write(ST + "~\n");
        segmentData.clear();
    }

    private void gsSegmentWriter(List<String> segmentData, LocalDateTime DateTime, FileWriter writer) throws IOException {
        segmentData.add("GS*IB");
        segmentData.add(ediConfigurationValues.getReceiverId());
        segmentData.add(ediConfigurationValues.getSenderId());
        segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("HHmm")));
        segmentData.add(ediConfigurationValues.getGsGroupControlNumber());
        segmentData.add(ediConfigurationValues.getGsResponsibleAgencyCode());
        segmentData.add(ediConfigurationValues.getGsVersionIdentifierCode());
        String GS = String.join("*", segmentData);
        writer.write(GS + "~\n");
        segmentData.clear();
    }

    private void isaSegmentWriter(List<String> segmentData, LocalDateTime DateTime, FileWriter writer) throws IOException {
        segmentData.add("ISA");
        segmentData.add(ediConfigurationValues.getIsaAuthorizationInformationQualifier());
        segmentData.add(createStringForSegment(ediConfigurationValues.getIsaAuthorizationInformation(), 10));
        segmentData.add(ediConfigurationValues.getIsaSecurityInformationQualifier());
        segmentData.add(createStringForSegment(ediConfigurationValues.getIsaSecurityInformation(), 10));
        segmentData.add(ediConfigurationValues.getIsaInterchangeIDQualifier());
        segmentData.add(createStringForSegment(ediConfigurationValues.getSenderId(), 15));
        segmentData.add("12");
        segmentData.add(createStringForSegment(ediConfigurationValues.getReceiverId(), 15));
        segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("yyMMdd")));
        segmentData.add(DateTime.format(DateTimeFormatter.ofPattern("HHmm")));
        segmentData.add("U");
        segmentData.add(ediConfigurationValues.getIsaInterchangeControlVersionNumber());
        segmentData.add(ediConfigurationValues.getIsaInterchangeControlNumber());
        segmentData.add(ediConfigurationValues.getIsaAcknowledgmentRequested());
        segmentData.add(ediConfigurationValues.getIsaUsageIndicator());
        String ISA = String.join("*", segmentData);
        writer.write(ISA + "~\n");
        segmentData.clear();
    }

    private static void cttSegmentWriter(List<String> segmentData, int linCount, FileWriter writer) throws IOException {
        segmentData.add("CTT");
        segmentData.add(String.valueOf(linCount));
        String CTT = String.join("*", segmentData);
        writer.write(CTT + "~\n");
        segmentData.clear();
    }

    private static int pidSegmentWriter(String[] data, List<String> segmentData, List<String> segmentNames, int segmentCount, FileWriter writer) throws IOException {
        segmentData.add("PID*F");
        segmentData.add("08");
        segmentData.add("*");
        segmentData.add(data[segmentNames.indexOf("Product Name")]);
        segmentCount++;
        String PID = String.join("*", segmentData);
        writer.write(PID + "~\n");
        segmentData.clear();
        return segmentCount;
    }

}
