package de.kjosu.kgdx;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.files.FileHandle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class KGDXLogger implements ApplicationLogger {

	private FileHandle directory = KGDX.files.external(String.format("%s/logs", KGDX.main.appName));
	private FileHandle logFile;

	private final DateFormat logFileFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final DateFormat logDateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");

	private int sysoutLogLevel = Lwjgl3Application.LOG_DEBUG;
	private int fileLogLevel = Lwjgl3Application.LOG_ERROR;

	public KGDXLogger() {
		directory.mkdirs();
		updateLogFile();
	}

	@Override
	public void log (String tag, String message) {
		String output = getFormattedLogMessage(tag, message, null);

		if (sysoutLogLevel >= Lwjgl3Application.LOG_INFO) {
			System.out.println(output);
		}

		if (fileLogLevel >= Lwjgl3Application.LOG_INFO) {
			updateLogFile();
			logFile.writeString(output, true);
		}
	}

	@Override
	public void log (String tag, String message, Throwable exception) {
		String output = getFormattedLogMessage(tag, message, exception);

		if (sysoutLogLevel >= Lwjgl3Application.LOG_INFO) {
			System.out.println(output);
		}

		if (fileLogLevel >= Lwjgl3Application.LOG_INFO) {
			updateLogFile();
			logFile.writeString(output, true);
		}
	}

	@Override
	public void error (String tag, String message) {
		String output = getFormattedLogMessage(tag, message, null);

		if (sysoutLogLevel >= Lwjgl3Application.LOG_ERROR) {
			System.err.println(output);
		}

		if (fileLogLevel >= Lwjgl3Application.LOG_ERROR) {
			updateLogFile();
			logFile.writeString(output, true);
		}
	}

	@Override
	public void error (String tag, String message, Throwable exception) {
		String output = getFormattedLogMessage(tag, message, exception);

		if (sysoutLogLevel >= Lwjgl3Application.LOG_ERROR) {
			System.err.println(output);
		}

		if (fileLogLevel >= Lwjgl3Application.LOG_ERROR) {
			updateLogFile();
			logFile.writeString(output, true);
		}
	}

	@Override
	public void debug (String tag, String message) {
		String output = getFormattedLogMessage(tag, message, null);

		if (sysoutLogLevel >= Lwjgl3Application.LOG_DEBUG) {
			System.out.println(output);
		}

		if (fileLogLevel >= Lwjgl3Application.LOG_DEBUG) {
			updateLogFile();
			logFile.writeString(output, true);
		}
	}

	@Override
	public void debug (String tag, String message, Throwable exception) {
		String output = getFormattedLogMessage(tag, message, exception);

		if (sysoutLogLevel >= Lwjgl3Application.LOG_DEBUG) {
			System.out.println(output);
		}

		if (fileLogLevel >= Lwjgl3Application.LOG_DEBUG) {
			updateLogFile();
			logFile.writeString(output, true);
		}
	}

	private String getFormattedLogMessage(String tag, String message, Throwable exception) {
		StringBuilder output = new StringBuilder();

		output.append(getFormattedDate(logDateFormat))
			.append(" - ").append(tag)
			.append(" - ").append(message);

		String stackTrace = getStackTrace(exception);
		if (KGDXUtils.isNullOrEmpty(stackTrace)) {
			output.append("\r\n").append(exception);
		}

		return output.toString();
	}

	private String getFormattedDate(DateFormat format) {
		return format.format(Calendar.getInstance().getTime());
	}

	private String getStackTrace(Throwable exception) {
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
			exception.printStackTrace(pw);
			return sw.toString();
		} catch (NullPointerException | IOException ignored) {
			return null;
		}
	}

	private void updateLogFile() {
		String fileName = String.format("%s.log", getFormattedDate(logFileFormat));
		logFile = directory.child(fileName);

		try {
			logFile.file().createNewFile();
		} catch (IOException ignored) {

		}
	}

	public FileHandle getDirectory() {
		return directory;
	}

	public void setDirectory(FileHandle directory) {
		this.directory = directory;
		updateLogFile();
	}

	public int getSysoutLogLevel() {
		return sysoutLogLevel;
	}

	public void setSysoutLogLevel(int sysoutLogLevel) {
		this.sysoutLogLevel = sysoutLogLevel;
	}

	public int getFileLogLevel() {
		return fileLogLevel;
	}

	public void setFileLogLevel(int fileLogLevel) {
		this.fileLogLevel = fileLogLevel;
	}
}
