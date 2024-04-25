package org.example.csv.csv.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "vendor_details")
public class VendorDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "data_setting", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> dataSetting;

    @Column(name = "data_mapping", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> dataMapping;

    @OneToMany(mappedBy = "vendor")
    private List<VendorInventory> vendorInventory;

    public VendorDetail() {
    }

    public VendorDetail(Integer id, String name, Map<String, Object> dataSetting, Map<String, Object> dataMapping) {
        this.id = id;
        this.name = name;
        this.dataSetting = dataSetting;
        this.dataMapping = dataMapping;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getDataSetting() {
        return dataSetting;
    }

    public void setDataSetting(Map<String, Object> dataSetting) {
        this.dataSetting = dataSetting;
    }

    public Map<String, Object> getDataMapping() {
        return dataMapping;
    }

    public void setDataMapping(Map<String, Object> dataMapping) {
        this.dataMapping = dataMapping;
    }

    public List<VendorInventory> getVendorInventory() {
        return vendorInventory;
    }

    public void setVendorInventory(List<VendorInventory> vendorInventory) {
        this.vendorInventory = vendorInventory;
    }

    @Override
    public String toString() {
        return "VendorDetail{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dataSetting=" + dataSetting +
                ", dataMapping=" + dataMapping +
                '}';
    }
}