package com.calderagames.spacelab.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ErrorLog {

	private static Charset charset = Charset.forName("US-ASCII");
	private static StandardOpenOption[] op = { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE };
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static Date date = new Date();

	public static void writeToErrorLog(StackTraceElement[] error) {
		System.out.println(System.getProperty("user.dir") + "\\log.txt");
		BufferedWriter bw = null;
		File errorLog = new File(System.getProperty("user.dir") + "\\log.txt");

		try {
			bw = Files.newBufferedWriter(errorLog.toPath(), charset, op);
			bw.write(dateFormat.format(date));
			bw.newLine();
			for(StackTraceElement trace : error) {
				bw.write(trace.toString());
				bw.newLine();
			}
			bw.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
