package org.example.csv.csv.services;

import org.example.csv.csv.dto.VendorDetailDto;

import java.util.List;

public interface VendorDetailServices {

    String addVendorDetail(VendorDetailDto vendorDetailDto);

    List<VendorDetailDto> getAllVendorDetails();

    VendorDetailDto getVendorDetailById(int id);

    String deleteVendorDetailById(int id);

    String updateVendorDetailById(int id, VendorDetailDto vendorDetailDto);
}
