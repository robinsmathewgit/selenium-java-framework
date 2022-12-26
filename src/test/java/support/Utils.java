package support;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {
	
	final static String SPINNERS_IN_THE_APPLICATION = "div[id='Spinner']";
	
	public static ExpectedCondition<Boolean> waitForSpinnersToDisappear() {
	    return new ExpectedCondition<Boolean>() {
	        @Override
	        public Boolean apply(WebDriver driver) {
	        	List<WebElement> spinners = driver.findElements(By.cssSelector(SPINNERS_IN_THE_APPLICATION));
	        	
	        	for(WebElement spinner : spinners) {
	        		try {
	        			if(spinner.isDisplayed()) {
	        				return false;
	        			}
	        		} catch (NoSuchElementException | StaleElementReferenceException e) {
						return false;
					}
	        	}
	        	return true;
	        }
	    };
	}
	
	/**
	 * Waits for all the spinners in the application to be disappeared
	 * @param driver
	 */
	public static void waitForPageLoad(WebDriver driver)
	{
		for(int i=0; i < 5; i++) {
			try {
				Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
				wait.until(waitForSpinnersToDisappear());
				Thread.sleep(250);
			}
			catch (Exception e) {}
		}
	}	

	/**
	 * Mouse over to web element 
	 * @param driver
	 * @param element
	 */
	public static void mouseOver(WebDriver driver, WebElement element) {
		// actions class with moveToElement() method for mouse hover to element
		Actions a = new Actions(driver);
		a.moveToElement(element).build().perform();
	}
	
	/**
	 * Switches to iFrame
	 * @param driver
	 * @param name
	 */
	public static void switchToIframe(WebDriver driver, WebElement frame) {
		driver.switchTo().frame(frame);
	}
}
