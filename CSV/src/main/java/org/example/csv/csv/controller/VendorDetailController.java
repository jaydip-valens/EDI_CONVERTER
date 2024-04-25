package org.example.csv.csv.controller;

import org.example.csv.csv.dto.VendorDetailDto;
import org.example.csv.csv.services.VendorDetailServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
public class VendorDetailController {

    @Autowired
    private VendorDetailServices vendorDetailServices;

    @PostMapping()
    public ResponseEntity<String> addVendorDetail(@RequestBody VendorDetailDto vendorDetailDto) {
        try {
            String result = vendorDetailServices.addVendorDetail(vendorDetailDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<VendorDetailDto>> getAllVendorDetails() {
        try {
            List<VendorDetailDto> details = vendorDetailServices.getAllVendorDetails();
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorDetailDto> getVendorDetailById(@PathVariable int id) {
        try {
            VendorDetailDto detail = vendorDetailServices.getVendorDetailById(id);
            if (detail == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVendorDetailById(@PathVariable int id) {
        try {
            String result = vendorDetailServices.deleteVendorDetailById(id);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVendorDetailById(@PathVariable int id, @RequestBody VendorDetailDto vendorDetailDto) {
        try {
            String result = vendorDetailServices.updateVendorDetailById(id, vendorDetailDto);
            if (result.equals("Invalid id")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
