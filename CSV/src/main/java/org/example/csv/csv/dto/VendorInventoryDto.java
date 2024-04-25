package org.example.csv.csv.dto;

public class VendorInventoryDto  {
    private String sku;
    private Integer quantity;
    private Integer unitCost;
    private String productTitle;
    private String upc;

    public VendorInventoryDto(String sku, Integer quantity, Integer unitCost, String productTitle, String upc) {
        this.sku = sku;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.productTitle = productTitle;
        this.upc = upc;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Integer unitCost) {
        this.unitCost = unitCost;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "sku = " + sku + ", " +
                "quantity = " + quantity + ", " +
                "unitCost = " + unitCost + ", " +
                "productTitle = " + productTitle + ", " +
                "upc = " + upc + ")";
    }
}