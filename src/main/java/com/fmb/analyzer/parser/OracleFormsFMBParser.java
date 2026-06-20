package com.fmb.analyzer.parser;

import com.fmb.analyzer.model.FMBEndpoint;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Specialized Parser for Oracle Forms FMB XML Format
 * Extracts Blocks, Items, Triggers, and Database Queries
 */
public class OracleFormsFMBParser {
    private static final Logger logger = Logger.getLogger(OracleFormsFMBParser.class.getName());

    private DocumentBuilder documentBuilder;

    public OracleFormsFMBParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        this.documentBuilder = factory.newDocumentBuilder();
    }

    /**
     * Parse Oracle Forms FMB XML file
     */
    public List<FMBEndpoint> parseFormModule(String xmlFilePath) throws IOException, SAXException {
        List<FMBEndpoint> endpoints = new ArrayList<>();
        
        Document doc = documentBuilder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();

        // Extract module name
        String moduleName = extractModuleName(doc);
        logger.info("Parsing Oracle Forms Module: " + moduleName);

        // Extract Blocks
        NodeList blockNodes = doc.getElementsByTagName("Block");
        for (int i = 0; i < blockNodes.getLength(); i++) {
            Element blockElement = (Element) blockNodes.item(i);
            List<FMBEndpoint> blockEndpoints = parseBlock(blockElement, moduleName);
            endpoints.addAll(blockEndpoints);
        }

        logger.info("Total endpoints extracted: " + endpoints.size());
        return endpoints;
    }

    /**
     * Extract module name
     */
    private String extractModuleName(Document doc) {
        NodeList formModules = doc.getElementsByTagName("FormModule");
        if (formModules.getLength() > 0) {
            Element formModule = (Element) formModules.item(0);
            return formModule.getAttribute("Name");
        }
        return "Unknown Module";
    }

    /**
     * Parse a Block element
     */
    private List<FMBEndpoint> parseBlock(Element blockElement, String moduleName) {
        List<FMBEndpoint> blockEndpoints = new ArrayList<>();
        
        String blockName = blockElement.getAttribute("Name");
        String whereClause = blockElement.getAttribute("WhereClause");
        String queryDataSource = blockElement.getAttribute("QueryDataSourceName");
        String orderByClause = blockElement.getAttribute("OrderByClause");

        logger.info("Parsing Block: " + blockName);

        // Create Block-level endpoint
        FMBEndpoint blockEndpoint = new FMBEndpoint();
        blockEndpoint.setName(blockName);
        blockEndpoint.setEndpoint("BLOCK://" + moduleName + "/" + blockName);
        blockEndpoint.setCategory("CRUD (Direct)");
        blockEndpoint.setMethod("QUERY");
        blockEndpoint.setDescription("Oracle Forms Block: " + blockName);
        
        if (!queryDataSource.isEmpty()) {
            blockEndpoint.setBusinessLogic("Data Source: " + queryDataSource);
        }
        
        if (!whereClause.isEmpty()) {
            blockEndpoint.setBusinessLogic((blockEndpoint.getBusinessLogic() != null ? blockEndpoint.getBusinessLogic() + "\n" : "") 
                    + "Where Clause: " + whereClause.substring(0, Math.min(100, whereClause.length())) + "...");
        }

        blockEndpoints.add(blockEndpoint);

        // Extract Items from Block
        NodeList itemNodes = blockElement.getElementsByTagName("Item");
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element itemElement = (Element) itemNodes.item(i);
            FMBEndpoint itemEndpoint = parseItem(itemElement, blockName, moduleName);
            blockEndpoints.add(itemEndpoint);
        }

        // Extract Triggers from Block
        NodeList triggerNodes = blockElement.getElementsByTagName("Trigger");
        for (int i = 0; i < triggerNodes.getLength(); i++) {
            Element triggerElement = (Element) triggerNodes.item(i);
            FMBEndpoint triggerEndpoint = parseTrigger(triggerElement, blockName, moduleName);
            blockEndpoints.add(triggerEndpoint);
        }

        return blockEndpoints;
    }

    /**
     * Parse an Item element
     */
    private FMBEndpoint parseItem(Element itemElement, String blockName, String moduleName) {
        FMBEndpoint endpoint = new FMBEndpoint();

        String itemName = itemElement.getAttribute("Name");
        String itemType = itemElement.getAttribute("ItemType");
        String dataType = itemElement.getAttribute("DataType");
        String columnName = itemElement.getAttribute("ColumnName");
        String prompt = itemElement.getAttribute("Prompt");
        boolean isDbItem = itemElement.getAttribute("DatabaseItem").equals("true");

        endpoint.setName(itemName);
        endpoint.setEndpoint("ITEM://" + moduleName + "/" + blockName + "/" + itemName);
        
        // Categorize by item type
        if ("Text Item".equals(itemType)) {
            endpoint.setCategory("CRUD (Direct)");
        } else if ("List Item".equals(itemType)) {
            endpoint.setCategory("LOVs");
        } else if ("Display Item".equals(itemType)) {
            endpoint.setCategory("Validations");
        } else if ("Push Button".equals(itemType)) {
            endpoint.setCategory("Actions");
        } else {
            endpoint.setCategory("Actions");
        }

        endpoint.setMethod(itemType);
        endpoint.setDescription(prompt.isEmpty() ? ("Item: " + itemName + " (" + itemType + ")") : prompt);
        
        StringBuilder logic = new StringBuilder();
        if (isDbItem && !columnName.isEmpty()) {
            logic.append("Database Column: ").append(columnName).append("\n");
        }
        if (!dataType.isEmpty()) {
            logic.append("Data Type: ").append(dataType).append("\n");
        }
        
        // Extract triggers within item
        NodeList itemTriggers = itemElement.getElementsByTagName("Trigger");
        if (itemTriggers.getLength() > 0) {
            logic.append("Triggers: ");
            for (int i = 0; i < itemTriggers.getLength(); i++) {
                Element triggerElem = (Element) itemTriggers.item(i);
                String triggerName = triggerElem.getAttribute("Name");
                logic.append(triggerName).append("; ");
            }
        }

        if (logic.length() > 0) {
            endpoint.setBusinessLogic(logic.toString());
        }

        endpoint.setDirect(isDbItem);
        endpoint.setIndirect(!isDbItem);

        return endpoint;
    }

    /**
     * Parse a Trigger element
     */
    private FMBEndpoint parseTrigger(Element triggerElement, String blockName, String moduleName) {
        FMBEndpoint endpoint = new FMBEndpoint();

        String triggerName = triggerElement.getAttribute("Name");
        String triggerText = triggerElement.getAttribute("TriggerText");

        endpoint.setName(triggerName + "_" + blockName);
        endpoint.setEndpoint("TRIGGER://" + moduleName + "/" + blockName + "/" + triggerName);

        // Categorize trigger by type
        if (triggerName.contains("POST-CHANGE") || triggerName.contains("KEY-NEXT")) {
            endpoint.setCategory("POST-CHANGE");
        } else if (triggerName.contains("VALIDATE")) {
            endpoint.setCategory("Validations");
        } else if (triggerName.contains("WHEN-BUTTON")) {
            endpoint.setCategory("Actions");
        } else if (triggerName.contains("COMMIT")) {
            endpoint.setCategory("Complex (Commit logic)");
        } else if (triggerName.contains("WHEN-LIST")) {
            endpoint.setCategory("LOVs");
        } else {
            endpoint.setCategory("Actions");
        }

        endpoint.setMethod("TRIGGER");
        endpoint.setDescription("Trigger: " + triggerName + " on Block: " + blockName);

        // Extract SQL queries from trigger text
        List<String> queries = extractSQLQueries(triggerText);
        if (!queries.isEmpty()) {
            endpoint.setBusinessLogic("SQL Queries:\n" + String.join("\n---\n", queries));
            if (triggerText.toLowerCase().contains("commit")) {
                endpoint.setCommitLogic(triggerText);
            }
        } else {
            endpoint.setBusinessLogic(triggerText.length() > 200 ? 
                triggerText.substring(0, 200) + "..." : triggerText);
        }

        return endpoint;
    }

    /**
     * Extract SQL queries from trigger text
     */
    private List<String> extractSQLQueries(String triggerText) {
        List<String> queries = new ArrayList<>();
        
        // Pattern for SELECT statements
        Pattern selectPattern = Pattern.compile(
            "SELECT\\s+.*?(?=FROM|from)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = selectPattern.matcher(triggerText);
        while (matcher.find()) {
            String query = matcher.group().trim();
            if (query.length() < 500) {
                queries.add(query);
            }
        }

        return queries;
    }

    /**
     * Get form structure information
     */
    public Map<String, Object> getFormStructure(String xmlFilePath) throws IOException, SAXException {
        Map<String, Object> structure = new HashMap<>();
        
        Document doc = documentBuilder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();

        String moduleName = extractModuleName(doc);
        structure.put("moduleName", moduleName);

        // Count blocks
        NodeList blockNodes = doc.getElementsByTagName("Block");
        structure.put("blockCount", blockNodes.getLength());

        // Count items
        NodeList itemNodes = doc.getElementsByTagName("Item");
        structure.put("itemCount", itemNodes.getLength());

        // Count triggers
        NodeList triggerNodes = doc.getElementsByTagName("Trigger");
        structure.put("triggerCount", triggerNodes.getLength());

        // List all block names
        List<String> blockNames = new ArrayList<>();
        for (int i = 0; i < blockNodes.getLength(); i++) {
            Element block = (Element) blockNodes.item(i);
            blockNames.add(block.getAttribute("Name"));
        }
        structure.put("blocks", blockNames);

        return structure;
    }
}
