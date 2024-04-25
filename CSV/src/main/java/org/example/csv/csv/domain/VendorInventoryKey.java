package org.example.csv.csv.domain;


import jakarta.persistence.*;

import java.io.Serializable;

public class VendorInventoryKey implements Serializable {

    private String sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private VendorDetail vendor;

    public VendorInventoryKey() {
    }

    public VendorInventoryKey(String sku, VendorDetail vendor, Integer quantity, Integer unitCost, String productTitle, String upc) {
        this.sku = sku;
        this.vendor = vendor;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public VendorDetail getVendor() {
        return vendor;
    }

    public void setVendor(VendorDetail vendor) {
        this.vendor = vendor;
    }
}
