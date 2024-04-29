package org.example.csv.csv.services.Implimentation;

import org.example.csv.csv.domain.VendorDetail;
import org.example.csv.csv.dto.VendorDetailDto;
import org.example.csv.csv.exceptionHandler.InvalidDataException;
import org.example.csv.csv.repository.VendorDetailRepository;
import org.example.csv.csv.services.VendorDetailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VendorDetailServicesImplementation implements VendorDetailServices {

    @Autowired
    private VendorDetailRepository vendorDetailRepository;
    @Override
    public void addVendorDetail(VendorDetailDto vendorDetailDto) {
        try {
            validateDataMapping(vendorDetailDto);
            vendorDetailRepository.save(dtoToEntity(vendorDetailDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<VendorDetailDto> getAllVendorDetails() {
        try {
            return vendorDetailRepository.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VendorDetailDto getVendorDetailById(int id) {
        try {
            Optional<VendorDetail> optionalVendorDetail = vendorDetailRepository.findById(id);
            return optionalVendorDetail.map(this::entityToDto).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteVendorDetailById(int id) {
        try {
            if (!vendorDetailRepository.existsById(id)) {
                return null;
            }
            vendorDetailRepository.deleteById(id);
            return "deleted";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private VendorDetail dtoToEntity(VendorDetailDto vendorDetailDto) {
        return new VendorDetail(vendorDetailDto.getId(), vendorDetailDto.getName(), vendorDetailDto.getDataSetting(), vendorDetailDto.getDataMapping());
    }

    private VendorDetailDto entityToDto(VendorDetail vendorDetail) {
        return new VendorDetailDto(vendorDetail.getId(), vendorDetail.getName(), vendorDetail.getDataSetting(),vendorDetail.getDataMapping());
    }

    private void validateDataMapping(VendorDetailDto vendorDetailDto) {
        if (!vendorDetailDto.getDataSetting().containsKey("segment_delimiter")) {
            throw new InvalidDataException();
        }
        Set<String> keys = vendorDetailDto.getDataMapping().keySet();
        if (!keys.containsAll(List.of("vendor_sku","quantity"))) {
            throw new InvalidDataException();
        }
    }
}
