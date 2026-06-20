package com.fmb.analyzer.report;

import com.fmb.analyzer.analyzer.FMBAnalyzer;
import com.fmb.analyzer.model.FMBEndpoint;
import com.fmb.analyzer.model.SummaryRecord;

import java.util.List;
import java.util.logging.Logger;

/**
 * Generates summary reports for FMB analysis
 */
public class SummaryReportGenerator {
    private static final Logger logger = Logger.getLogger(SummaryReportGenerator.class.getName());
    
    private FMBAnalyzer analyzer;

    public SummaryReportGenerator(FMBAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Generate and print summary table
     */
    public void generateSummaryTable() {
        List<SummaryRecord> summary = analyzer.generateSummary();

        System.out.println("\n" + "=".repeat(100));
        System.out.println("FMB ENDPOINT ANALYSIS - SUMMARY TABLE");
        System.out.println("=".repeat(100));
        System.out.println(String.format("%-30s | %-8s | %s", "Category", "Count", "Key Endpoints"));
        System.out.println("-".repeat(100));

        for (SummaryRecord record : summary) {
            System.out.println(record.toString());
        }

        System.out.println("-".repeat(100));
        System.out.println(String.format("TOTAL ENDPOINTS: %d", analyzer.getTotalEndpoints()));
        System.out.println("=".repeat(100) + "\n");
    }

    /**
     * Generate detailed endpoint report
     */
    public void generateDetailedReport() {
        List<FMBEndpoint> endpoints = analyzer.getAllEndpoints();

        System.out.println("\n" + "=".repeat(120));
        System.out.println("FMB ENDPOINT DETAILED REPORT");
        System.out.println("=".repeat(120));
        System.out.println(String.format("%-35s | %-15s | %-20s | %-20s | %-15s",
                "Endpoint Name", "Method", "Category", "Block Type", "Has Logic"));
        System.out.println("-".repeat(120));

        for (FMBEndpoint endpoint : endpoints) {
            String blockType = endpoint.isDirect() ? "DIRECT" : 
                             (endpoint.isIndirect() ? "INDIRECT" : "NONE");
            boolean hasLogic = (endpoint.getCommitLogic() != null && !endpoint.getCommitLogic().isEmpty()) ||
                             (endpoint.getHistoryTracking() != null && !endpoint.getHistoryTracking().isEmpty());

            System.out.println(String.format("%-35s | %-15s | %-20s | %-20s | %-15s",
                    truncate(endpoint.getName(), 33),
                    endpoint.getMethod() != null ? endpoint.getMethod() : "N/A",
                    endpoint.getCategory() != null ? endpoint.getCategory() : "N/A",
                    blockType,
                    hasLogic ? "YES" : "NO"));
        }

        System.out.println("-".repeat(120));
        System.out.println("=".repeat(120) + "\n");
    }

    /**
     * Generate category breakdown report
     */
    public void generateCategoryBreakdown() {
        List<SummaryRecord> summary = analyzer.generateSummary();

        System.out.println("\n" + "=".repeat(100));
        System.out.println("CATEGORY BREAKDOWN REPORT");
        System.out.println("=".repeat(100));

        int totalEndpoints = analyzer.getTotalEndpoints();

        for (SummaryRecord record : summary) {
            double percentage = (record.getCount() * 100.0) / totalEndpoints;
            String bar = generateProgressBar(percentage, 40);
            
            System.out.println(String.format("%s: %3d endpoints (%.1f%%) %s",
                    padRight(record.getCategory(), 30),
                    record.getCount(),
                    percentage,
                    bar));
        }

        System.out.println("-".repeat(100));
        System.out.println("=".repeat(100) + "\n");
    }

    /**
     * Generate block type analysis report
     */
    public void generateBlockTypeAnalysis() {
        List<FMBEndpoint> directEndpoints = analyzer.getDirectBlockEndpoints();
        List<FMBEndpoint> indirectEndpoints = analyzer.getIndirectBlockEndpoints();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("BLOCK TYPE ANALYSIS");
        System.out.println("=".repeat(80));
        System.out.println(String.format("Direct Blocks:       %3d endpoints", directEndpoints.size()));
        System.out.println(String.format("Indirect Blocks:     %3d endpoints", indirectEndpoints.size()));
        System.out.println("-".repeat(80));

        if (!directEndpoints.isEmpty()) {
            System.out.println("\nDirect Block Endpoints:");
            for (FMBEndpoint endpoint : directEndpoints) {
                System.out.println(String.format("  - %s [%s]", endpoint.getName(), endpoint.getEndpoint()));
            }
        }

        if (!indirectEndpoints.isEmpty()) {
            System.out.println("\nIndirect Block Endpoints:");
            for (FMBEndpoint endpoint : indirectEndpoints) {
                System.out.println(String.format("  - %s [%s]", endpoint.getName(), endpoint.getEndpoint()));
            }
        }

        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Generate complex logic report
     */
    public void generateComplexLogicReport() {
        List<FMBEndpoint> commitLogicEndpoints = analyzer.getEndpointsWithCommitLogic();
        List<FMBEndpoint> historyEndpoints = analyzer.getEndpointsWithHistory();

        System.out.println("\n" + "=".repeat(100));
        System.out.println("COMPLEX LOGIC ANALYSIS");
        System.out.println("=".repeat(100));

        System.out.println(String.format("Endpoints with Commit Logic:    %3d", commitLogicEndpoints.size()));
        System.out.println(String.format("Endpoints with History Tracking: %3d", historyEndpoints.size()));
        System.out.println("-".repeat(100));

        if (!commitLogicEndpoints.isEmpty()) {
            System.out.println("\nCommit Logic Endpoints:");
            for (FMBEndpoint endpoint : commitLogicEndpoints) {
                System.out.println(String.format("  - %s: %s",
                        padRight(endpoint.getName(), 35),
                        truncate(endpoint.getCommitLogic(), 50)));
            }
        }

        if (!historyEndpoints.isEmpty()) {
            System.out.println("\nHistory Tracking Endpoints:");
            for (FMBEndpoint endpoint : historyEndpoints) {
                System.out.println(String.format("  - %s: %s",
                        padRight(endpoint.getName(), 35),
                        truncate(endpoint.getHistoryTracking(), 50)));
            }
        }

        System.out.println("=".repeat(100) + "\n");
    }

    /**
     * Generate HTTP method distribution report
     */
    public void generateMethodDistributionReport() {
        List<FMBEndpoint> endpoints = analyzer.getAllEndpoints();

        java.util.Map<String, Integer> methodCount = new java.util.HashMap<>();
        for (FMBEndpoint endpoint : endpoints) {
            String method = endpoint.getMethod() != null ? endpoint.getMethod() : "UNKNOWN";
            methodCount.put(method, methodCount.getOrDefault(method, 0) + 1);
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("HTTP METHOD DISTRIBUTION");
        System.out.println("=".repeat(60));

        methodCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry -> {
                    double percentage = (entry.getValue() * 100.0) / endpoints.size();
                    String bar = generateProgressBar(percentage, 30);
                    System.out.println(String.format("%-10s: %3d (%.1f%%) %s",
                            entry.getKey(), entry.getValue(), percentage, bar));
                });

        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Generate comprehensive report
     */
    public void generateComprehensiveReport() {
        generateSummaryTable();
        generateCategoryBreakdown();
        generateMethodDistributionReport();
        generateBlockTypeAnalysis();
        generateComplexLogicReport();
        generateDetailedReport();
    }

    /**
     * Utility method to generate progress bar
     */
    private String generateProgressBar(double percentage, int width) {
        int filled = (int) ((percentage / 100.0) * width);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < width; i++) {
            bar.append(i < filled ? "=" : " ");
        }
        bar.append("]");
        return bar.toString();
    }

    /**
     * Truncate string to specified length
     */
    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() > length) {
            return str.substring(0, length - 3) + "...";
        }
        return str;
    }

    /**
     * Pad string to the right
     */
    private String padRight(String str, int length) {
        return String.format("%-" + length + "s", str != null ? str : "");
    }
}
