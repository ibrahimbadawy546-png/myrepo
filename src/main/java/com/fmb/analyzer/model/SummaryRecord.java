package com.fmb.analyzer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary record for endpoint categories
 */
public class SummaryRecord {
    private String category;
    private int count;
    private List<String> keyEndpoints;

    public SummaryRecord(String category) {
        this.category = category;
        this.count = 0;
        this.keyEndpoints = new ArrayList<>();
    }

    public void addEndpoint(String endpoint) {
        this.count++;
        if (this.keyEndpoints.size() < 5) { // Keep top 5 endpoints
            this.keyEndpoints.add(endpoint);
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getKeyEndpoints() {
        return keyEndpoints;
    }

    public void setKeyEndpoints(List<String> keyEndpoints) {
        this.keyEndpoints = keyEndpoints;
    }

    public String getKeyEndpointsAsString() {
        return String.join(", ", keyEndpoints);
    }

    @Override
    public String toString() {
        return String.format("%-30s | %-8d | %s", category, count, getKeyEndpointsAsString());
    }
}
