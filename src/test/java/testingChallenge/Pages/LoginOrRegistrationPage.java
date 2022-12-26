package testingChallenge.Pages;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import support.Utils;

public class LoginOrRegistrationPage extends LoadableComponent<LoginOrRegistrationPage> {

	WebDriver driver;
	public Header Header;
	
	final String CSS_LOCATOR_EMAIL_INPUT = "input[id='email_create']"; // CSS selector Email input
	final String CSS_LOCATOR_CREATE_ACCOUNT = "button[id='SubmitCreate']"; // CSS selector for Create an account
	final String CSS_LOCATOR_SUBMIT_ACCOUNT = "button[id='submitAccount']"; // CSS selector for Submit account	
	final String CSS_LOCATOR_ALL_REQUIRED_INPUT_FIELDS = "input[class^='is_required'], p[class^='required'] input"; // All input fields for registration
	final String CSS_LOCATOR_STATE_SELECT = "select[id='id_state']"; // State to select
	final String CSS_LOCATOR_COUNTRY_SELECT = "select[id='id_country']"; // Country to select
	final String CSS_LOCATOR_PROCEED_TO_CHECKOUT = "button[name='processAddress'], button[name='processCarrier']"; // CSS Selector for proceed to checkout
	final String CSS_LOCATOR_T_C_CHECKBOX = "input[id='cgv']";
	final String CSS_PRODUCT_NAME_AT_CHECKOUT = "p[class='product-name']";
		
	public LoginOrRegistrationPage(WebDriver driver) 
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
		if(!driver.getTitle().contains("Login"))
		{
			Assert.fail("Login/Registration page could not be loaded or server down");
		}
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, 2);

		// Reinitialize Header on each load
		Header = new Header(driver);
		PageFactory.initElements(finder, Header);
	}
	
	/**
	 * Enter an email and click on 'Create an Account' button
	 * @param emailInput
	 * @return
	 * @throws InterruptedException 
	 */
	final public void enterEmailAndClickCreateAccountButton(String emailInput) throws InterruptedException {
		driver.findElement(By.cssSelector(CSS_LOCATOR_EMAIL_INPUT)).sendKeys(emailInput);
		driver.findElement(By.cssSelector(CSS_LOCATOR_CREATE_ACCOUNT)).click();
		Thread.sleep(3000);
		Utils.waitForPageLoad(driver);
	}
	
	/**
	 * Click submit an account button
	 * @param emailInput
	 * @return
	 */
	final public void clickSubmitAccountButton() {
		driver.findElement(By.cssSelector(CSS_LOCATOR_SUBMIT_ACCOUNT)).click();
		Utils.waitForPageLoad(driver);
	}
	
	/**
	 * Enters all the required fields in the registration form
	 * @return
	 */
	final public List<String> enterAllRequiredFields() {
		
		List<String> allFieldsSelected = new ArrayList<String>();
		
		for (WebElement element : driver.findElements(By.cssSelector(CSS_LOCATOR_ALL_REQUIRED_INPUT_FIELDS))) {
			
			String valueToEnter = RandomStringUtils.randomAlphabetic(10);
			
			if(element.getAttribute("id").equalsIgnoreCase("postcode")) {
				valueToEnter = RandomStringUtils.randomNumeric(5);				
			}
			else if(element.getAttribute("id").equalsIgnoreCase("phone_mobile")) {
				valueToEnter = RandomStringUtils.randomNumeric(10);				
			}
			
			try {
				element.sendKeys(valueToEnter);	
			} catch (Exception e) {	}
			allFieldsSelected.add("Entered value " + valueToEnter + " for " + element.getAttribute("name"));
		}		
		
		Select stateSelect = new Select(driver.findElement(By.cssSelector(CSS_LOCATOR_STATE_SELECT)));
		Select countrySelect = new Select(driver.findElement(By.cssSelector(CSS_LOCATOR_COUNTRY_SELECT)));
		stateSelect.selectByIndex(1);
		countrySelect.selectByIndex(1);	
		return allFieldsSelected;
	}
	
	/**
	 * Clicks proceed to checkout button
	 * @throws Exception
	 */
	public final void clickProceedToCheckoutButton() throws Exception {
		try {
			driver.findElement(By.cssSelector(CSS_LOCATOR_PROCEED_TO_CHECKOUT)).click();
			Utils.waitForPageLoad(driver);
		} catch (Exception e) {
			throw new Exception("CheckoutButtonNotAvailableException : Checkout button is not available");
		}
	}
	
	/**
	 * Checks T&C
	 * @throws Exception
	 */
	public final void clickTermsAndConditionsCheckbox() throws Exception {
		try {
			driver.findElement(By.cssSelector(CSS_LOCATOR_T_C_CHECKBOX)).click();
			Utils.waitForPageLoad(driver);
		} catch (Exception e) {
			throw new Exception("TandCCheckboxNotAvailableException : T & C check box is not available");
		}
	}
	 
	/**
	 * Returns true if expected product name displayed
	 * @param name
	 */
	public final Boolean isProductNameSameAsSelected(String name) {
		return driver.findElement(By.cssSelector(CSS_PRODUCT_NAME_AT_CHECKOUT)).getText().contains(name);
	}
}
