package testingChallenge.Tests;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import support.Log;
import support.TestDataExtractor;
import support.WebDriverUtils;
import testingChallenge.Pages.CheckoutPage;
import testingChallenge.Pages.HomePage;
import testingChallenge.Pages.LoginOrRegistrationPage;
import testingChallenge.Pages.ProductPage;

public class ProductPurchase {

	private static String webSite;
	private static String xltestDataWorkBook;
	private static String xltestDataWorkSheet;
	
	@BeforeClass
	public void init(ITestContext context) {

		ProductPurchase.webSite = System.getProperty("webSite") != null ? System.getProperty("webSite") : context.getCurrentXmlTest().getParameter("webSite");
		ProductPurchase.xltestDataWorkBook = context.getCurrentXmlTest().getParameter("testDataFileName");
		ProductPurchase.xltestDataWorkSheet = context.getCurrentXmlTest().getParameter("workSheet");
	}
	
	@Test(description = "Purchase a product")
	public void TC_001 (ITestContext context) throws Exception {
		
		Log.testCaseInfo("Purchase a product");
		final TestDataExtractor testData = new TestDataExtractor(xltestDataWorkBook, xltestDataWorkSheet, "TC_001");
		final WebDriver driver = WebDriverUtils.getDriver("chrome");
		Log.addTestRunMachineInfo(driver);
		
		try {
			
			testData.setTestCaseId(Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());
			testData.readData();
			
			// Step 1: Open Browser & launch site
			HomePage homePage = new HomePage(driver, webSite).get();
			Log.message("01- Launched shopping website. (" + webSite + ")", driver);
			
			//Select Summer dress from Women
			ProductPage productPage = homePage.selectSummerDressFromWomen();
			Log.message("02 - Selected Summer Dress link under Women tab");
			
			if(!productPage.isSummerDressPageLoaded()) {
				Log.fail("Test Failed - Summer Dress page is not loaded", driver);
			}
			
			Log.message("03 - Summer dress page is loaded as expected");
			
			String product = testData.get("Data");
			
			if(!productPage.mouseOverDressName(product)) {
				Log.fail("Test Failed - Failed to Mouse over '"+ product +"'", driver);
			}
			
			Log.message("04 - Mouse over 'Printed Summer Dress' worked as expected", driver);
			
			productPage.selectQuickView();
			Log.message("05 - Clicked Quick View button", driver);
			
			productPage.QuickViewWindow.selectSize("S");
			Log.message("06 - Selected size 'S'", driver);
			
			productPage.QuickViewWindow.clickAddToCartButton();
			Log.message("07 - Clicked Add To Cart button", driver);
			
			productPage.QuickViewWindow.clickContinueShoppingButton();
			Log.message("08 - Clicked Continue shopping button", driver);
			
			CheckoutPage checkoutPage = productPage.Header.mouseOverCartAndClickCheckoutbutton();
			Log.message("09 - Hovered over to Cart section and clicked Check Out button", driver);
			
			checkoutPage.clickCheckoutButton();
			Log.message("10 - Clicked Proceed to checkout button", driver);
			
			LoginOrRegistrationPage loginOrRegistrationPage = new LoginOrRegistrationPage(driver).get();
			Log.message("11 - Registration page loaded", driver);
			
			String emailString = RandomStringUtils.randomAlphanumeric(10) + "@test.com";
			loginOrRegistrationPage.enterEmailAndClickCreateAccountButton(emailString);
			Log.message("12- -Entered email address and clicked Create Account button", driver);
			
			List<String> enteredFields = loginOrRegistrationPage.enterAllRequiredFields();
			for (String fields : enteredFields) {
				Log.message("--- "+fields);
			}
			
			loginOrRegistrationPage.clickSubmitAccountButton();
			Log.message("13 - Clicked Submit account button", driver);
			
			loginOrRegistrationPage.clickProceedToCheckoutButton();
			Log.message("14 - Clicked Proceed to checkout button", driver);
			
			loginOrRegistrationPage.clickTermsAndConditionsCheckbox();
			Log.message("15 - Clicked Terms and Conditions checkbox", driver);
			
			loginOrRegistrationPage.clickProceedToCheckoutButton();
			Log.message("16 - Clicked Proceed to checkout button", driver);
			
			if(!loginOrRegistrationPage.isProductNameSameAsSelected(product)) {
				Log.fail("Test Failed - Product name not displayed as expected", driver);
			}
			
			Log.pass("Test Passed - Product name displayed as expected", driver);

		}
		catch (Exception e) {
			Log.exception(e, driver);
		}
		finally {
			driver.quit();
		}
		
	} //TC_001
}

