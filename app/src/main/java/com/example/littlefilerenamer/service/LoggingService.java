package com.example.littlefilerenamer.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingService {

    private static final String FILENAME_DATE_TIME_FORMAT = "yyMMdd_HHmmss";

    private static final String LOG_FILE_EXTENSION = ".txt";

    public static String writeLogToFile(File logDir, String content) {
        File logFile = getLogFile(logDir);
        try (FileWriter fileWriter = new FileWriter(logFile, true)) {
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logFile.getName();
    }

    public static String getLogLineBeginning(int lineNumber){
        return String.format("%03d", lineNumber) + ": ";
    }

    public static File getLogFile(File dir) {
        File logFile = new File(dir, getNewLogFileName());
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logFile;
    }

    private static String getNewLogFileName() {
        return DateTimeFormatter.ofPattern(FILENAME_DATE_TIME_FORMAT).format(LocalDateTime.now()) + LOG_FILE_EXTENSION;
    }
}
