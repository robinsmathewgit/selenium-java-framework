package testingChallenge.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import support.Utils;

public final class QuickViewWindow {

	final String CSS_LOCATOR_SELECT_SIZE = "select[id='group_1']"; // CSS selector for selecting size
	final String CSS_LOCATOR_ADD_TO_CART_BUTTON = "p[id='add_to_cart'] button"; // CSS CSS Selector for Add to cart button
	final String CSS_LOCATOR_CONTINUE_SHOPPING_BUTTON = "span[title='Continue shopping']"; // CSS Selector for continue shopping button
	final String CSS_LOCATOR_IFRAME = "iframe[class='fancybox-iframe']";

	private final WebDriver driver;

	/**
	 * constructor of the QuickViewWindow class
	 * 
	 * @param driver
	 *            Selenium Web driver
	 */
	public QuickViewWindow(final WebDriver driver) {
		this.driver = driver;
	}
	
	/**
	 * Selects size from dropdown
	 * @param sizeText
	 */
	final public void selectSize(String sizeText) {
		Utils.switchToIframe(driver, driver.findElement(By.cssSelector(CSS_LOCATOR_IFRAME)));
		Select sizeSelect = new Select(driver.findElement(By.cssSelector(CSS_LOCATOR_SELECT_SIZE)));
		sizeSelect.selectByVisibleText(sizeText);
		driver.switchTo().defaultContent();
	}
	
	/**
	 * Clicks add to cart button
	 * @throws Exception
	 */
	final public void clickAddToCartButton() throws Exception {

		Utils.switchToIframe(driver, driver.findElement(By.cssSelector(CSS_LOCATOR_IFRAME)));
		try {
			driver.findElement(By.cssSelector(CSS_LOCATOR_ADD_TO_CART_BUTTON)).click();
			Utils.waitForPageLoad(driver);
		} catch (Exception e) {
			driver.switchTo().defaultContent();
			throw new Exception("AddToCartButtonNotAvailableException - Failed to click add to cart button");
		}
		driver.switchTo().defaultContent();
	}
	
	/**
	 * Clicks continue shopping button
	 * @throws Exception 
	 */
	final public void clickContinueShoppingButton() throws Exception {
		try {
			driver.findElement(By.cssSelector(CSS_LOCATOR_CONTINUE_SHOPPING_BUTTON)).click();
			Utils.waitForPageLoad(driver);
		} catch (Exception e) {
			throw new Exception("ContinueShoppingButtonNotAvailableException - Failed to click Continue Shopping button");
		}
	}

}
