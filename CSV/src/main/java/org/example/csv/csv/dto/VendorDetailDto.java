package org.example.csv.csv.dto;

import java.util.Map;

public class VendorDetailDto  {
    private Integer id;

    private String name;

    private Map<String, Object> dataSetting;
    private Map<String, Object> dataMapping;

    public VendorDetailDto() {
    }

    public VendorDetailDto(Integer id, String name, Map<String, Object> dataSetting, Map<String, Object> dataMapping) {
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "dataSetting = " + dataSetting + ", " +
                "dataMapping = " + dataMapping + ")";
    }

    public String getName() {
        return name;
    }
}