package org.example.csv.csv.services.Implimentation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.csv.csv.domain.VendorDetail;
import org.example.csv.csv.dto.VendorDetailDto;
import org.example.csv.csv.repository.VendorDetailRepository;
import org.example.csv.csv.services.VendorDetailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorDetailServicesImplementation implements VendorDetailServices {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private VendorDetailRepository vendorDetailRepository;

    @Override
    public void addVendorDetail(VendorDetailDto vendorDetailDto) {
        try {
            vendorDetailRepository.save(objectMapper.convertValue(vendorDetailDto, VendorDetail.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<VendorDetailDto> getAllVendorDetails() {
        try {
            return objectMapper.convertValue(vendorDetailRepository.findAll(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VendorDetailDto getVendorDetailById(int id) {
        try {
            Optional<VendorDetail> optionalVendorDetail = vendorDetailRepository.findById(id);
            return optionalVendorDetail.map(vendorDetail -> objectMapper.convertValue(vendorDetail, VendorDetailDto.class)).orElse(null);
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
}
