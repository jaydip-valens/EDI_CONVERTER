package com.edi.converor.ServiceImpl;

import com.edi.converor.domain.YAMLConfig;
import com.edi.converor.service.CsvToEdiService;
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
    private YAMLConfig yamlConfig;

    @Override
    public File CsvToEdi(MultipartFile csvFile) throws Exception{
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        String date = dateFormatter.format(localDateTime);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
        String time = timeFormatter.format(localDateTime);

        String fileName = csvFile.getOriginalFilename();

        int startIndex = 0,endIndex = 0;
        startIndex = fileName.indexOf("_");
        endIndex = fileName.indexOf(".");

        String supplierName = fileName.substring(startIndex+1,endIndex).replaceAll("_"," ");

        String isaSegmentValue = "ISA*00*               *00*               *01*"+yamlConfig.getSenderId()+"*12*"+yamlConfig.getReceiverId()+"*"+date+"*"+time+"*"+yamlConfig.getStandardsIdentifier()+"*"+yamlConfig.getVersionNumber()+"*"+yamlConfig.getControlNumber()+"*"+yamlConfig.getAckRequested()+"*"+yamlConfig.getUsageIndicator()+"*~";
        String gsSegmentValue = "GS*"+yamlConfig.getFunctionalIdentifierCode()+"*"+yamlConfig.getReceiverId()+"*"+yamlConfig.getSenderId()+"*20190417*1101*"+yamlConfig.getGroupControlNumber()+"*"+yamlConfig.getAgencyCode()+"*"+yamlConfig.getIndustryCode();
        String stSegmentValue = "ST*"+yamlConfig.getTransactionIdentifierCode()+"*"+yamlConfig.getTransactionSetControlNumber();
        String biaSegmentValue = "BIA*"+yamlConfig.getTransactionSetPurposeCode()+"*"+yamlConfig.getReportTypeCode()+"*"+yamlConfig.getBiaReferenceIdentification()+"*20190417";
        String refSegmentValue = "REF*"+yamlConfig.getReferenceIdentificationQualifier()+"*"+yamlConfig.getReferenceIdentification();
        String n1SegmentValue = "N1*"+yamlConfig.getEntityIdentifierCode()+"*"+supplierName;

        String cttSegmentValue = "CTT*"+yamlConfig.getTransactionTotal();
        String seSegmentValue = "SE*"+yamlConfig.getNumberOfIncludedSegments()+"*"+yamlConfig.getTransactionSetControl();
        String geSegmentValue = "GE*"+yamlConfig.getNumberOfTransactionSetIncluded()+"*"+yamlConfig.getGroupControlNumber();
        String ieaSegmentValue = "IEA*"+yamlConfig.getNumberOfFunctionalGroups()+"*"+yamlConfig.getControlNumber();

        supplierName = supplierName + ".edi";
        File ediFile =new File(supplierName);

//        CSVReader csvReader = new CSVReader(new FileReader(csvFile.getOriginalFilename()));
        try(InputStream stream = csvFile.getInputStream(); Scanner scanner = new Scanner(stream); FileWriter ediWriter = new FileWriter(ediFile))  {
            ediWriter.write(isaSegmentValue+"\n"+gsSegmentValue+"\n"+stSegmentValue+"\n"+biaSegmentValue+"\n"+refSegmentValue+"\n"+n1SegmentValue+"\n");
            scanner.nextLine();
            Random rand = new Random();
            while(scanner.hasNext()){
                long productId = rand.nextLong(100000000000L,999999999999L);
                String data = scanner.nextLine();
                String[] itemDetails = data.split(",");
                String linSegmentValue = "LIN**" + yamlConfig.getProductIdQualifier() + productId + "***" + yamlConfig.getProductIdQualifierForVendor() + "*" + itemDetails[0];
                String pidProductSegmentValue = "PID*" + yamlConfig.getItemDescriptionType() + "*" + yamlConfig.getProductCharacteristicCode() + "***" + itemDetails[1];
                String pidColorSegmentValue = "PID*" + yamlConfig.getItemDescriptionType() + "*" + yamlConfig.getProductCharacteristicCodeForBuyer() + "***" + itemDetails[1];
                String ctpSegmentValue = "CTP**" + yamlConfig.getPriceIdentifyingCode() + "*" + itemDetails[2];
                String qtySegmentValue = "QTY*" + yamlConfig.getQuantityQualifier() + "*" + itemDetails[3] + "*" + yamlConfig.getUnitCode();
                String dtmSegmentValue = "DTM*" + yamlConfig.getDateTimeQualifier() + "*" + date;
                ediWriter.write(linSegmentValue+"\n"+pidProductSegmentValue+"\n"+pidColorSegmentValue+"\n"+ctpSegmentValue+"\n"+qtySegmentValue+"\n"+dtmSegmentValue+"\n");
            }
            ediWriter.write(cttSegmentValue+"\n"+seSegmentValue+"\n"+geSegmentValue+"\n"+ieaSegmentValue);
        } catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        return ediFile;
    }
}
