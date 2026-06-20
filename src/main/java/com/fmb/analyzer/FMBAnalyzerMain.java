package com.fmb.analyzer;

import com.fmb.analyzer.analyzer.FMBAnalyzer;
import com.fmb.analyzer.model.FMBEndpoint;
import com.fmb.analyzer.parser.FMBXMLParser;
import com.fmb.analyzer.report.SummaryReportGenerator;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Main entry point for FMB XML Analyzer
 */
public class FMBAnalyzerMain {
    private static final Logger logger = Logger.getLogger(FMBAnalyzerMain.class.getName());

    public static void main(String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║         FMB XML ANALYZER - ENDPOINT ANALYSIS TOOL           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");

            FMBXMLParser parser = new FMBXMLParser();
            List<FMBEndpoint> endpoints;

            // Parse XML file
            String xmlFilePath = getXMLFilePath();
            
            if (xmlFilePath != null && !xmlFilePath.isEmpty()) {
                endpoints = parser.parse(xmlFilePath);
            } else {
                // Use sample data for demonstration
                System.out.println("No file provided. Using sample data for demonstration...\n");
                endpoints = generateSampleEndpoints();
            }

            // Analyze endpoints
            FMBAnalyzer analyzer = new FMBAnalyzer(endpoints);

            // Generate report
            SummaryReportGenerator reportGenerator = new SummaryReportGenerator(analyzer);

            System.out.println("\nSelect report type:");
            System.out.println("1. Summary Table");
            System.out.println("2. Category Breakdown");
            System.out.println("3. Block Type Analysis");
            System.out.println("4. Complex Logic Analysis");
            System.out.println("5. Detailed Endpoint Report");
            System.out.println("6. HTTP Method Distribution");
            System.out.println("7. Comprehensive Report (All)");
            System.out.println("8. Exit");

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.print("\nEnter your choice (1-8): ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        reportGenerator.generateSummaryTable();
                        break;
                    case "2":
                        reportGenerator.generateCategoryBreakdown();
                        break;
                    case "3":
                        reportGenerator.generateBlockTypeAnalysis();
                        break;
                    case "4":
                        reportGenerator.generateComplexLogicReport();
                        break;
                    case "5":
                        reportGenerator.generateDetailedReport();
                        break;
                    case "6":
                        reportGenerator.generateMethodDistributionReport();
                        break;
                    case "7":
                        reportGenerator.generateComprehensiveReport();
                        break;
                    case "8":
                        running = false;
                        System.out.println("\nThank you for using FMB XML Analyzer!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            scanner.close();

        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getXMLFilePath() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter FMB XML file path (or press Enter to skip): ");
        return scanner.nextLine().trim();
    }

    /**
     * Generate sample FMB endpoints for demonstration
     */
    private static List<FMBEndpoint> generateSampleEndpoints() {
        List<FMBEndpoint> endpoints = new java.util.ArrayList<>();

        // LOVs Endpoints
        FMBEndpoint lov1 = new FMBEndpoint("GET_DEPARTMENT_LOV", "/api/lov/departments", "LOVs", "GET");
        lov1.setDescription("Retrieve list of departments");
        endpoints.add(lov1);

        FMBEndpoint lov2 = new FMBEndpoint("GET_STATUS_LOV", "/api/lov/status", "LOVs", "GET");
        lov2.setDescription("Retrieve list of status values");
        endpoints.add(lov2);

        // POST-CHANGE Endpoints
        FMBEndpoint postChange = new FMBEndpoint("POST_CHANGE_VALIDATION", "/api/post-change/validate", "POST-CHANGE", "POST");
        postChange.setDescription("Post-change validation");
        endpoints.add(postChange);

        // Validations Endpoints
        FMBEndpoint validation1 = new FMBEndpoint("VALIDATE_EMPLOYEE_ID", "/api/validate/employee", "Validations", "POST");
        validation1.setDescription("Validate employee ID format");
        endpoints.add(validation1);

        // CRUD Direct Endpoints
        FMBEndpoint crudDirect1 = new FMBEndpoint("CREATE_EMPLOYEE", "/api/employees", "CRUD (Direct)", "POST");
        crudDirect1.setDirect(true);
        crudDirect1.setDescription("Create new employee record");
        endpoints.add(crudDirect1);

        FMBEndpoint crudDirect2 = new FMBEndpoint("UPDATE_EMPLOYEE", "/api/employees/{id}", "CRUD (Direct)", "PUT");
        crudDirect2.setDirect(true);
        crudDirect2.setDescription("Update employee record");
        endpoints.add(crudDirect2);

        FMBEndpoint crudDirect3 = new FMBEndpoint("READ_EMPLOYEE", "/api/employees/{id}", "CRUD (Direct)", "GET");
        crudDirect3.setDirect(true);
        crudDirect3.setDescription("Read employee record");
        endpoints.add(crudDirect3);

        // CRUD Indirect Endpoints
        FMBEndpoint crudIndirect1 = new FMBEndpoint("DELETE_EMPLOYEE_INDIRECT", "/api/employees/{id}/indirect", "CRUD (Indirect)", "DELETE");
        crudIndirect1.setIndirect(true);
        crudIndirect1.setDescription("Delete employee with validation");
        endpoints.add(crudIndirect1);

        // Actions Endpoints
        FMBEndpoint action1 = new FMBEndpoint("APPROVE_REQUISITION", "/api/actions/approve", "Actions", "POST");
        action1.setDescription("Approve a requisition");
        endpoints.add(action1);

        FMBEndpoint action2 = new FMBEndpoint("REJECT_REQUEST", "/api/actions/reject", "Actions", "POST");
        action2.setDescription("Reject a request");
        endpoints.add(action2);

        // Complex - Key-next calculations
        FMBEndpoint complex1 = new FMBEndpoint("CALCULATE_NEXT_SEQ", "/api/complex/calc-seq", "Complex (Key-next calculations)", "POST");
        complex1.setBusinessLogic("SELECT MAX(id) + 1 FROM sequence_table");
        complex1.setDescription("Calculate next sequence number");
        endpoints.add(complex1);

        // Complex - Commit logic
        FMBEndpoint complex2 = new FMBEndpoint("COMMIT_TRANSACTION", "/api/complex/commit", "Complex (Commit logic)", "POST");
        complex2.setCommitLogic("BEGIN TRANSACTION; UPDATE status = 'COMMITTED'; COMMIT;");
        complex2.setDescription("Commit transaction with validation");
        endpoints.add(complex2);

        // Complex - History
        FMBEndpoint complex3 = new FMBEndpoint("GET_HISTORY", "/api/complex/history/{id}", "Complex (History)", "GET");
        complex3.setHistoryTracking("SELECT * FROM audit_log WHERE entity_id = ?");
        complex3.setDescription("Retrieve audit history");
        endpoints.add(complex3);

        return endpoints;
    }
}
