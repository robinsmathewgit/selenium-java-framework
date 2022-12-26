package support;

import java.io.File;
import java.io.IOException;

import org.testng.Reporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.common.io.Files;

public class ExtentManager {
	private static ExtentReports extent;
	private static File outputDir = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
	private static String reportFileName = "Test-Automation-Extent-Unformatted.html";
	private static String reportFilePath = outputDir.getParent();
	private static String reportFileLocation = reportFilePath + File.separator + reportFileName;
	
	public static ExtentReports getInstance() {
		if(extent == null) 
			createInstance();
		return extent;
	}
	
	// Create an extent report instance
	public static ExtentReports createInstance() {
		String fileName = getReportPath(reportFilePath);
		try {
			Files.copy(new File("src/test/resources/logos/report-logo.png"), new File(reportFilePath + File.separator + "report-logo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ExtentSparkReporter htmlReporter = new ExtentSparkReporter(fileName);
		
		try {
			htmlReporter.loadXMLConfig("extent-config.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		htmlReporter.config().setDocumentTitle("Test Automation Report");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setTimeStampFormat("EEEE, MMM dd, yyyy, hh:mm a '('zzz')'");
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		
		// Set environment details
		extent.setSystemInfo("Test Execution Environment", "Selenium Grid");
		extent.setSystemInfo("Application URL", "");
		extent.setSystemInfo("Browser", "");
		
		return extent;
	}
	
	// Create the report path
	private static String getReportPath(String path) {
		File testDirectory = new File(path);
		if(!testDirectory.exists()) {
			if(testDirectory.mkdir()) {
				System.out.println("Directory: " + path + " is created!");
				return reportFileLocation;
			}
			else {
				System.out.println("Failed to create directory: " + path);
				return System.getProperty("user.dir");
			}
		}
		else {
			System.out.println("Directory already exists: " + path);
		}
		return reportFileLocation;
	}
}
