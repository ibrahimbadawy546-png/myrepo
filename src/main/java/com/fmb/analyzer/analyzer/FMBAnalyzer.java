package com.fmb.analyzer.analyzer;

import com.fmb.analyzer.model.EndpointCategory;
import com.fmb.analyzer.model.FMBEndpoint;
import com.fmb.analyzer.model.SummaryRecord;

import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * FMB Endpoint Analyzer
 * Analyzes and categorizes FMB endpoints
 */
public class FMBAnalyzer {
    private static final Logger logger = Logger.getLogger(FMBAnalyzer.class.getName());
    
    private List<FMBEndpoint> endpoints;
    private Map<String, List<FMBEndpoint>> categorizedEndpoints;

    public FMBAnalyzer(List<FMBEndpoint> endpoints) {
        this.endpoints = endpoints != null ? endpoints : new ArrayList<>();
        this.categorizedEndpoints = new HashMap<>();
        analyze();
    }

    /**
     * Analyze endpoints and categorize them
     */
    private void analyze() {
        // Initialize categories
        for (EndpointCategory category : EndpointCategory.getAllCategories()) {
            categorizedEndpoints.put(category.getDisplayName(), new ArrayList<>());
        }

        // Categorize endpoints
        for (FMBEndpoint endpoint : endpoints) {
            categorizeEndpoint(endpoint);
        }

        logger.info("Analysis complete. Total endpoints: " + endpoints.size());
    }

    /**
     * Categorize individual endpoint
     */
    private void categorizeEndpoint(FMBEndpoint endpoint) {
        String category = endpoint.getCategory();
        
        if (category == null || category.isEmpty()) {
            // Auto-detect category based on endpoint characteristics
            category = autoDetectCategory(endpoint);
        }

        String normalizedCategory = normalizeCategory(category);
        
        categorizedEndpoints.computeIfAbsent(normalizedCategory, k -> new ArrayList<>())
                .add(endpoint);
    }

    /**
     * Auto-detect category based on endpoint characteristics
     */
    private String autoDetectCategory(FMBEndpoint endpoint) {
        String name = endpoint.getName().toUpperCase();
        String endpoint_path = endpoint.getEndpoint().toUpperCase();

        // LOVs detection
        if (name.contains("LOV") || name.contains("LIST_OF_VALUES") || 
            endpoint_path.contains("lov") || endpoint_path.contains("lookup")) {
            return EndpointCategory.LOVs.getDisplayName();
        }

        // POST-CHANGE detection
        if (name.contains("POST_CHANGE") || name.contains("POSTCHANGE") ||
            endpoint_path.contains("post-change") || endpoint_path.contains("postchange")) {
            return EndpointCategory.POST_CHANGE.getDisplayName();
        }

        // Validation detection
        if (name.contains("VALIDAT") || endpoint_path.contains("validat")) {
            return EndpointCategory.VALIDATIONS.getDisplayName();
        }

        // CRUD detection
        if (name.contains("CREATE") || name.contains("READ") || 
            name.contains("UPDATE") || name.contains("DELETE")) {
            if (endpoint.isDirect()) {
                return EndpointCategory.CRUD_DIRECT.getDisplayName();
            } else if (endpoint.isIndirect()) {
                return EndpointCategory.CRUD_INDIRECT.getDisplayName();
            }
            return EndpointCategory.CRUD_DIRECT.getDisplayName();
        }

        // Action detection
        if (name.contains("ACTION") || endpoint_path.contains("action")) {
            return EndpointCategory.ACTIONS.getDisplayName();
        }

        // Complex logic detection
        if (endpoint.getCommitLogic() != null && !endpoint.getCommitLogic().isEmpty()) {
            return EndpointCategory.COMMIT_LOGIC.getDisplayName();
        }

        if (endpoint.getHistoryTracking() != null && !endpoint.getHistoryTracking().isEmpty()) {
            return EndpointCategory.HISTORY_TRACKING.getDisplayName();
        }

        if (endpoint.getBusinessLogic() != null && !endpoint.getBusinessLogic().isEmpty()) {
            if (name.contains("CALC") || name.contains("NEXT")) {
                return EndpointCategory.COMPLEX_CALCULATIONS.getDisplayName();
            }
        }

        // Default to Actions
        return EndpointCategory.ACTIONS.getDisplayName();
    }

    /**
     * Normalize category string
     */
    private String normalizeCategory(String category) {
        if (category == null) return EndpointCategory.ACTIONS.getDisplayName();
        
        String normalized = category.toUpperCase().trim();
        
        for (EndpointCategory cat : EndpointCategory.getAllCategories()) {
            if (cat.getDisplayName().toUpperCase().equals(normalized) ||
                cat.name().equals(normalized)) {
                return cat.getDisplayName();
            }
        }
        
        return category;
    }

    /**
     * Generate summary report
     */
    public List<SummaryRecord> generateSummary() {
        List<SummaryRecord> summary = new ArrayList<>();

        for (String category : categorizedEndpoints.keySet()) {
            List<FMBEndpoint> categoryEndpoints = categorizedEndpoints.get(category);
            
            SummaryRecord record = new SummaryRecord(category);
            for (FMBEndpoint endpoint : categoryEndpoints) {
                record.addEndpoint(endpoint.getEndpoint());
            }
            record.setCount(categoryEndpoints.size());
            
            summary.add(record);
        }

        // Sort by count descending
        summary.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));
        
        return summary;
    }

    /**
     * Get endpoints by category
     */
    public List<FMBEndpoint> getEndpointsByCategory(String category) {
        String normalizedCategory = normalizeCategory(category);
        return categorizedEndpoints.getOrDefault(normalizedCategory, new ArrayList<>());
    }

    /**
     * Get endpoints by method
     */
    public List<FMBEndpoint> getEndpointsByMethod(String method) {
        return endpoints.stream()
                .filter(e -> e.getMethod() != null && e.getMethod().equalsIgnoreCase(method))
                .collect(Collectors.toList());
    }

    /**
     * Get endpoints with direct blocks
     */
    public List<FMBEndpoint> getDirectBlockEndpoints() {
        return endpoints.stream()
                .filter(FMBEndpoint::isDirect)
                .collect(Collectors.toList());
    }

    /**
     * Get endpoints with indirect blocks
     */
    public List<FMBEndpoint> getIndirectBlockEndpoints() {
        return endpoints.stream()
                .filter(FMBEndpoint::isIndirect)
                .collect(Collectors.toList());
    }

    /**
     * Get endpoints with commit logic
     */
    public List<FMBEndpoint> getEndpointsWithCommitLogic() {
        return endpoints.stream()
                .filter(e -> e.getCommitLogic() != null && !e.getCommitLogic().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Get endpoints with history tracking
     */
    public List<FMBEndpoint> getEndpointsWithHistory() {
        return endpoints.stream()
                .filter(e -> e.getHistoryTracking() != null && !e.getHistoryTracking().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Get all endpoints
     */
    public List<FMBEndpoint> getAllEndpoints() {
        return new ArrayList<>(endpoints);
    }

    /**
     * Get total endpoint count
     */
    public int getTotalEndpoints() {
        return endpoints.size();
    }
}
