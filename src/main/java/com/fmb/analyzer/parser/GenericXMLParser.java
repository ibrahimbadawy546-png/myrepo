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

/**
 * Generic XML Parser for any form.xml format
 * Automatically detects XML structure and extracts data
 */
public class GenericXMLParser {
    private static final Logger logger = Logger.getLogger(GenericXMLParser.class.getName());

    private DocumentBuilder documentBuilder;
    private Map<String, String> fieldMappings; // Maps expected fields to actual XML element names

    public GenericXMLParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        this.documentBuilder = factory.newDocumentBuilder();
        this.fieldMappings = new HashMap<>();
    }

    /**
     * Parse any XML file with auto-detection
     */
    public List<FMBEndpoint> parseGeneric(String xmlFilePath) throws IOException, SAXException {
        List<FMBEndpoint> endpoints = new ArrayList<>();
        
        Document doc = documentBuilder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();

        // Try to find the root container element (endpoints, records, items, etc.)
        NodeList allElements = doc.getDocumentElement().getChildNodes();
        
        // Detect the actual element name for records
        String elementName = detectElementName(allElements);
        logger.info("Detected element name: " + elementName);

        // Extract all record elements
        NodeList recordNodes = doc.getElementsByTagName(elementName);
        
        for (int i = 0; i < recordNodes.getLength(); i++) {
            Element element = (Element) recordNodes.item(i);
            FMBEndpoint endpoint = parseGenericElement(element);
            if (endpoint != null) {
                endpoints.add(endpoint);
            }
        }

        logger.info("Parsed " + endpoints.size() + " records from XML");
        return endpoints;
    }

    /**
     * Auto-detect the main element name for records
     */
    private String detectElementName(NodeList nodes) {
        Map<String, Integer> elementCounts = new HashMap<>();
        
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName().toLowerCase();
                if (!name.equals("endpoint") && !name.equals("record") && !name.equals("item")) {
                    elementCounts.put(name, elementCounts.getOrDefault(name, 0) + 1);
                }
            }
        }

        // Return the most frequent element (likely the record type)
        if (!elementCounts.isEmpty()) {
            return elementCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("endpoint");
        }
        
        return "endpoint";
    }

    /**
     * Parse a generic element with flexible field mapping
     */
    private FMBEndpoint parseGenericElement(Element element) {
        try {
            FMBEndpoint endpoint = new FMBEndpoint();

            // Get all child elements
            Map<String, String> fieldValues = extractAllFields(element);

            // Map fields intelligently
            endpoint.setName(getValueByAnyKey(fieldValues, "name", "id", "title", "key"));
            endpoint.setEndpoint(getValueByAnyKey(fieldValues, "path", "endpoint", "url", "uri", "resource"));
            endpoint.setMethod(getValueByAnyKey(fieldValues, "method", "verb", "http_method", "operation"));
            endpoint.setCategory(getValueByAnyKey(fieldValues, "category", "type", "group", "class"));
            endpoint.setDescription(getValueByAnyKey(fieldValues, "description", "desc", "summary", "remarks"));
            
            // Block type detection
            String blockType = getValueByAnyKey(fieldValues, "blocktype", "block_type", "block", "kind");
            endpoint.setDirect("DIRECT".equalsIgnoreCase(blockType) || blockType.isEmpty());
            endpoint.setIndirect("INDIRECT".equalsIgnoreCase(blockType));

            // Complex logic
            endpoint.setBusinessLogic(getValueByAnyKey(fieldValues, "businesslogic", "business_logic", "logic", "rules"));
            endpoint.setCommitLogic(getValueByAnyKey(fieldValues, "commitlogic", "commit_logic", "commit", "transaction_logic"));
            endpoint.setHistoryTracking(getValueByAnyKey(fieldValues, "historytracking", "history_tracking", "history", "audit"));

            return endpoint;
        } catch (Exception e) {
            logger.warning("Error parsing element: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract all fields from an element
     */
    private Map<String, String> extractAllFields(Element element) {
        Map<String, String> fields = new LinkedHashMap<>();

        // Get child element values
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String key = node.getNodeName().toLowerCase();
                String value = node.getTextContent() != null ? node.getTextContent().trim() : "";
                fields.put(key, value);
            }
        }

        // Get attributes
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            String key = attr.getName().toLowerCase();
            fields.put(key, attr.getValue());
        }

        return fields;
    }

    /**
     * Get value from map by trying multiple possible key names
     */
    private String getValueByAnyKey(Map<String, String> map, String... possibleKeys) {
        for (String key : possibleKeys) {
            String lowerKey = key.toLowerCase().replace("_", "").replace("-", "");
            for (String mapKey : map.keySet()) {
                String normalizedMapKey = mapKey.toLowerCase().replace("_", "").replace("-", "");
                if (normalizedMapKey.equals(lowerKey) && !map.get(mapKey).isEmpty()) {
                    return map.get(mapKey);
                }
            }
        }
        return "";
    }

    /**
     * Parse XML from string content
     */
    public List<FMBEndpoint> parseGenericFromString(String xmlContent) throws IOException, SAXException {
        List<FMBEndpoint> endpoints = new ArrayList<>();
        
        Document doc = documentBuilder.parse(new org.xml.sax.InputSource(
                new java.io.StringReader(xmlContent)));
        doc.getDocumentElement().normalize();

        NodeList allElements = doc.getDocumentElement().getChildNodes();
        String elementName = detectElementName(allElements);

        NodeList recordNodes = doc.getElementsByTagName(elementName);
        
        for (int i = 0; i < recordNodes.getLength(); i++) {
            Element element = (Element) recordNodes.item(i);
            FMBEndpoint endpoint = parseGenericElement(element);
            if (endpoint != null) {
                endpoints.add(endpoint);
            }
        }

        return endpoints;
    }

    /**
     * Custom field mapping for specific XML formats
     */
    public void setFieldMapping(String expectedField, String xmlElementName) {
        fieldMappings.put(expectedField, xmlElementName);
    }

    /**
     * Get raw XML structure info for debugging
     */
    public Map<String, Integer> getXMLStructure(String xmlFilePath) throws IOException, SAXException {
        Map<String, Integer> structure = new HashMap<>();
        
        Document doc = documentBuilder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();

        NodeList allNodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < allNodes.getLength(); i++) {
            Node node = allNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String name = node.getNodeName();
                structure.put(name, structure.getOrDefault(name, 0) + 1);
            }
        }

        return structure;
    }
}
