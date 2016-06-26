package com.customize.reporter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import com.customize.reporter.model.AbstractLog;
import com.customize.reporter.model.PageContents;
import com.customize.reporter.services.LogAction;

abstract class AbstractReporter {
	private volatile static List<com.customize.reporter.services.LogAction> actionList = new ArrayList<LogAction>();

	private static String output;
	private static DataSaver saver = null;
	private String baseFileName = UUID.randomUUID().toString();

	private WebDriver driver;
	private AbstractLog currentLog;

	void setLog(AbstractLog log) {
		this.currentLog = log;
	}

	protected String getBaseFileName() {
		return baseFileName;
	}

	protected AbstractLog getLog() {
		return this.currentLog;
	}

	protected WebDriver getDriver() {
		return this.driver;
	}

	protected void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	protected DataSaver getSaver() {
		return saver;
	}

	protected List<com.customize.reporter.services.LogAction> getActionList() {
		return actionList;
	}

	/**
	 * Sets string path to the output
	 * 
	 * @param rootFolder
	 *            path to the output folder
	 */
	public static void setTestNGOutputFolder(String rootFolder) {
		output = rootFolder;
	}

	/**
	 * <ol>
	 * <li>Provides saver with path to output information.
	 * <li>Initializes saver.<br>
	 * <li>Creates if missing output directories.<br>
	 * </ol>
	 */
	public static void init() {
		saver = new SaverFileSystem(output);
		saver.init();
	}

	/**
	 * The actual Reporting mechanism should ensure that it provides for a
	 * specific implementation for this.
	 * 
	 * @param takeScreenshot
	 *            <b>true/false</b> take or not and save screenshot
	 * @param saveSrc
	 *            <b>true/false</b> save or not page source
	 * @return - An {@link AbstractLog} subclass that represents the actual log
	 *         that was generated.
	 */
	protected abstract AbstractLog createLog(boolean takeScreenshot,
			boolean saveSrc);

	protected void generateLog(boolean takeScreenshot, boolean saveSrc) {
		try {
			AbstractLog log = createLog(takeScreenshot, saveSrc);

			String screenshotPath = null;
			log.setScreen(null);

			if (takeScreenshot && driver != null) {
				// screenshot
				byte[] screenshot = Gatherer.takeScreenshot(driver);
				if( screenshot != null) {
					PageContents screen = new PageContents(screenshot, baseFileName);
					screenshotPath = saver.saveScreenshot(screen);
					log.setScreen(screenshotPath);
				}
			}
			// creating a string from all the info for the report to deserialize
			Reporter.log(log.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param action
	 *            - A {@link com.customize.reporter.services.LogAction} object that represents the custom log
	 *            action to be invoked when
	 *            {@link WebReporter#log(WebDriver, String, boolean, boolean)} gets called.
	 * 
	 */
	public static void addLogAction(LogAction action) {
		if (!actionList.contains(action)) {
			actionList.add(action);
		}
	}

}
