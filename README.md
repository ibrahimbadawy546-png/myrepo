# FMB XML Analyzer

A comprehensive Java application for analyzing FMB (Functional Module Builder) XML format and generating detailed endpoint analysis reports.

## Features

### 1. **XML Parsing**
- Parse FMB XML files with structured endpoint definitions
- Extract endpoint metadata (name, path, method, category)
- Support for complex logic definitions (commit logic, history tracking)

### 2. **Endpoint Categorization**
Automatically categorizes endpoints into the following categories:

| Category | Description |
|----------|-------------|
| **LOVs** | List of Values - Static reference data lookups |
| **POST-CHANGE** | Post-change validation and processing |
| **Validations** | Data validation rules and constraints |
| **CRUD (Direct)** | Create, Read, Update, Delete - Direct operations |
| **CRUD (Indirect)** | Create, Read, Update, Delete - Indirect operations |
| **Actions** | Custom actions and operations |
| **Complex (Key-next calculations)** | Key-next calculations and computations |
| **Complex (Commit logic)** | Commit transaction logic |
| **Complex (History)** | Historical tracking and audit trails |

### 3. **Comprehensive Reports**
- **Summary Table**: Category, Count, and Key Endpoints
- **Category Breakdown**: Percentage distribution visualization
- **Block Type Analysis**: Direct vs. Indirect block classification
- **Complex Logic Analysis**: Commit logic and history tracking details
- **HTTP Method Distribution**: GET, POST, PUT, DELETE distribution
- **Detailed Endpoint Report**: Complete endpoint information

### 4. **Auto-Detection**
Intelligent auto-detection of endpoint categories based on:
- Endpoint name patterns
- Endpoint path patterns
- Block type (Direct/Indirect)
- Complex logic presence

## Project Structure

```
myrepo/
├── pom.xml
├── README.md
├── sample_fmb.xml
└── src/main/java/com/fmb/analyzer/
    ├── FMBAnalyzerMain.java           # Main entry point
    ├── model/
    │   ├── FMBEndpoint.java            # Endpoint model
    │   ├── EndpointCategory.java       # Category enum
    │   └── SummaryRecord.java          # Summary record
    ├── parser/
    │   └── FMBXMLParser.java           # XML parser
    ├── analyzer/
    │   └── FMBAnalyzer.java            # Analysis engine
    └── report/
        └── SummaryReportGenerator.java  # Report generator
```

## Building the Project

```bash
# Clone the repository
git clone https://github.com/ibrahimbadawy546-png/myrepo.git
cd myrepo

# Build with Maven
mvn clean install
```

## Running the Application

### Method 1: From Maven
```bash
mvn exec:java -Dexec.mainClass="com.fmb.analyzer.FMBAnalyzerMain"
```

### Method 2: From Command Line
```bash
# Navigate to target directory
cd target

# Run with sample data
java -cp "fmb-xml-analyzer-1.0.0.jar:lib/*" com.fmb.analyzer.FMBAnalyzerMain

# Run with your XML file
java -cp "fmb-xml-analyzer-1.0.0.jar:lib/*" com.fmb.analyzer.FMBAnalyzerMain
# When prompted, enter your XML file path
```

## Usage

### Interactive Menu
The application provides an interactive menu:

```
╔════════════════════════════════════════════════════════════╗
║         FMB XML ANALYZER - ENDPOINT ANALYSIS TOOL           ║
╚════════════════════════════════════════════════════════════╝

Select report type:
1. Summary Table
2. Category Breakdown
3. Block Type Analysis
4. Complex Logic Analysis
5. Detailed Endpoint Report
6. HTTP Method Distribution
7. Comprehensive Report (All)
8. Exit

Enter your choice (1-8):
```

### Sample Summary Table Output

```
====================================================================================================
FMB ENDPOINT ANALYSIS - SUMMARY TABLE
====================================================================================================
Category                       | Count    | Key Endpoints
----------------------------------------------------------------------------------------------------
CRUD (Direct)                  | 3        | /api/employees, /api/employees/{id}, /api/employees
Actions                        | 2        | /api/actions/approve, /api/actions/reject
Complex (History)              | 1        | /api/complex/history/{id}
Complex (Commit logic)         | 1        | /api/complex/commit
Complex (Key-next calculations)| 1        | /api/complex/calc-seq
LOVs                           | 2        | /api/lov/departments, /api/lov/status
POST-CHANGE                    | 1        | /api/post-change/validate
Validations                    | 1        | /api/validate/employee
CRUD (Indirect)                | 1        | /api/employees/{id}/indirect
----------------------------------------------------------------------------------------------------
TOTAL ENDPOINTS: 13
====================================================================================================
```

## XML Format Specification

The FMB XML format should follow this structure:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<fmb>
    <endpoints>
        <endpoint>
            <name>ENDPOINT_NAME</name>
            <path>/api/endpoint/path</path>
            <method>GET|POST|PUT|DELETE</method>
            <category>Category Name</category>
            <description>Endpoint description</description>
            <blockType>DIRECT|INDIRECT</blockType>
            <businessLogic>Optional business logic</businessLogic>
            <commitLogic>Optional commit logic</commitLogic>
            <historyTracking>Optional history tracking logic</historyTracking>
        </endpoint>
    </endpoints>
</fmb>
```

## Model Classes

### FMBEndpoint
```java
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
    // ... getters and setters
}
```

### EndpointCategory
Enum representing all supported categories with display names and descriptions.

### SummaryRecord
```java
public class SummaryRecord {
    private String category;
    private int count;
    private List<String> keyEndpoints;
    // ... methods
}
```

## Analyzer Engine

The `FMBAnalyzer` class provides:
- Automatic endpoint categorization
- Category-based filtering
- Method-based filtering
- Direct/Indirect block filtering
- Complex logic filtering

### Key Methods

```java
// Get endpoints by category
List<FMBEndpoint> getEndpointsByCategory(String category);

// Get endpoints by HTTP method
List<FMBEndpoint> getEndpointsByMethod(String method);

// Get direct block endpoints
List<FMBEndpoint> getDirectBlockEndpoints();

// Get indirect block endpoints
List<FMBEndpoint> getIndirectBlockEndpoints();

// Get endpoints with commit logic
List<FMBEndpoint> getEndpointsWithCommitLogic();

// Get endpoints with history tracking
List<FMBEndpoint> getEndpointsWithHistory();

// Generate summary
List<SummaryRecord> generateSummary();

// Get all endpoints
List<FMBEndpoint> getAllEndpoints();

// Get total endpoint count
int getTotalEndpoints();
```

## Report Generators

The `SummaryReportGenerator` class provides multiple report generation methods:

1. **Summary Table** - Tabular view of all categories with counts and key endpoints
2. **Category Breakdown** - Visual representation with percentage bars
3. **Detailed Report** - Complete endpoint information
4. **Block Type Analysis** - Direct vs. Indirect block distribution
5. **Complex Logic Report** - Commit logic and history tracking details
6. **HTTP Method Distribution** - GET, POST, PUT, DELETE statistics
7. **Comprehensive Report** - All reports combined

## Sample Data

A sample FMB XML file (`sample_fmb.xml`) is included with the project containing:
- 2 LOVs endpoints
- 1 POST-CHANGE endpoint
- 1 Validations endpoint
- 3 CRUD Direct endpoints
- 1 CRUD Indirect endpoint
- 2 Actions endpoints
- 1 Complex calculation endpoint
- 1 Complex commit logic endpoint
- 1 Complex history endpoint

**Total: 13 sample endpoints**

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Dependencies

- **DOM Processing**: W3C DOM API for XML parsing
- **Logging**: SLF4J for logging
- **Testing**: JUnit 4 for unit tests

## Example Usage in Code

```java
// Parse XML
FMBXMLParser parser = new FMBXMLParser();
List<FMBEndpoint> endpoints = parser.parse("sample_fmb.xml");

// Analyze
FMBAnalyzer analyzer = new FMBAnalyzer(endpoints);

// Generate reports
SummaryReportGenerator generator = new SummaryReportGenerator(analyzer);
generator.generateSummaryTable();
generator.generateCategoryBreakdown();
generator.generateComplexLogicReport();

// Query endpoints
List<FMBEndpoint> crudEndpoints = analyzer.getEndpointsByCategory("CRUD (Direct)");
List<FMBEndpoint> postEndpoints = analyzer.getEndpointsByMethod("POST");
List<FMBEndpoint> historyEndpoints = analyzer.getEndpointsWithHistory();
```

## Future Enhancements

- [ ] Export reports to JSON/CSV/Excel formats
- [ ] Web UI dashboard for visualization
- [ ] Performance metrics and analytics
- [ ] REST API endpoints for programmatic access
- [ ] Advanced filtering and search capabilities
- [ ] Custom report templates
- [ ] Database integration for storing analysis results
- [ ] API documentation generation (Swagger/OpenAPI)

## License

This project is open source and available under the MIT License.

## Author

Ibrahim Badawy

## Support

For issues, feature requests, or contributions, please create an issue or pull request on the GitHub repository.
