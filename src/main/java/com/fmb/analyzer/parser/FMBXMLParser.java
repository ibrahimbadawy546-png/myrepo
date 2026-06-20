package com.fmb.analyzer.parser;

import com.fmb.analyzer.model.FMBEndpoint;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Parser for FMB XML format
 */
public class FMBXMLParser {
    private static final Logger logger = Logger.getLogger(FMBXMLParser.class.getName());

    private DocumentBuilder documentBuilder;

    public FMBXMLParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        this.documentBuilder = factory.newDocumentBuilder();
    }

    /**
     * Parse FMB XML file and extract endpoints
     */
    public List<FMBEndpoint> parse(String xmlFilePath) throws IOException, SAXException {
        List<FMBEndpoint> endpoints = new ArrayList<>();
        
        Document doc = documentBuilder.parse(new File(xmlFilePath));
        doc.getDocumentElement().normalize();

        // Extract endpoints from various possible XML structures
        NodeList endpointNodes = doc.getElementsByTagName("endpoint");
        
        for (int i = 0; i < endpointNodes.getLength(); i++) {
            Element endpointElement = (Element) endpointNodes.item(i);
            FMBEndpoint endpoint = parseEndpoint(endpointElement);
            if (endpoint != null) {
                endpoints.add(endpoint);
            }
        }

        logger.info("Parsed " + endpoints.size() + " endpoints from XML");
        return endpoints;
    }

    /**
     * Parse individual endpoint element
     */
    private FMBEndpoint parseEndpoint(Element element) {
        try {
            FMBEndpoint endpoint = new FMBEndpoint();

            // Extract basic information
            endpoint.setName(getElementText(element, "name"));
            endpoint.setEndpoint(getElementText(element, "path"));
            endpoint.setMethod(getElementText(element, "method"));
            endpoint.setCategory(getElementText(element, "category"));
            endpoint.setDescription(getElementText(element, "description"));

            // Extract block type information
            String blockType = getElementText(element, "blockType");
            endpoint.setDirect("DIRECT".equalsIgnoreCase(blockType));
            endpoint.setIndirect("INDIRECT".equalsIgnoreCase(blockType));

            // Extract complex logic
            endpoint.setBusinessLogic(getElementText(element, "businessLogic"));
            endpoint.setCommitLogic(getElementText(element, "commitLogic"));
            endpoint.setHistoryTracking(getElementText(element, "historyTracking"));

            return endpoint;
        } catch (Exception e) {
            logger.warning("Error parsing endpoint: " + e.getMessage());
            return null;
        }
    }

    /**
     * Utility method to get text content of an element
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String text = element.getTextContent();
                return text != null ? text.trim() : "";
            }
        }
        return "";
    }

    /**
     * Parse FMB XML from string content
     */
    public List<FMBEndpoint> parseFromString(String xmlContent) throws IOException, SAXException {
        List<FMBEndpoint> endpoints = new ArrayList<>();
        
        Document doc = documentBuilder.parse(new org.xml.sax.InputSource(
                new java.io.StringReader(xmlContent)));
        doc.getDocumentElement().normalize();

        NodeList endpointNodes = doc.getElementsByTagName("endpoint");
        
        for (int i = 0; i < endpointNodes.getLength(); i++) {
            Element endpointElement = (Element) endpointNodes.item(i);
            FMBEndpoint endpoint = parseEndpoint(endpointElement);
            if (endpoint != null) {
                endpoints.add(endpoint);
            }
        }

        return endpoints;
    }
}
