package testingChallenge.Pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;

import support.Utils;

public class ProductPage extends LoadableComponent<ProductPage> {

	WebDriver driver;
	public QuickViewWindow QuickViewWindow;
	public Header Header;
	
	final String CSS_LOCATOR_PRODUCT_LIST_NAME = "ul[class^='product_list'] a[class='product-name']"; // CSS selector for Product List Name
	final String CSS_QUICK_HOVERED_VIEW_BUTTON = "li[class$='hovered'] a[class='quick-view'] span"; // CSS Selector for hovered quick view button;
		
	public ProductPage(WebDriver driver) 
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
		if(!driver.getTitle().contains("- My Store"))
		{
			Assert.fail("Product page could not be loaded or server down");
		}
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, 2);

		// Reinitialize QuickViewWindow on each load
		QuickViewWindow = new QuickViewWindow(driver);
		PageFactory.initElements(finder, QuickViewWindow);

		// Reinitialize Header on each load
		Header = new Header(driver);
		PageFactory.initElements(finder, Header);
	}
	
	/**
	 * Returns true if the page loaded is summer dress page
	 * @return
	 */
	final public Boolean isSummerDressPageLoaded() {
		return driver.getTitle().contains("Summer Dresses");		
	}
	
	/**
	 * Mouse over on the dress with a specific name
	 * @param dressName - Name of the dress
	 * @throws Exception 
	 */
	final public Boolean mouseOverDressName(String dressName) throws Exception {
		for (WebElement iterable_element : driver.findElements(By.cssSelector(CSS_LOCATOR_PRODUCT_LIST_NAME))) {
			if(iterable_element.getText().equalsIgnoreCase(dressName)) {
				Utils.mouseOver(driver, iterable_element); //Mouse Over element
				break;
			}
		}
		try {
			return driver.findElement(By.cssSelector(CSS_QUICK_HOVERED_VIEW_BUTTON)).isDisplayed();
		} catch (Exception e) {
			throw new Exception("MouseOverFailedException : Quick view button not displayed on mouse over");
		} 
	}
	
	/**
	 * Clicks on the quick view button displayed on mouse over the product
	 * @param dressName
	 * @throws Exception
	 */
	final public void selectQuickView() throws Exception {
		try {
			driver.findElement(By.cssSelector(CSS_QUICK_HOVERED_VIEW_BUTTON)).click();
		} catch (Exception e) {
			throw new Exception("QuickViewButtonNotAvailableException : Quick view button is not available");
		}
	}
}
