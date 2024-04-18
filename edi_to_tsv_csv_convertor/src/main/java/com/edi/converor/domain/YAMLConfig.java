package com.edi.converor.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "edi")
public class YAMLConfig {

    private String senderId;
    private String receiverId;
    private String standardsIdentifier;
    private String versionNumber;
    private String controlNumber;
    private String ackRequested;
    private String usageIndicator;
    private String functionalIdentifierCode;
    private String groupControlNumber;
    private String agencyCode;
    private String industryCode;
    private String transactionIdentifierCode;
    private String transactionSetControlNumber;
    private String transactionSetPurposeCode;
    private String reportTypeCode;
    private String biaReferenceIdentification;
    private String referenceIdentificationQualifier;
    private String referenceIdentification;
    private String entityIdentifierCode;
    private String productIdQualifier;
    private String productIdQualifierForVendor;
    private String itemDescriptionType;
    private String productCharacteristicCode;
    private String productCharacteristicCodeForBuyer;
    private String priceIdentifyingCode;
    private String quantityQualifier;
    private String unitCode;
    private String dateTimeQualifier;
    private String TransactionTotal;
    private String NumberOfIncludedSegments;
    private String TransactionSetControl;
    private String NumberOfTransactionSetIncluded;
    private String NumberOfFunctionalGroups;


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getStandardsIdentifier() {
        return standardsIdentifier;
    }

    public void setStandardsIdentifier(String standardsIdentifier) {
        this.standardsIdentifier = standardsIdentifier;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    public void setControlNumber(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    public String getAckRequested() {
        return ackRequested;
    }

    public void setAckRequested(String ackRequested) {
        this.ackRequested = ackRequested;
    }

    public String getUsageIndicator() {
        return usageIndicator;
    }

    public void setUsageIndicator(String usageIndicator) {
        this.usageIndicator = usageIndicator;
    }

    public String getFunctionalIdentifierCode() {
        return functionalIdentifierCode;
    }

    public void setFunctionalIdentifierCode(String functionalIdentifierCode) {
        this.functionalIdentifierCode = functionalIdentifierCode;
    }

    public String getGroupControlNumber() {
        return groupControlNumber;
    }

    public void setGroupControlNumber(String groupControlNumber) {
        this.groupControlNumber = groupControlNumber;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public String getTransactionIdentifierCode() {
        return transactionIdentifierCode;
    }

    public void setTransactionIdentifierCode(String transactionIdentifierCode) {
        this.transactionIdentifierCode = transactionIdentifierCode;
    }

    public String getTransactionSetControlNumber() {
        return transactionSetControlNumber;
    }

    public void setTransactionSetControlNumber(String transactionSetControlNumber) {
        this.transactionSetControlNumber = transactionSetControlNumber;
    }

    public String getTransactionSetPurposeCode() {
        return transactionSetPurposeCode;
    }

    public void setTransactionSetPurposeCode(String transactionSetPurposeCode) {
        this.transactionSetPurposeCode = transactionSetPurposeCode;
    }

    public String getReportTypeCode() {
        return reportTypeCode;
    }

    public void setReportTypeCode(String reportTypeCode) {
        this.reportTypeCode = reportTypeCode;
    }

    public String getBiaReferenceIdentification() {
        return biaReferenceIdentification;
    }

    public void setBiaReferenceIdentification(String biaReferenceIdentification) {
        this.biaReferenceIdentification = biaReferenceIdentification;
    }

    public String getReferenceIdentificationQualifier() {
        return referenceIdentificationQualifier;
    }

    public void setReferenceIdentificationQualifier(String referenceIdentificationQualifier) {
        this.referenceIdentificationQualifier = referenceIdentificationQualifier;
    }

    public String getReferenceIdentification() {
        return referenceIdentification;
    }

    public void setReferenceIdentification(String referenceIdentification) {
        this.referenceIdentification = referenceIdentification;
    }

    public String getEntityIdentifierCode() {
        return entityIdentifierCode;
    }

    public void setEntityIdentifierCode(String entityIdentifierCode) {
        this.entityIdentifierCode = entityIdentifierCode;
    }

    public String getProductIdQualifier() {
        return productIdQualifier;
    }

    public void setProductIdQualifier(String productIdQualifier) {
        this.productIdQualifier = productIdQualifier;
    }

    public String getProductIdQualifierForVendor() {
        return productIdQualifierForVendor;
    }

    public void setProductIdQualifierForVendor(String productIdQualifierForVendor) {
        this.productIdQualifierForVendor = productIdQualifierForVendor;
    }

    public String getItemDescriptionType() {
        return itemDescriptionType;
    }

    public void setItemDescriptionType(String itemDescriptionType) {
        this.itemDescriptionType = itemDescriptionType;
    }

    public String getProductCharacteristicCode() {
        return productCharacteristicCode;
    }

    public void setProductCharacteristicCode(String productCharacteristicCode) {
        this.productCharacteristicCode = productCharacteristicCode;
    }

    public String getProductCharacteristicCodeForBuyer() {
        return productCharacteristicCodeForBuyer;
    }

    public void setProductCharacteristicCodeForBuyer(String productCharacteristicCodeForBuyer) {
        this.productCharacteristicCodeForBuyer = productCharacteristicCodeForBuyer;
    }

    public String getPriceIdentifyingCode() {
        return priceIdentifyingCode;
    }

    public void setPriceIdentifyingCode(String priceIdentifyingCode) {
        this.priceIdentifyingCode = priceIdentifyingCode;
    }

    public String getQuantityQualifier() {
        return quantityQualifier;
    }

    public void setQuantityQualifier(String quantityQualifier) {
        this.quantityQualifier = quantityQualifier;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getDateTimeQualifier() {
        return dateTimeQualifier;
    }

    public void setDateTimeQualifier(String dateTimeQualifier) {
        this.dateTimeQualifier = dateTimeQualifier;
    }

    public String getTransactionTotal() {
        return TransactionTotal;
    }

    public void setTransactionTotal(String transactionTotal) {
        TransactionTotal = transactionTotal;
    }

    public String getNumberOfIncludedSegments() {
        return NumberOfIncludedSegments;
    }

    public void setNumberOfIncludedSegments(String numberOfIncludedSegments) {
        NumberOfIncludedSegments = numberOfIncludedSegments;
    }

    public String getTransactionSetControl() {
        return TransactionSetControl;
    }

    public void setTransactionSetControl(String transactionSetControl) {
        TransactionSetControl = transactionSetControl;
    }

    public String getNumberOfTransactionSetIncluded() {
        return NumberOfTransactionSetIncluded;
    }

    public void setNumberOfTransactionSetIncluded(String numberOfTransactionSetIncluded) {
        NumberOfTransactionSetIncluded = numberOfTransactionSetIncluded;
    }

    public String getNumberOfFunctionalGroups() {
        return NumberOfFunctionalGroups;
    }

    public void setNumberOfFunctionalGroups(String numberOfFunctionalGroups) {
        NumberOfFunctionalGroups = numberOfFunctionalGroups;
    }


}
