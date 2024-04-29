package org.example.csv.csv.services.Implimentation;

import com.opencsv.CSVWriter;
import org.example.csv.csv.domain.VendorDetail;
import org.example.csv.csv.domain.VendorInventory;
import org.example.csv.csv.exceptionHandler.InvalidFileException;
import org.example.csv.csv.repository.VendorDetailRepository;
import org.example.csv.csv.repository.VendorInventoryRepository;
import org.example.csv.csv.services.VendorInventoryServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import static org.example.csv.csv.utils.Util.validateFile;

@Service
public class VendorInventoryServicesImplementation implements VendorInventoryServices {

    @Autowired
    private VendorInventoryRepository vendorInventoryRepository;

    @Autowired
    private VendorDetailRepository vendorDetailRepository;
    @Override
    public String addVendorInventory(MultipartFile ediFile, int id) {
        try {
            validateFile(ediFile);
            Optional<VendorDetail> optionalVendorDetail = vendorDetailRepository.findById(id);
            if (optionalVendorDetail.isEmpty()) {
                return "Invalid ID";
            }
            VendorDetail vendorDetail = optionalVendorDetail.get();
            String segmentDelimiter = vendorDetail.getDataSetting().get("segment_delimiter");
            String content = new String(ediFile.getBytes());
            String[] contentList = content.split(segmentDelimiter);
            List<VendorInventory> vendorInventories = new ArrayList<>();
            try  {
                for (String data : contentList) {
                    final VendorInventory vendorInventory = getVendorInventory(data, optionalVendorDetail.get(), vendorDetail.getDataMapping());
                    vendorInventories.add(vendorInventory);
                }
                vendorInventoryRepository.saveAll(vendorInventories);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (InvalidFileException e) {
            throw new InvalidFileException();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "added";
    }

    private  VendorInventory getVendorInventory(String data, VendorDetail vendorDetail, Map<String, String> dataMapping) {
        VendorInventory vendorInventory = new VendorInventory();
        String[] segment = data.split("\\*");
        Set<String> dataMappingKeySet = dataMapping.keySet();
        String segmentId = segment[0];
        switch (segmentId) {
            case "LIN":
                if (dataMappingKeySet.contains("vendor_sku")) {
                    int index = getIndexOfData(dataMapping, "vendor_sku");
                    vendorInventory.setSku(segment[index - 1]);
                }
                if (dataMappingKeySet.contains("vendor_upc")) {
                    int index = getIndexOfData(dataMapping, "vendor_upc");
                    vendorInventory.setSku(segment[index - 1]);
                }
                break;

            case "PID" :
                if (dataMappingKeySet.contains("product_title")) {
                    int index = getIndexOfData(dataMapping, "product_title");
                    vendorInventory.setProductTitle(segment[index - 1]);
                }

            case "CTP" :
                if (dataMappingKeySet.contains("unit_cost")) {
                    int index = getIndexOfData(dataMapping, "unit_cost");
                    vendorInventory.setUnitCost(Double.parseDouble(segment[index - 1]));
                }

            case "QTY" :
                if (dataMappingKeySet.contains("quantity")) {
                    int index = getIndexOfData(dataMapping, "quantity");
                    vendorInventory.setQuantity(Integer.parseInt(segment[index - 1]));
                }

        }
        vendorInventory.setVendor(vendorDetail);
        return vendorInventory;
    }


    private int getIndexOfData(Map<String, String> dataMapping, String key) {
        return Integer.parseInt(dataMapping.get(key).split("-")[0]);
    }
}



