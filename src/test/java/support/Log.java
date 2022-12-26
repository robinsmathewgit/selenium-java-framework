package support;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aventstack.extentreports.Status;

public class Log {

	public static boolean printconsoleoutput = false;
	private static String screenShotFolderPath;
	private static String browserLogFolderPath;
	private static AtomicInteger screenShotIndex = new AtomicInteger(0);
	private static AtomicInteger browserLogIndex = new AtomicInteger(0);
	
	final static String TEST_TITLE_HTML_BEGIN = "&emsp;<div class=\"test-title\"> <strong><font size = \"3\" color = \"#000080\">";
	final static String TEST_TITLE_HTML_END = "</font> </strong> </div>&emsp;<div><strong>Steps:</strong></div>";
	
	final static String EXTENT_TEST_TITLE_HTML_BEGIN = "&emsp;<div class=\"test-title\"> <strong><font size = \"3\"> Test Description: ";
	final static String EXTENT_TEST_TITLE_HTML_END = "</font> </strong> </div>&emsp;";
	
	final static String TEST_COND_HTML_BEGIN = "&emsp;<div class=\"test-title\"> <strong><font size = \"3\" color = \"#0000FF\">";
	final static String TEST_COND_HTML_END = "</font> </strong> </div>&emsp;";

	final static String MESSAGE_HTML_BEGIN = "<div class=\"test-message\">&emsp;";
	final static String MESSAGE_HTML_END = "</div>";
	
	final static String PASS_HTML_BEGIN = "<div class=\"test-result\"><br><font color=\"green\"><strong> ";
	final static String PASS_HTML_END1 = " </strong></font> ";
	final static String PASS_HTML_END2 = "</div>&emsp;";

	final static String FAIL_HTML_BEGIN = "<div class=\"test-result\"><br><font color=\"red\"><strong> ";
	final static String FAIL_HTML_END1 = " </strong></font> ";
	final static String FAIL_HTML_END2 = "</div>&emsp;";

	final static String FAIL_SOFT_HTML_BEGIN = "<div class=\"test-result\">&emsp; <small><font color=\"red\"><strong> ";
	final static String FAIL_SOFT_HTML_END1 = " </strong> </font> </small>";
	final static String FAIL_SOFT_HTML_END2 = "</div>&emsp;";

	final static String SKIP_EXCEPTION_HTML_BEGIN = "<div class=\"test-result\"><br><font color=\"orange\"><strong> ";
	final static String SKIP_HTML_END1 = " </strong></font> ";
	final static String SKIP_HTML_END2 = " </strong></font> ";

	final static String EVENT_HTML_BEGIN = "<div class=\"test-event\"> <font color=\"maroon\"> <small> &emsp; &emsp;--- "; 
	final static String EVENT_HTML_END = "</small> </font> </div>";
	
	/******************************************************************************************************************
	 * 
	 * This static block ensures to clear the screenshot folder if any in the output at beginning of each suite
	 * Also sets up the print console flag for the run
	 * Careful in making changes, will stop runs / builds
	 * 
	 *******************************************************************************************************************/
	static {

		File screenShotFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
		screenShotFolderPath = screenShotFolder.getParent() + File.separator + "ScreenShot" + File.separator;
		screenShotFolder = new File(screenShotFolderPath);

		File browserLogFolder = new File(Reporter.getCurrentTestResult().getTestContext().getOutputDirectory());
		browserLogFolderPath = browserLogFolder.getParent() + File.separator + "BrowserLogs" + File.separator;
		browserLogFolder = new File(browserLogFolderPath);

		if (!screenShotFolder.exists()) {
			screenShotFolder.mkdir();
		}

		if (!browserLogFolder.exists()) {
			browserLogFolder.mkdir();
		}

		File[] screenShots = screenShotFolder.listFiles();

		if (screenShots != null && screenShots.length > 0) { //delete files if the folder has any
			for (File screenShot : screenShots)
				screenShot.delete();
		}

		File[] browserLogs = browserLogFolder.listFiles();

		if (browserLogs != null && browserLogs.length > 0) { //delete files if the folder has any
			for (File browserLog : browserLogs)
				browserLog.delete();
		}
	
		final Map <String, String> params = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getAllParameters();

		if (params.containsKey("printconsoleoutput")) //set print console flag
			Log.printconsoleoutput = Boolean.parseBoolean(params.get("printconsoleoutput"));

	} //static block
	
	/**
	 * Creates Browser Log Text File
	 * @param driver
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static String createBrowserConsoleLog(WebDriver driver) throws TransformerException, ParserConfigurationException, IOException {
		
		String consoleLogXmlFileString = "";
		
		try {
			
			consoleLogXmlFileString = Reporter.getCurrentTestResult().getName() + "_cl_" + browserLogIndex.incrementAndGet() + ".xml";
			File consoleLogTextFile = new File(browserLogFolderPath + consoleLogXmlFileString);
			consoleLogTextFile.createNewFile();
			
			LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
			
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			// root element
			Element testSuite = document.createElement("ConsoleLogs");
			document.appendChild(testSuite);
			
			for(LogEntry entry : logEntries) {
				
				Element entryNode = document.createElement("Entry");
				testSuite.appendChild(entryNode);
				
				entryNode.setAttribute("level", entry.getLevel().toString());
				entryNode.setAttribute("timeStamp", sdfDate.format(entry.getTimestamp()));
				entryNode.setAttribute("message", entry.getMessage());
			}
			
			// Create the xml file
			// Transform the DOM object to an XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(consoleLogTextFile);
			transformer.transform(domSource, streamResult);
			
		} catch (WebDriverException e) {
			consoleLogXmlFileString = "";
		}
		
		return consoleLogXmlFileString;
	}
	
	/**
	 * Creates Browser Log Text File
	 * @param driver
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static String createBrowserNetworkLog(WebDriver driver) throws TransformerException, ParserConfigurationException, IOException {
		
		String consoleLogXmlFileString = "";
		
		try {
			
			consoleLogXmlFileString = Reporter.getCurrentTestResult().getName() + "_nw_" + browserLogIndex.incrementAndGet() + ".xml";
			File consoleLogTextFile = new File(browserLogFolderPath + consoleLogXmlFileString);
			consoleLogTextFile.createNewFile();
			
			List<LogEntry> networkEntries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
			
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			
			// root element
			Element testSuite = document.createElement("NetworkLogs");
			document.appendChild(testSuite);
			
			for(Iterator<LogEntry> it = networkEntries.iterator(); it.hasNext();) {
				
				LogEntry entry = it.next();
				
				try {
					JSONObject json = new JSONObject(entry.getMessage());
					JSONObject message = json.getJSONObject("message");
					String method = message.getString("method");
					
					if(method != null && "Network.responseRecieved".equals(method)) {
						Element entryNode = document.createElement("Entry");
						testSuite.appendChild(entryNode);
						
						JSONObject params = message.getJSONObject("params");
						JSONObject response = message.getJSONObject("response");
						JSONObject headers = message.getJSONObject("headers");
						
						String timeStamp = "";
						String url = "";
						int status = 0;
						
						try { timeStamp = headers.getString("Date");} catch (Exception e) {}
						try { url = headers.getString("url");} catch (Exception e) {}
						try { status = headers.getInt("status");} catch (Exception e) {}
						
						entryNode.setAttribute("params", params.toString());
						entryNode.setAttribute("response", response.toString());
						entryNode.setAttribute("headers", headers.toString());
						
						if(timeStamp.isEmpty())
							entryNode.setAttribute("timeStamp", timeStamp);
						if(url.isEmpty())
							entryNode.setAttribute("url", url);
						if(status != 0)
							entryNode.setAttribute("status", Integer.toString(status));						
					}					
				} catch (JSONException e) { }
			}
			
			// Create the xml file
			// Transform the DOM object to an XML file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(consoleLogTextFile);
			transformer.transform(domSource, streamResult);
			
		} catch (WebDriverException e) {
			consoleLogXmlFileString = "";
		}
		
		return consoleLogXmlFileString;
	}
	
	public static String takeScreenShot(WebDriver driver) {
		
		String inputFile = "";
		
		for (int i=0; i<3; i++) {
		
			try {
				inputFile = Reporter.getCurrentTestResult().getName() + "_" + screenShotIndex.incrementAndGet() + ".png";
				File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshot, new File(screenShotFolderPath + inputFile));
				screenshot.delete();
				break;
			}
			catch (IOException | WebDriverException e1) { }
			
		}
		
		return inputFile;

	}
	
	public static String getBrowserConsoleLogsHyperLink(String inputFile) {
		
		if(inputFile.trim().isEmpty()) {
			return "";
		}
		
		String browserLogLink = "<a href=\"." + File.separator + "BrowserLogs" + File.separator + inputFile + "\" target=\"_blank\" >[Console Log]</a>";
		return browserLogLink;
	}
	
	public static String getBrowserNetworkLogsHyperLink(String inputFile) {
		
		if(inputFile.trim().isEmpty()) {
			return "";
		}
		
		String browserLogLink = "<a href=\"." + File.separator + "BrowserLogs" + File.separator + inputFile + "\" target=\"_blank\" >[Network Log]</a>";
		return browserLogLink;
	}
	
	public static String getScreenShotHyperLink(String inputFile) {

		if(inputFile.trim().isEmpty()) {
			return "";
		}
		
		String screenShotLink = "<a href=\"." + File.separator + "ScreenShot" + File.separator + inputFile + "\" target=\"_blank\" >[ScreenShot]</a>";
		return screenShotLink;
	}
	
	public static void addTestRunMachineInfo(WebDriver driver) {

		Object params[] = Reporter.getCurrentTestResult().getParameters();
		String testMachine = "";
		String hub = "localhost";

		try {
			hub = (Reporter.getCurrentTestResult().getHost() == null) ? Inet4Address.getLocalHost().getHostName() : Reporter.getCurrentTestResult().getHost();
		}
		catch (UnknownHostException e) { }

		try {
			testMachine = "(Browser: " + ((RemoteWebDriver) driver).getCapabilities().getBrowserName() + ", Hub: " + hub + ")";
		}
		catch (Exception e) { }

		params[0] = testMachine + ", " + params[0].toString().trim();
		Reporter.getCurrentTestResult().setParameters(params);

	}

	public static void testCaseInfo(String description) {
		
		if (Reporter.getOutput(Reporter.getCurrentTestResult()).toString().contains("<div class=\"test-title\">"))
			Reporter.log(TEST_TITLE_HTML_BEGIN + description + TEST_TITLE_HTML_END + "&emsp;");
		else
			Reporter.log(TEST_TITLE_HTML_BEGIN + description + TEST_TITLE_HTML_END + "<!-- Report -->&emsp;");
		
		ExtentTestManager.startTest(Reporter.getCurrentTestResult().getMethod().getMethodName() + " - " + description);
		ExtentTestManager.getTest().log(Status.INFO, EXTENT_TEST_TITLE_HTML_BEGIN + description + EXTENT_TEST_TITLE_HTML_END + "&emsp;");

		System.out.println(description.replace("&emsp;", "\t"));
	}

	public static void testConditionInfo(String description) {

		
		Reporter.log(TEST_COND_HTML_BEGIN + description + TEST_COND_HTML_END);
		System.out.println(description.replace("&emsp;", "\t"));
		
	}

	public static void message(String description) {

		Reporter.log(MESSAGE_HTML_BEGIN + description + MESSAGE_HTML_END);
		
		ExtentTestManager.getTest().log(Status.INFO, MESSAGE_HTML_BEGIN + description.replace("--- ", "&#9670; ") + MESSAGE_HTML_END);

		if (printconsoleoutput)
			System.out.println(description.replace("&emsp;", "\t"));
	}

	public static void message(String description, WebDriver driver) {

		String inputFile = takeScreenShot(driver);
		Reporter.log(MESSAGE_HTML_BEGIN + description + "&emsp;" + getScreenShotHyperLink(inputFile) + MESSAGE_HTML_END);
		
		ExtentTestManager.getTest().log(Status.INFO, MESSAGE_HTML_BEGIN + description.replace("--- ", "&#9670; ") + getScreenShotHyperLink(inputFile) + MESSAGE_HTML_END);

		if (printconsoleoutput)
			System.out.println(description.replace("&emsp;", "\t"));
	}

	public static void event(String description) {

		String currDate = new SimpleDateFormat("dd MMM HH:mm:ss SSS").format(Calendar.getInstance().getTime());
		Reporter.log(EVENT_HTML_BEGIN + currDate + " - " + description + EVENT_HTML_END);

		if (printconsoleoutput)
			System.out.println("\t--- " + description + "[Time: " + currDate + "]");
	
	}

	public static void event(String description, long duration) {

		String currDate = new SimpleDateFormat("dd MMM HH:mm:ss SSS").format(Calendar.getInstance().getTime());
		Reporter.log(EVENT_HTML_BEGIN + currDate + " - <b>" + duration + "</b> - " + description + 	" - " + Thread.currentThread().getStackTrace()[2].toString() + EVENT_HTML_END);

		if (printconsoleoutput)
			System.out.println("\t--- " + description + "[Duration: " + duration + "] " + "[" + "Time: " +  currDate + "]" + "["
					+ Thread.currentThread().getStackTrace()[2].toString() + "]");
	}

	public static void pass(String description) {

		Reporter.log(PASS_HTML_BEGIN + description + PASS_HTML_END1 + PASS_HTML_END2);
		
		ExtentTestManager.getTest().log(Status.PASS, PASS_HTML_BEGIN + description + PASS_HTML_END1 + PASS_HTML_END2);

		if (printconsoleoutput)
			System.out.println("Pass: " + description.replace("&emsp;", "\t"));
	}

	public static void pass(String description, WebDriver driver) {

		String inputFile = takeScreenShot(driver);
		Reporter.log(PASS_HTML_BEGIN + description + PASS_HTML_END1 + getScreenShotHyperLink(inputFile) + PASS_HTML_END2);

		if (printconsoleoutput)
			System.out.println("Pass: " + description.replace("&emsp;", "\t"));
	}

	public static void fail(String description, WebDriver driver) throws TransformerException, ParserConfigurationException, IOException {

		String inputFile = takeScreenShot(driver);
		String consoleLogFile = createBrowserConsoleLog(driver);
		String networkLogFile = createBrowserNetworkLog(driver);

		Reporter.log(FAIL_HTML_BEGIN + description + FAIL_HTML_END1 + " " + getBrowserConsoleLogsHyperLink(consoleLogFile) + " " + getBrowserNetworkLogsHyperLink(networkLogFile) + " " + getScreenShotHyperLink(inputFile) + FAIL_HTML_END2);
		
		ExtentTestManager.getTest().log(Status.FAIL, FAIL_HTML_BEGIN + description + FAIL_HTML_END1 + " " + getBrowserConsoleLogsHyperLink(consoleLogFile) + " " + getBrowserNetworkLogsHyperLink(networkLogFile) + " " + getScreenShotHyperLink(inputFile) + FAIL_HTML_END2);

		if (printconsoleoutput)
			System.out.println("Fail: " + description.replace("&emsp;", "\t"));

		Assert.fail(description);

	}

	public static void failsoft(String description, WebDriver driver) {

		String inputFile = takeScreenShot(driver);
		Reporter.log(FAIL_SOFT_HTML_BEGIN + description + FAIL_SOFT_HTML_END1 + getScreenShotHyperLink(inputFile) + FAIL_SOFT_HTML_END2);
		
		ExtentTestManager.getTest().log(Status.INFO, FAIL_SOFT_HTML_BEGIN + description + FAIL_SOFT_HTML_END1 + getScreenShotHyperLink(inputFile) + FAIL_SOFT_HTML_END2);

		if (printconsoleoutput)
			System.out.println("Fail: " + description.replace("&emsp;", "\t"));

	}
	
	public static void exception(Exception e, WebDriver driver) throws Exception {

		String inputFile = takeScreenShot(driver);
		String consoleLogFile = createBrowserConsoleLog(driver);
		String networkLogFile = createBrowserNetworkLog(driver);

		String eMessage = e.getMessage();
		
		if (eMessage != null && eMessage.contains("\n"))
			eMessage = eMessage.substring(0, eMessage.indexOf("\n"));
		
		if (e instanceof SkipException) {
			Reporter.log(SKIP_EXCEPTION_HTML_BEGIN + eMessage + SKIP_HTML_END1 + " " + getBrowserConsoleLogsHyperLink(consoleLogFile) + " " + getBrowserNetworkLogsHyperLink(networkLogFile) + " " + getScreenShotHyperLink(inputFile) + SKIP_HTML_END2);			
			ExtentTestManager.getTest().log(Status.SKIP, SKIP_EXCEPTION_HTML_BEGIN + eMessage + SKIP_HTML_END1 + " " + getBrowserConsoleLogsHyperLink(consoleLogFile) + " " + getBrowserNetworkLogsHyperLink(networkLogFile) + " " + getScreenShotHyperLink(inputFile) + SKIP_HTML_END2);
		}
		else {
			Reporter.log(FAIL_HTML_BEGIN + eMessage + FAIL_HTML_END1 + " " + getBrowserConsoleLogsHyperLink(consoleLogFile) + " " + getBrowserNetworkLogsHyperLink(networkLogFile) + " " + getScreenShotHyperLink(inputFile) + FAIL_HTML_END2);			
			ExtentTestManager.getTest().log(Status.FAIL, FAIL_HTML_BEGIN + eMessage + FAIL_HTML_END1 + " " + getBrowserConsoleLogsHyperLink(consoleLogFile) + " " + getBrowserNetworkLogsHyperLink(networkLogFile) + " " + getScreenShotHyperLink(inputFile) + FAIL_HTML_END2);
		}

		throw e;

	}

	public static void exception(Exception e) throws Exception {

		String eMessage = e.getMessage();
		
		if (eMessage != null && eMessage.contains("\n"))
			eMessage = eMessage.substring(0, eMessage.indexOf("\n"));

		if (e instanceof SkipException) {
			Reporter.log(SKIP_EXCEPTION_HTML_BEGIN + eMessage + SKIP_HTML_END1 + SKIP_HTML_END2);			
			ExtentTestManager.getTest().log(Status.SKIP, SKIP_EXCEPTION_HTML_BEGIN + eMessage + SKIP_HTML_END1 + SKIP_HTML_END2);
		}
		else {
			Reporter.log(FAIL_HTML_BEGIN + eMessage + FAIL_HTML_END1 + FAIL_HTML_END2);		
			ExtentTestManager.getTest().log(Status.FAIL, FAIL_HTML_BEGIN + eMessage + FAIL_HTML_END1 + FAIL_HTML_END2);
		}

		throw e;

	}

}
