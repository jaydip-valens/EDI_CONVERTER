package org.example.csv.csv.services.Implimentation.ridham_implimentation;

import org.example.csv.csv.domain.ridham_domain.YamlConfig;
import org.example.csv.csv.services.ridham_service.CsvToEdiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

@Service
public class CsvToEdiServiceImpl implements CsvToEdiService {


    @Autowired
    private YamlConfig yamlConfig;
    static Random rand = new Random();
    static LocalDateTime localDateTime = LocalDateTime.now();

    private static final Logger logger = LoggerFactory.getLogger(CsvToEdiServiceImpl.class);

    @Override
    public File csvToEdi(MultipartFile csvFile) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        String date = dateFormatter.format(localDateTime);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = timeFormatter.format(localDateTime);

        String fileName = csvFile.getOriginalFilename();

        int startIndex, endIndex;
        startIndex = fileName.indexOf("_");
        endIndex = fileName.indexOf(".");

        String supplierName = fileName.substring(startIndex + 1, endIndex).replaceAll("_", " ");

        String isaSegmentValue = getIsaSegmentValue(date, time);
        String gsSegmentValue = getGsSegmentValue();
        String stSegmentValue = getStSegmentValue();
        String biaSegmentValue = getBiaSegmentValue();
        String refSegmentValue = getRefSegmentValue();
        String n1SegmentValue = getN1SegmentValue();

        String cttSegmentValue = getCttSegmentValue();
        String seSegmentValue = getSeSegmentValue();
        String geSegmentValue = getGeSegmentValue();
        String ieaSegmentValue = getIeaSegmentValue();

        supplierName = supplierName + ".edi";
        File ediFile = new File(supplierName);

        logger.info("Start reading data from CSV file.");
        try (InputStream stream = csvFile.getInputStream();
             Scanner scanner = new Scanner(stream);
             FileWriter ediWriter = new FileWriter(ediFile)) {
            logger.info("Start writing data into EDI file.");
            ediWriter.write(isaSegmentValue + "~" + gsSegmentValue + "~" + stSegmentValue + "~" +
                    biaSegmentValue + "~" + refSegmentValue + "~" + n1SegmentValue + "~");
            scanner.nextLine();
            while (scanner.hasNext()) {
                long productId = rand.nextLong(100000000000L, 999999999999L);
                String data = scanner.nextLine();
                String[] itemDetails = data.split(",");
                String linSegmentValue = getLinSegmentValue(productId, itemDetails);
                String pidProductSegmentValue = getPidSegmentValue(itemDetails);
                String pidColorSegmentValue = getPidColorSegmentValue(itemDetails);
                String ctpSegmentValue = getCtpSegmentValue(itemDetails);
                String qtySegmentValue = getQtySegmentValue(itemDetails);
                String dtmSegmentValue = getDtmSegmentValue(date);
                ediWriter.write(linSegmentValue + "~" + pidProductSegmentValue + "~" +
                        pidColorSegmentValue + "~" + ctpSegmentValue + "~" +
                        qtySegmentValue + "~" + dtmSegmentValue + "~");
            }
            ediWriter.write(cttSegmentValue + "~" + seSegmentValue + "~" + geSegmentValue + "~" + ieaSegmentValue);
            logger.info("CSV file successfully converted into EDI file.");
        } catch (Exception e) {
            logger.error("Runtime error: {}",e.getMessage());
            throw new RuntimeException();
        }
        return ediFile;
    }

    private String getIeaSegmentValue() {
        String ieaSegmentValue = "IEA*" + yamlConfig.getNumberOfFunctionalGroups() + "*" +
                yamlConfig.getControlNumber();
        return ieaSegmentValue;
    }

    private String getGeSegmentValue() {
        String geSegmentValue = "GE*" + yamlConfig.getNumberOfTransactionSetIncluded() + "*" +
                yamlConfig.getGroupControlNumber();
        return geSegmentValue;
    }

    private String getSeSegmentValue() {
        String seSegmentValue = "SE*" + yamlConfig.getNumberOfIncludedSegments() + "*" +
                yamlConfig.getTransactionSetControl();
        return seSegmentValue;
    }

    private String getCttSegmentValue() {
        String cttSegmentValue = "CTT*" + yamlConfig.getTransactionTotal();
        return cttSegmentValue;
    }

    private String getN1SegmentValue() {
        String n1SegmentValue = "N1*" + yamlConfig.getEntityIdentifierCode() + "*";
        return n1SegmentValue;
    }

    private String getRefSegmentValue() {
        String refSegmentValue = "REF*" + yamlConfig.getReferenceIdentificationQualifier() + "*" +
                yamlConfig.getReferenceIdentification();
        return refSegmentValue;
    }

    private String getBiaSegmentValue() {
        String biaSegmentValue = "BIA*" + yamlConfig.getTransactionSetPurposeCode() + "*" +
                yamlConfig.getReportTypeCode() + "*" + yamlConfig.getBiaReferenceIdentification() + "*20190417";
        return biaSegmentValue;
    }

    private String getStSegmentValue() {
        String stSegmentValue = "ST*" + yamlConfig.getTransactionIdentifierCode() + "*" +
                yamlConfig.getTransactionSetControlNumber();
        return stSegmentValue;
    }

    private String getGsSegmentValue() {
        String gsSegmentValue = "GS*" + yamlConfig.getFunctionalIdentifierCode() + "*" + yamlConfig.getReceiverId() + "*" +
                yamlConfig.getSenderId() + "*20190417*1101*" + yamlConfig.getGroupControlNumber() + "*" +
                yamlConfig.getAgencyCode() + "*" + yamlConfig.getIndustryCode();
        return gsSegmentValue;
    }

    private String getIsaSegmentValue(String date, String time) {
        String isaSegmentValue = "ISA*00*               *00*               *01*" + yamlConfig.getSenderId() + "*12*" +
                yamlConfig.getReceiverId() + "*" + date + "*" + time + "*" + yamlConfig.getStandardsIdentifier() + "*" +
                yamlConfig.getVersionNumber() + "*" + yamlConfig.getControlNumber() + "*" + yamlConfig.getAckRequested() + "*" +
                yamlConfig.getUsageIndicator() + "*~";
        return isaSegmentValue;
    }

    private String getDtmSegmentValue(String date) {
        return "DTM*" + yamlConfig.getDateTimeQualifier() + "*" + date;
    }

    private String getQtySegmentValue(String[] itemDetails) {
        String qtySegmentValue = "QTY*" + yamlConfig.getQuantityQualifier() + "*" +
                itemDetails[3].replaceAll("\"", "") + "*" +
                yamlConfig.getUnitCode();
        return qtySegmentValue;
    }

    private String getCtpSegmentValue(String[] itemDetails) {
        String ctpSegmentValue = "CTP**" + yamlConfig.getPriceIdentifyingCode() + "*" +
                itemDetails[2].replaceAll("\"", "");
        return ctpSegmentValue;
    }

    private String getPidColorSegmentValue(String[] itemDetails) {
        String pidColorSegmentValue = "PID*" + yamlConfig.getItemDescriptionType() + "*" +
                yamlConfig.getProductCharacteristicCodeForBuyer() +
                "***" + itemDetails[1].replaceAll("\"", "");
        return pidColorSegmentValue;
    }

    private String getPidSegmentValue(String[] itemDetails) {
        String pidProductSegmentValue = "PID*" + yamlConfig.getItemDescriptionType() + "*" +
                yamlConfig.getProductCharacteristicCode() + "***" +
                itemDetails[1].replaceAll("\"", "");
        return pidProductSegmentValue;
    }

    private String getLinSegmentValue(long productId, String[] itemDetails) {
        String linSegmentValue = "LIN**" + yamlConfig.getProductIdQualifier() + "*" + productId + "***" +
                yamlConfig.getProductIdQualifierForVendor() + "*" +
                itemDetails[0].replaceAll("\"", "");
        return linSegmentValue;
    }
}
