package testingChallenge.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import support.Utils;

public final class Header {

	final String CSS_LOCATOR_CART_SECTION = "a[title='View my shopping cart']"; // CSS selector for Cart section
	final String CSS_LOCATOR_CHECKOUT_BUTTON = "a[id='button_order_cart'] i"; // CSS selector for Checkout button
	
	private final WebDriver driver;

	/**
	 * constructor of the header class
	 * 
	 * @param driver
	 *            Selenium Web driver
	 */
	public Header(final WebDriver driver) {
		this.driver = driver;
	}
	
	/**
	 * Mouse over Cart and clicks checkout button
	 * @return
	 * @throws InterruptedException 
	 */
	final public CheckoutPage mouseOverCartAndClickCheckoutbutton() throws InterruptedException {
		Thread.sleep(3000);
		Utils.mouseOver(driver, driver.findElement(By.cssSelector(CSS_LOCATOR_CART_SECTION)));
		driver.findElement(By.cssSelector(CSS_LOCATOR_CHECKOUT_BUTTON)).click();
		return new CheckoutPage(driver).get();
	}	

}
