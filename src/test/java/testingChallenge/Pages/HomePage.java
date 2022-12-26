package testingChallenge.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;
import org.testng.SkipException;

import support.Utils;

public final class HomePage extends LoadableComponent <HomePage> {

	private final WebDriver driver;
	public QuickViewWindow QuickViewWindow;
	public Header Header;
	
	private String URL;
	private boolean isPageLoaded = false;

	final String CSS_LOCATOR_WOMEN_TAB = "a[title='Women']"; // CSS selector for Womens tab
	final String CSS_LOCATOR_SUMMER_DRESSES_LINK = "ul[style^='display: block'] a[title='Summer Dresses']"; // CSS Selector for summer dresses

	/*-----------------------------
		Page Factory WebElements
	-------------------------------*/

	@FindBy(how = How.CSS, using = CSS_LOCATOR_WOMEN_TAB)
	private WebElement womenTab;

	/**
	 * 
	 * Constructor of the class.
	 * 
	 * returns HomePage object for the given driver & URL. 
	 * Please remember to invoke the home page in the browser using get() method. 
	 * It sets up the page factory to AjaxElementLoactor factory.
	 * 
	 * @param driver
	 *            WebDriver
	 * @param URL
	 *            URL of Shopping site for test
	 * 
	 */
	public HomePage(WebDriver driver, String webSite) {

		URL = webSite;
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, 2);
		PageFactory.initElements(finder, this);

	}

	/*-----------------------------------------------------
		Loadable Component & Page Factory Implementation
	--------------------------------------------------------*/
	/**
	 * This is override method of {@link LoadableComponent }
	 * It checks whether the current page in the selenium browser session is Home page
	 * Asserts performed is
	 * Title should be My Store
	 */

	@Override
	final protected void isLoaded() {

		if (!isPageLoaded)
			Assert.fail("Site is down or Current Page is not home page");

		boolean correctTitle = driver.getTitle().contains("My Store");
		
		if (!(correctTitle)) {
			throw new SkipException("WrongPageException: Site is down or Current Page is not home page. [Current URL: " + driver.getCurrentUrl() + "].");
		}

		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, 2);

		// Reinitialize QuickViewWindow on each load
		QuickViewWindow = new QuickViewWindow(driver);
		PageFactory.initElements(finder, QuickViewWindow);

		// Reinitialize Header on each load
		Header = new Header(driver);
		PageFactory.initElements(finder, Header);
		
	} // isloaded

	/**
	 * This is override method of LoadableComponent
	 * 
	 * <p>
	 * It loads the premium mobile site in current selenium browser session.(default or the given URL in the constructor.)
	 * 
	 * Also synchronise until the page gets loaded
	 * 
	 */
	@Override
	final protected void load() {

		driver.get(URL);
		isPageLoaded = true;

	} // load

	/**
	 * Mouse over Womens tab and Selects Summer Dress link
	 */
	final public ProductPage selectSummerDressFromWomen() {

		Utils.mouseOver(driver, womenTab);
		driver.findElement(By.cssSelector(CSS_LOCATOR_SUMMER_DRESSES_LINK)).click();
		return new ProductPage(driver).get();
	}
	
} // HomePage
