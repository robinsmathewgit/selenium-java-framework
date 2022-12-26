package support;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    public synchronized void onStart(ITestContext context) {}

    public synchronized void onFinish(ITestContext context) {
        ExtentTestManager.endTest();
        ExtentManager.getInstance().flush();
    }
    
    public synchronized void onTestStart(ITestResult result) {}

    public synchronized void onTestSuccess(ITestResult result) {}

    public synchronized void onTestFailure(ITestResult result) {}

    public synchronized void onTestSkipped(ITestResult result) {}

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
}