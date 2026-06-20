package com.fmb.analyzer.model;

/**
 * Represents an FMB Endpoint extracted from XML
 */
public class FMBEndpoint {
    private String name;
    private String endpoint;
    private String category;
    private String method;
    private String description;
    private boolean isDirect;
    private boolean isIndirect;
    private String businessLogic;
    private String commitLogic;
    private String historyTracking;

    public FMBEndpoint() {
    }

    public FMBEndpoint(String name, String endpoint, String category, String method) {
        this.name = name;
        this.endpoint = endpoint;
        this.category = category;
        this.method = method;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public void setDirect(boolean direct) {
        isDirect = direct;
    }

    public boolean isIndirect() {
        return isIndirect;
    }

    public void setIndirect(boolean indirect) {
        isIndirect = indirect;
    }

    public String getBusinessLogic() {
        return businessLogic;
    }

    public void setBusinessLogic(String businessLogic) {
        this.businessLogic = businessLogic;
    }

    public String getCommitLogic() {
        return commitLogic;
    }

    public void setCommitLogic(String commitLogic) {
        this.commitLogic = commitLogic;
    }

    public String getHistoryTracking() {
        return historyTracking;
    }

    public void setHistoryTracking(String historyTracking) {
        this.historyTracking = historyTracking;
    }

    @Override
    public String toString() {
        return "FMBEndpoint{" +
                "name='" + name + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", category='" + category + '\'' +
                ", method='" + method + '\'' +
                ", isDirect=" + isDirect +
                ", isIndirect=" + isIndirect +
                '}';
    }
}
