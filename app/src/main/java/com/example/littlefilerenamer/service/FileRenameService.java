package com.example.littlefilerenamer.service;

import android.content.SharedPreferences;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FileRenameService {

    private static final int LEFT_CROP_INDEX = 6;

    private static final int RIGHT_CROP_INDEX = 19;

    private static final int FILE_INITIAL_CHECK_INDEX = 6;

    private static final String[] INITIALS_TO_RENAME = new String[]{"PXL_20", "VID_20", "IMG_20"};

    static HashMap<String, Object> renameFiles(SharedPreferences preferences, SettingsKeySupplier keySupplier) {
        if (preferences.getBoolean(keySupplier.getCameraSwitchKeyName(), false)) {
            File dir = new File(preferences.getString(keySupplier.getCameraDirKeyName(), ""));
            boolean isLogging = preferences.getBoolean(keySupplier.getLogSwitchKeyName(), false);
            File logDir = isLogging ? new File(preferences.getString(keySupplier.getLogDirKeyName(), "")) : null;
            return renameFilesInDir(dir, logDir);
        }
        return RenamingStatisticService.generateNothingToRenameResult();
    }

    private static HashMap<String, Object> renameFilesInDir(File dir, File logDir) {
        List<File> listToRename = createFileListToRename(dir);
        int listToRenameSize = listToRename.size();
        if (listToRenameSize > 0) {
            Pair<Integer, String> result = proceedRenaming(listToRename, logDir);
            return RenamingStatisticService.getRenamingStatistic(listToRenameSize, result);
        }
        return RenamingStatisticService.generateNothingToRenameResult();
    }

    static List<File> createFileListToRename(File dir) {
        File[] list = dir.listFiles();
        if (list != null && list.length > 0) {
            return Arrays.stream(list)
                    .filter(FileRenameService::isNeedToRename)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private static Pair<Integer, String> proceedRenaming(List<File> list, File logDir) {
        boolean isLogging = logDir != null;
        AtomicInteger lineNumber = new AtomicInteger(1);
        StringBuilder log = new StringBuilder();
        int successfulRenamings = (int) list.stream()
                .map(file -> isLogging ? renameFileWithLogging(file, lineNumber.getAndIncrement()) : renameFile(file))
                .peek(pair -> log.append(pair.second))
                .filter(pair -> pair.first)
                .count();
        String logFileName = "";
        if (isLogging) {
            logFileName = LoggingService.writeLogToFile(logDir, log.toString());
        }
        return new Pair<>(successfulRenamings, logFileName);
    }

    private static Pair<Boolean, String> renameFile(File file) {
        boolean result = file.renameTo(generateNewFileNameWithSuffix(file));
        return new Pair<>(result, "");
    }

    private static Pair<Boolean, String> renameFileWithLogging(File file, int lineNumber) {
        File newFile = generateNewFileNameWithSuffix(file);
        boolean isResult = file.renameTo(newFile);
        StringBuilder logLine = new StringBuilder(LoggingService.getLogLineBeginning(lineNumber));
        logLine.append(isResult ? file.getName() + " successfully renamed to " + newFile.getName()
                : "Failed to rename file " + file.getName() + " to " + newFile.getName());
        logLine.append(System.getProperty("line.separator"));
        return new Pair<>(isResult, logLine.toString());
    }

    private static File generateNewFileNameWithSuffix(File file) {
        int i = 0;
        File newFile;
        do {
            newFile = generateNewFileWithSuffix(file, i);
            i++;
        } while (newFile.exists());
        return newFile;
    }

    private static File generateNewFileWithSuffix(File file, int i) {
        String fileName = String.format("%1$s" + "%2$s" + ".%3$s",
                file.getName().substring(LEFT_CROP_INDEX, RIGHT_CROP_INDEX),
                i == 0 ? "" : "-" + i,
                getFileExtension(file));
        return new File(file.getParentFile(), fileName);
    }

    private static boolean isNeedToRename(File file) {
        String fileNameInitial = file.getName().substring(0, FILE_INITIAL_CHECK_INDEX);
        return Arrays.stream(INITIALS_TO_RENAME).anyMatch(fileNameInitial::equalsIgnoreCase);
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (i > p) {
            return fileName.substring(i + 1);
        }
        return "";
    }
}