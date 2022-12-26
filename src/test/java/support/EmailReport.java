package support;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.reporters.EmailableReporter2;
import org.testng.xml.XmlSuite;

import com.google.common.io.Files;

public class EmailReport extends EmailableReporter2 {

	PrintWriter emailOut;
	String fileName;
	static String unescapePattern = "\\<div\\sclass=\"messages\">(.*)\\<\\/div\\>";
	static String startTestTitle = "<div class=\"test-title\"> <strong><font size = \"3\" color = \"#000080\">";
	static String endTestTitle = "</font> </strong> </div>&emsp;<div><strong>Steps:</strong></div><!-- Report -->";
	static Boolean ignoreMethodeName = false;
	
	@Override
	public void generateReport(List <XmlSuite> xml, List <ISuite> suites, String outdir) {

		super.generateReport(xml, suites, outdir);
		File eScripts = new File("jsscripts.txt");
		File eCSS = new File("ReportCSS.txt");

		try {
			
			File eReport = new File(outdir + File.separator + "TestAutomationResults.html");
			File eReport1 = new File(outdir + File.separator + "emailable-report.html");
			
			File extentReport = new File(outdir + File.separator + "Test-Automation-Extent-Report.html");
			File extentReport1 = new File(outdir + File.separator + "Test-Automation-Extent-Unformatted.html");

			FileUtils.copyFile(eReport, eReport1);
			String eContent = FileUtils.readFileToString(eReport, "UTF-8");

			Files.copy(extentReport1, extentReport);
			FileUtils.copyFile(extentReport, extentReport1);
			String extentContent = FileUtils.readFileToString(extentReport, "UTF-8");

			Pattern p = Pattern.compile(unescapePattern, Pattern.DOTALL);
			Matcher matcher = p.matcher(eContent);
			int matchCount = 0;

			while (matcher.find()) {
				matchCount++;
			}

			matcher = p.matcher(eContent);

			for (int i = 0; i < matchCount; i++) {
				matcher.find();
				String unEscapePart = matcher.group(1);
				unEscapePart = unEscapePart.replace("&lt;", "<"); // removing the HTML escaping in the email report
				unEscapePart = unEscapePart.replace("&gt;", ">"); // removing the HTML escaping in the email report
				unEscapePart = unEscapePart.replace("&quot;", "\"");
				unEscapePart = unEscapePart.replace("&apos;", "'");
				unEscapePart = unEscapePart.replace("&amp;", "&");
				eContent = eContent.replace(matcher.group(1), unEscapePart);
			}

			long minStartTime = 0;
			long maxEndTime = 0;
			long temp = 0;
			
			String suiteName = "";

			// Adding Test method - description to Summary Table (i.e)Test case title
			for (SuiteResult suiteResult : super.suiteResults) {
				
				suiteName = suiteResult.getSuiteName();

				String ignoreMethodeNameParameter = suites.get(suiteResults.indexOf(suiteResult)).getParameter("ignoreMethodeName");;		
				
				if(ignoreMethodeNameParameter != null && ignoreMethodeNameParameter.contains("true"))
					ignoreMethodeName = true;
				
				for (TestResult testResult : suiteResult.getTestResults()) {
					
					for (ClassResult classResult : testResult.getFailedTestResults()) {

						for (MethodResult methodResult : classResult.getMethodResults()) {

							for (ITestResult tResult : methodResult.getResults()) {

								temp = tResult.getStartMillis();

								String exceptionReplacement = tResult.getThrowable().getMessage(); // Replace stake trace with original unescape them

								if (!(tResult.getThrowable() instanceof java.lang.AssertionError) && exceptionReplacement != null && !exceptionReplacement.isEmpty()) {

									if (exceptionReplacement.indexOf("(Session") > 0)
										exceptionReplacement = exceptionReplacement.substring(0, exceptionReplacement.indexOf("(Session") - 1).trim();

									String exceptionToReplace = exceptionReplacement;
									exceptionReplacement = exceptionReplacement.replace("&", "&amp;");
									exceptionReplacement = exceptionReplacement.replace("<", "&lt;");
									exceptionReplacement = exceptionReplacement.replace(">", "&gt;");
									exceptionReplacement = exceptionReplacement.replace("\"", "&quot;");
									exceptionReplacement = exceptionReplacement.replace("'", "&apos;");
									eContent = eContent.replace(exceptionToReplace, exceptionReplacement);
								}

								if (minStartTime == 0 || temp < minStartTime)
									minStartTime = temp;

								temp = tResult.getEndMillis();

								if (maxEndTime == 0 || temp > maxEndTime)
									maxEndTime = temp;

								if (!tResult.getMethod().isTest())
									continue;

								String methodDescription = getTestTitle(Reporter.getOutput(tResult).toString());
								String methodName = tResult.getMethod().getMethodName();
								
								if (methodDescription.isEmpty())
									methodDescription = tResult.getMethod().getDescription();

								String toReplace = "<a href=\"#m([0-9]{1,4})\">" + methodName + "</a>";
								
								String toReplaceBy = "";
								
								if(ignoreMethodeName)
									toReplaceBy = "<a href=\"#m$1\">"+ methodDescription + "</a>";
								else
									toReplaceBy = "<a href=\"#m$1\">" + methodName + ": " + methodDescription + "</a>";
								
								eContent = eContent.replaceFirst(toReplace, toReplaceBy);
							}

						}

					}
					
					
					for (ClassResult classResult : testResult.getSkippedTestResults()) {

						for (MethodResult methodResult : classResult.getMethodResults()) {

							for (ITestResult tResult : methodResult.getResults()) {

								temp = tResult.getStartMillis();

								if (minStartTime == 0 || temp < minStartTime)
									minStartTime = temp;

								temp = tResult.getEndMillis();

								if (maxEndTime == 0 || temp > maxEndTime)
									maxEndTime = temp;

								if (!tResult.getMethod().isTest())
									continue;

								String methodName = tResult.getMethod().getMethodName();
								String methodDescription = getTestTitle(Reporter.getOutput(tResult).toString());

								if (methodDescription.isEmpty())
									methodDescription = tResult.getMethod().getDescription();

								String toReplace = "<a href=\"#m([0-9]{1,4})\">" + methodName + "</a>";
								
								String toReplaceBy = "";
								
								if(ignoreMethodeName)
									toReplaceBy = "<a href=\"#m$1\">"+ methodDescription + "</a>";
								else
									toReplaceBy = "<a href=\"#m$1\">" + methodName + ": " + methodDescription + "</a>";
								
								eContent = eContent.replaceFirst(toReplace, toReplaceBy);
							}

						}

					}

					for (ClassResult classResult : testResult.getPassedTestResults()) {

						for (MethodResult methodResult : classResult.getMethodResults()) {

							for (ITestResult tResult : methodResult.getResults()) {

								temp = tResult.getStartMillis();

								if (minStartTime == 0 || temp < minStartTime)
									minStartTime = temp;

								temp = tResult.getEndMillis();

								if (maxEndTime == 0 || temp > maxEndTime)
									maxEndTime = temp;

								if (!tResult.getMethod().isTest())
									continue;

								String methodName = tResult.getMethod().getMethodName();
								String methodDescription = getTestTitle(Reporter.getOutput(tResult).toString());
								
								if (methodDescription.isEmpty())
									methodDescription = tResult.getMethod().getDescription();

								String toReplace = "<a href=\"#m([0-9]{1,4})\">" + methodName + "</a>";
								
								String toReplaceBy = "";
								
								if(ignoreMethodeName)
									toReplaceBy = "<a href=\"#m$1\">"+ methodDescription + "</a>";
								else
									toReplaceBy = "<a href=\"#m$1\">" + methodName + ": " + methodDescription + "</a>";
								
								eContent = eContent.replaceFirst(toReplace, toReplaceBy);

							}
						}
					}	
				}
			}

			eContent = eContent.replace("</head>", "\r</head>\r");
			eContent = eContent.replace("<table", "\r\t<table");
			eContent = eContent.replace("</table>", "\r\t</table>\r");
			eContent = eContent.replaceFirst("<table>", "<table id='suitesummary' title=\"Filters results based on cell clicked/Shows all result on double-click\">");
			eContent = eContent.replaceFirst("<table>", "<table id='summary'>");

			eContent = eContent.replace("<thead>", "\r\t<thead>\r");
			eContent = eContent.replace("</thead>", "\r\t</thead>\r");
			eContent = eContent.replace("<tbody>", "\r\t<tbody>\r");
			eContent = eContent.replace("</tbody>", "\r\t</tbody>\r");

			eContent = eContent.replace("<h2", "\r\t\t<h2");
			eContent = eContent.replace("<tr", "\r\t\t<tr");
			eContent = eContent.replace("</tr>", "\r\t\t</tr>\r");
			eContent = eContent.replace("<td>", "\r\t\t\t<td>");
			eContent = eContent.replace("</td>", "\r\t\t\t</td>\r");
			eContent = eContent.replace("<th", "\r\t\t\t<th");
			eContent = eContent.replace("</th>", "\r\t\t\t</th>");
			eContent = eContent.replace("<br/>", "");
			eContent = eContent.replace("runScenario:", "Scenario:");
			
			// This is to add a <br> for test name in TestNG.xml file
			eContent = eContent.replace("-Break Line- ", "<br>");
			
			eContent = eContent.replaceAll("<style(.*)</style>", "\r" + FileUtils.readFileToString(eCSS, "utf-8") + "\r");
			eContent = eContent.replace("<head>", "<head>" + "\r" + FileUtils.readFileToString(eScripts, "utf-8") + "\r");
			eContent = eContent.replace("<head>", "<head>" + "\r<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />\r");

			eContent = eContent.replaceFirst("<table id='suitesummary' title=\"Filters results based on cell clicked/Shows all result on double-click\">",
					"<table id='suitesummary' title=\"Filters results based on cell clicked/Shows all result on double-click\" duration=\"" + (maxEndTime - minStartTime) + "\">");
			
			FileUtils.writeStringToFile(eReport, eContent, "ISO-8859-1");
			
			String toReplaceLogo = "<span class=\"badge badge-primary\">";
			String replaceByLogo = "<span class=\"badge badge-primary\" style=\"background-color:#08111c;\">";
			
			String toReplaceExtentReportLogo = "<div class=\"logo\"";
			String replaceByExtentReportLogo = "<div";
			
			String toReplaceReportDescription = "</ul>\r\n" +
					"</div>\r\n"+ 
					"</div><div class=\"side-nav\">";
			String replaceByReportDescription = "<li class=\"m-r-10\">\r\n" +
					"a href=\"#\"><span class=\"badge badge-primary\">" + suiteName + "</span></a>\r\n" +
					"</li>\r\n" +
					"</ul>\r\n" +
					"</div>\r\n" +
					"</div><div class=\"side-nav\">";
			
			String toReplaceIcon = "<link rel=\"shortcut icon\" href=\"https://cdn.jsdelivr.net/gh/extent-framework/extent-github-cdn@b00a2d0486596e73dd7326beacf352c639623a0e/commons/img/logo.png\">";
			String replaceByIcon = "<link rel=\"shortcut icon\" href=\"report-logo.png\">";
			
			extentContent = extentContent.replaceFirst(toReplaceIcon, replaceByIcon);
			extentContent = extentContent.replaceAll(toReplaceLogo, replaceByLogo);
			extentContent = extentContent.replaceFirst(toReplaceExtentReportLogo, replaceByExtentReportLogo);
			extentContent = extentContent.replaceFirst(toReplaceReportDescription, replaceByReportDescription);
			
			FileUtils.writeStringToFile(extentReport, extentContent, "ISO-8859-1");

		}
		catch (IOException e) {
			e.printStackTrace();
		}

		try {

			FileReader fr = null;
			fr = new FileReader(outdir + File.separator + "TestAutomationResults.html");
			BufferedReader br = new BufferedReader(fr);
			StringBuilder content = new StringBuilder(10000);
			String s;
			int tableCount = 0;

			String hub = "localhost";

			try {
				hub = (suites.get(0).getHost() == null) ? Inet4Address.getLocalHost().getHostName() : suites.get(0).getHost();
			}
			catch (UnknownHostException e) {
			}

			while ((s = br.readLine()) != null) {

				content.append(s + "\n");
				if (s.trim().contains("</table>"))
					tableCount++;

				if (s.startsWith("<body")) {

					content.append("<p> Hi, </p>" + "\n" + "<p> Test automation scripts execution completed. Find the results summary below. </p>" + "\n"
							+ "<p> <font color=\"blue\"><b>Note:</b> Please find the link to detailed results at the bottom of the email; Recommend to use <b>Chrome or Firefox </b> to view the results. </font></p>" + "\n" + "<p> <u><h3> Test Run Details: </h3> </u>"
							+ "\r<table  bordercolor=\"#FFFFF\"> </u></h3> </p>\r" +

							"\r<pre style=\"font-size: 1.2em;\">\r" + "   <b>Test Name</b> : " + System.getProperty("testname") + "\r" + "   <b>Suite Name</b>: " + System.getProperty("suiteFile")
							+ "\r" + "   <b>Run Date</b>  : " + (new Date()).toString() + "\r" + "   <b>Test Name</b> : " + System.getProperty("name") + "\r" + "   <b>Run By</b>    : " + hub + "\r"
							+ "</pre>" + "<br><br>\n");
				}

				if (tableCount == 1) {
					content.append("<p><br></p><p> Thanks, <br>Test Automation Team</p>\n</body>\n</html>");
					break;
				}

			}

			String emailContent = content.toString();
			File emailMsg = new File("." + "\\src\\test\\java\\AutomationTestResultsEmail.html".replace("\\", File.separator));
			FileUtils.writeStringToFile(emailMsg, emailContent, "ISO-8859-1");
			
			br.close();
			fr.close();

			// adding files/folders to be added on zip folder
			List <String> fileName = new ArrayList <String>();
			fileName.add(outdir + File.separator + "TestAutomationResults.html");
			fileName.add(outdir + File.separator + "ScreenShot");

			String ouputFile = outdir + File.separator + "AutomationTestSummaryReport.zip";
			FolderZiper folderZiper = new FolderZiper();
			folderZiper.zipFolder(fileName, ouputFile);

		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public static String getTestTitle(String content) {

		Pattern p = Pattern.compile(startTestTitle + "(.*)" + endTestTitle, Pattern.DOTALL);
		Matcher matcher = p.matcher(content);

		try {
			if (matcher.find())
				return matcher.group(1);
			else
				return "";
		}
		catch (IllegalStateException e) {
			return "";
		}

	}

	@Override
	protected PrintWriter createWriter(String outdir) throws IOException {
		new File(outdir).mkdirs();
		emailOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, "TestAutomationResults.html"))));
		return emailOut;
	}
	
}
