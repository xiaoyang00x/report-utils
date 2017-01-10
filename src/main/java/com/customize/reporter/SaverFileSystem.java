package com.customize.reporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.customize.reporter.model.PageContents;

/**
 * {@link DataSaver} that stores the info on the file system.
 */
public class SaverFileSystem implements DataSaver {

	private String outputFolder;

	public SaverFileSystem(String testNGOutputFolder) {
		this.outputFolder = testNGOutputFolder.endsWith(File.separator) ? testNGOutputFolder
				: testNGOutputFolder + File.separator;
	}

	@Override
	public String saveScreenshot(PageContents s) {
		String screenshotAbsolutePath = getScreenshotAbsolutePath(s.getId());
		try (OutputStream out = new FileOutputStream(screenshotAbsolutePath)) {
			out.write(s.getScreenImage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String screenshotUrl = "screenshots/" + s.getId() + ".png";
		return screenshotUrl;

	}

	private String getScreenshotAbsolutePath(String name) {
		String screenshotPath = "screenshots" + File.separator + name + ".png";
		String screenshotAbsolutePath = outputFolder + screenshotPath;
		return screenshotAbsolutePath;
	}

	public String saveSources(PageContents s) {
		/*
		 * Made output to txt file not html
		 */
		String path = outputFolder + "sources" + File.separator + s.getId() + ".source.txt";
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF8"))) {
			out.write(s.getPageSource());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	@Override
	public PageContents getScreenshotByName(String name) throws IOException {
		String path = getScreenshotAbsolutePath(name);
		File f = new File(path);
		byte[] bytes = getBytesFromFile(f);
		PageContents returnValue = new PageContents(bytes, name);
		return returnValue;
	}

	// package protected for testing
	static byte[] getBytesFromFile(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			// Get the size of the file
			long length = file.length();

			// Create the byte array to hold the data
			byte[] bytes = new byte[(int) length];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				IOException e = new IOException("Could not completely read file " + file.getName());
				if (is != null) {
					is.close();
				}
				throw e;
			}

			return bytes;
		}

	}

	/**
	 * @see com.customize.reporter.DataSaver#init() Creates
	 *      directories sources, html, screenshots based off output folder
	 */
	@Override
	public void init() {
		(new File(outputFolder)).mkdirs();
		(new File(outputFolder, "sources")).mkdir();
		(new File(outputFolder, "html")).mkdir();
		(new File(outputFolder, "screenshots")).mkdir();
		(new File(outputFolder, "video")).mkdir();
	}
}
