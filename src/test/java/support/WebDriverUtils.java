package support;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.xml.XmlTest;

import com.aventstack.extentreports.Status;

public class WebDriverUtils {
	
	static String driverHost = null;
	static String driverPort = null;
	static URL hubURL ;
	static AtomicInteger browserType = new AtomicInteger(0);
	public static ExpectedCondition<Boolean> documentLoad;
	public static ExpectedCondition<Boolean> framesLoad;
	
	static {
		
		documentLoad = new ExpectedCondition <Boolean>() {
			
			final public Boolean apply(final WebDriver driver) {

				final JavascriptExecutor js = (JavascriptExecutor) driver;
				boolean docReadyState = (Boolean) js.executeScript("return (document.readyState==\"complete\")");
				return docReadyState;
			}
			
		};
		
		framesLoad = new ExpectedCondition <Boolean>() {
			
			final public Boolean apply(final WebDriver driver) {

				try {
					
					JavascriptExecutor js;
					List<WebElement> frames = driver.findElements(By.tagName("iframe:not([style*='height: 0px'])"));
					boolean docReadyState = true;
					
					for (WebElement frame : frames) {
						
						try {
							
							driver.switchTo().defaultContent();
							driver.switchTo().frame(frame);
							js = (JavascriptExecutor) driver;
							docReadyState = docReadyState && (Boolean) js.executeScript("return (document.readyState==\"complete\")");
							driver.switchTo().defaultContent();
							if (!docReadyState) break;
							
						}
						catch(WebDriverException e) { }
					}
						
					return docReadyState;
				}
				finally {
					driver.switchTo().defaultContent();
				}
			}
			
		};
		
		XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
		driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost"): test.getParameter("deviceHost");
		driverPort = test.getParameter("devicePort");
		
		try {
			hubURL = new URL("http://" + driverHost + ":" + driverPort + "/wd/hub");
		}
		catch (MalformedURLException e) { 	}
	}
	
	public static WebDriver getDriver(String driverType) {

		WebDriver driver = null;

		try {
						
			if (driverType.equalsIgnoreCase("chrome")) {
				
				ChromeOptions chrome_options = new ChromeOptions();   
                chrome_options.addArguments("--start-maximized");
                
                LoggingPreferences logPrefs = new LoggingPreferences();
                logPrefs.enable(LogType.BROWSER, Level.ALL);
                logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
                chrome_options.setCapability("goog:loggingPrefs", logPrefs);
                
				driver = new RemoteWebDriver(hubURL,chrome_options);				
				
			}
			
			else if (driverType.equalsIgnoreCase("IE")) {
				//Not implemented
			}
			else {
				//Not implemented
			}

			Assert.assertNotNull(driver, "Driver did not intialize...\n Please check if hub is running / configuration settings are corect.");
			
		}
		catch (UnreachableBrowserException e) {
			e.printStackTrace();
			ExtentTestManager.getTest().log(Status.SKIP, "Hub is not started or down. " + e.getMessage());
			throw new SkipException("Hub is not started or down.");
		}
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
			ExtentTestManager.getTest().log(Status.SKIP, "There are some error in the code. " + e.getMessage());
			throw new SkipException("Hub is not started or down.");
		}
		catch (WebDriverException e) {	
			ExtentTestManager.getTest().log(Status.FAIL, e.getMessage());		
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			ExtentTestManager.getTest().log(Status.SKIP, "Exception encountered in getDriver Method. " + e.getMessage());
			Assert.fail("Exception encountered in getDriver Method." + e.getMessage().toString());
		}

		finally {
			// /************************************************************************************************************
			// * Update start time of the tests once free slot is assigned by RemoteWebDriver - Just for reporting purpose
			// *************************************************************************************************************/
			 try {
				 Field f = Reporter.getCurrentTestResult().getClass().getDeclaredField("m_startMillis");
				 f.setAccessible(true);
				 f.setLong(Reporter.getCurrentTestResult(), Calendar.getInstance().getTime().getTime());
			 }
			 catch (Exception e) {
			 }
		}
		Log.event("Driver::Get");
		Log.addTestRunMachineInfo(driver);
		return driver;

	}

}