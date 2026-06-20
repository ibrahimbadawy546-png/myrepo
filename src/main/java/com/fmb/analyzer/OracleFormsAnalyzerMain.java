package com.fmb.analyzer;

import com.fmb.analyzer.analyzer.FMBAnalyzer;
import com.fmb.analyzer.model.FMBEndpoint;
import com.fmb.analyzer.parser.OracleFormsFMBParser;
import com.fmb.analyzer.report.SummaryReportGenerator;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Main entry point for Oracle Forms FMB Analyzer
 * Supports both generic XML and Oracle Forms FMB format
 */
public class OracleFormsAnalyzerMain {
    private static final Logger logger = Logger.getLogger(OracleFormsAnalyzerMain.class.getName());

    public static void main(String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║    ORACLE FORMS FMB ANALYZER - ENDPOINT ANALYSIS TOOL        ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");

            OracleFormsFMBParser parser = new OracleFormsFMBParser();
            List<FMBEndpoint> endpoints;

            // Get XML file path
            String xmlFilePath = getXMLFilePath();
            
            if (xmlFilePath != null && !xmlFilePath.isEmpty()) {
                // Show form structure first
                System.out.println("\n📋 Analyzing Form Structure...\n");
                Map<String, Object> structure = parser.getFormStructure(xmlFilePath);
                
                System.out.println("Module Name: " + structure.get("moduleName"));
                System.out.println("Total Blocks: " + structure.get("blockCount"));
                System.out.println("Total Items: " + structure.get("itemCount"));
                System.out.println("Total Triggers: " + structure.get("triggerCount"));
                System.out.println("Blocks: " + structure.get("blocks"));
                System.out.println();

                // Parse endpoints
                System.out.println("🔍 Parsing Endpoints...\n");
                endpoints = parser.parseFormModule(xmlFilePath);
                
                System.out.println("✅ Extracted " + endpoints.size() + " endpoints\n");
            } else {
                System.out.println("No file provided. Using sample data for demonstration...\n");
                endpoints = generateSampleEndpoints();
            }

            // Analyze endpoints
            FMBAnalyzer analyzer = new FMBAnalyzer(endpoints);

            // Show interactive menu
            showInteractiveMenu(analyzer);

        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show interactive menu and handle user selections
     */
    private static void showInteractiveMenu(FMBAnalyzer analyzer) {
        SummaryReportGenerator reportGenerator = new SummaryReportGenerator(analyzer);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              SELECT REPORT TYPE                             ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            System.out.println("1. Summary Table");
            System.out.println("2. Category Breakdown");
            System.out.println("3. Block Type Analysis");
            System.out.println("4. Complex Logic Analysis");
            System.out.println("5. Detailed Endpoint Report");
            System.out.println("6. HTTP Method Distribution");
            System.out.println("7. Comprehensive Report (All)");
            System.out.println("8. Exit");

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
                    System.out.println("\n👋 Thank you for using Oracle Forms FMB Analyzer!");
                    break;
                default:
                    System.out.println("\n❌ Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    /**
     * Get XML file path from user
     */
    private static String getXMLFilePath() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("📁 Enter FMB XML file path (or press Enter to skip): ");
        String filePath = scanner.nextLine().trim();
        
        if (!filePath.isEmpty()) {
            java.io.File file = new java.io.File(filePath);
            if (file.exists()) {
                return filePath;
            } else {
                System.out.println("❌ File not found: " + filePath);
                return null;
            }
        }
        return null;
    }

    /**
     * Generate sample FMB endpoints for demonstration
     */
    private static List<FMBEndpoint> generateSampleEndpoints() {
        List<FMBEndpoint> endpoints = new java.util.ArrayList<>();

        // Sample Block Endpoint
        FMBEndpoint blockEndpoint = new FMBEndpoint("EMPLOYEES_BLOCK", "BLOCK://EMPLOYEE_FORM/EMPLOYEES_BLOCK", "CRUD (Direct)", "QUERY");
        blockEndpoint.setDescription("Main employee data block");
        endpoints.add(blockEndpoint);

        // Sample Item Endpoints
        FMBEndpoint itemEmpId = new FMBEndpoint("EMP_ID", "ITEM://EMPLOYEE_FORM/EMPLOYEES_BLOCK/EMP_ID", "CRUD (Direct)", "Text Item");
        itemEmpId.setDescription("Employee ID number");
        endpoints.add(itemEmpId);

        FMBEndpoint itemEmpName = new FMBEndpoint("EMP_NAME", "ITEM://EMPLOYEE_FORM/EMPLOYEES_BLOCK/EMP_NAME", "CRUD (Direct)", "Text Item");
        itemEmpName.setDescription("Employee Name");
        endpoints.add(itemEmpName);

        FMBEndpoint itemStatus = new FMBEndpoint("STATUS", "ITEM://EMPLOYEE_FORM/EMPLOYEES_BLOCK/STATUS", "LOVs", "List Item");
        itemStatus.setDescription("Employee Status List");
        endpoints.add(itemStatus);

        // Sample Trigger Endpoints
        FMBEndpoint triggerPostChange = new FMBEndpoint("POST_CHANGE_EMP_ID", "TRIGGER://EMPLOYEE_FORM/EMPLOYEES_BLOCK/POST-CHANGE", "POST-CHANGE", "TRIGGER");
        triggerPostChange.setDescription("Execute query after employee ID change");
        triggerPostChange.setBusinessLogic("EXECUTE_QUERY;");
        endpoints.add(triggerPostChange);

        FMBEndpoint triggerValidate = new FMBEndpoint("WHEN_VALIDATE_EMP_NAME", "TRIGGER://EMPLOYEE_FORM/EMPLOYEES_BLOCK/WHEN-VALIDATE", "Validations", "TRIGGER");
        triggerValidate.setDescription("Validate employee name format");
        endpoints.add(triggerValidate);

        FMBEndpoint triggerCommit = new FMBEndpoint("COMMIT_TRANSACTION", "TRIGGER://EMPLOYEE_FORM/EMPLOYEES_BLOCK/COMMIT", "Complex (Commit logic)", "TRIGGER");
        triggerCommit.setDescription("Commit employee record changes");
        triggerCommit.setCommitLogic("COMMIT_FORM;");
        endpoints.add(triggerCommit);

        FMBEndpoint triggerAction = new FMBEndpoint("SAVE_BUTTON", "TRIGGER://EMPLOYEE_FORM/EMPLOYEES_BLOCK/WHEN-BUTTON", "Actions", "TRIGGER");
        triggerAction.setDescription("Save employee record");
        endpoints.add(triggerAction);

        return endpoints;
    }
}
