package testingChallenge.Pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;

import support.Utils;

public class CheckoutPage extends LoadableComponent<CheckoutPage> {

	WebDriver driver;
	
	final String CSS_LOCATOR_CHECKOUT_BUTTON = "a[class*='checkout button']"; // CSS selector for checkout button
		
	public CheckoutPage(WebDriver driver) 
	{
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, 2);
		PageFactory.initElements(finder, this);
	}

	@Override
	protected void load() 
	{ 
		Utils.waitForPageLoad(driver);
	}

	@Override
	protected void isLoaded() throws Error 
	{
		if(!driver.getTitle().contains("Order"))
		{
			Assert.fail("Checkout page could not be loaded or server down");
		}
	}
	
	/**
	 * Clicks checkout button
	 * @throws Exception
	 */
	public final void clickCheckoutButton() throws Exception {
		try {
			driver.findElement(By.cssSelector(CSS_LOCATOR_CHECKOUT_BUTTON)).click();
		} catch (Exception e) {
			throw new Exception("CheckoutButtonNotAvailableException : Checkout button is not available");
		}
	}
}
