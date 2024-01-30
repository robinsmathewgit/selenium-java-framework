# Selenium Java TestNG Maven Framework - Selenium Java TestNG Project

This project is a Selenium-based automation framework developed in Java, utilizing TestNG for test execution, Maven for project management and dependency resolution, and Extent Report for detailed test reporting.

## Prerequisites

Before you begin, ensure you have the following tools installed:

- *Java Development Kit (JDK):* [Download JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
- *Apache Maven:* [Download Maven](https://maven.apache.org/download.cgi)

## Project Structure

plaintext
cancom_ui_automation
│
├── src
│   └── test
│       ├── java
│       │   ├── pages
│       │   ├── tests
│       │   └── support
│       └── resources
├── pom.xml
└── testNgXml
	├── SmokeTest.xml
	└── RegressionTest.xml


- src/test: Contains test code.
- pom.xml: Maven project configuration.
- testNgXml: TestNG configuration files.

## Setup and Usage

1. *Clone the Repository:*
   bash
   git clone 
   cd selenium-java-framework
   

2. *Build the Project:*
   bash
   mvn clean install
   

3. *Run Tests:*
   bash
   mvn test -Dsuite='TestNgXml\YOUR_SUITE.xml'
   

## Test Scripts

Create your test scripts in the src/test/java directory. An example TestNG class is provided in the template.

java
// Example TestNG class
public class SampleTest {

    @Test
    public void testSample() {
        // Your Selenium test logic here
    }
}


## Maven Commands

- *Build the project:* mvn clean install
- *Run tests:* mvn test -Dsuite='TestNgXml\YOUR_SUITE.xml'

## TestNG Configuration (testng.xml)

Create a testng.xml file in the project root:

xml
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="TestSuite">
    <test name="Test">
        <classes>
            <class name="your.package.SampleTest"/>
        </classes>
    </test>
</suite>

