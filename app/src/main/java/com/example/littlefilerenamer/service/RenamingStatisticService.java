package com.example.littlefilerenamer.service;

import android.content.SharedPreferences;
import android.util.Pair;

import java.io.File;
import java.util.HashMap;

public class RenamingStatisticService {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String LOG_FILE_NAME_KEY = "log_file_name";

    private static final String FILES_TO_RENAME_KEY = "files_to_rename";

    private static final String FILES_RENAMED_KEY = "files_renamed";

    public static String getRenamingResults(SharedPreferences preferences, SettingsKeySupplier keySupplier) {
        File dir = new File(preferences.getString(keySupplier.getCameraDirKeyName(), ""));
        int totalFilesBeforeRenaming = DirStatisticService.countFilesInDir(dir);
        HashMap<String, Object> results = FileRenameService.renameFiles(preferences, keySupplier);
        StringBuilder reply = new StringBuilder("Files before renaming: " + totalFilesBeforeRenaming + LINE_SEPARATOR);
        reply.append("Files after renaming: ").append(DirStatisticService.countFilesInDir(dir)).append(LINE_SEPARATOR);
        reply.append("Files to rename: ").append(results.get(FILES_TO_RENAME_KEY)).append(LINE_SEPARATOR);
        reply.append("Files renamed: ").append(results.get(FILES_RENAMED_KEY)).append(LINE_SEPARATOR);
        String logFileName = (String) results.get(LOG_FILE_NAME_KEY);
        reply.append(logFileName != null && logFileName.length() > 0 ? "Renaming logged to: " + logFileName : "");
        return reply.toString();
    }

    static HashMap<String, Object> getRenamingStatistic(int listToRenameSize, Pair<Integer, String> result) {
        HashMap<String, Object> map = new HashMap<>(4, 1);
        map.put(FILES_TO_RENAME_KEY, listToRenameSize);
        map.put(FILES_RENAMED_KEY, result.first);
        map.put(LOG_FILE_NAME_KEY, result.second);
        return map;
    }

    static HashMap<String, Object> generateNothingToRenameResult() {
        HashMap<String, Object> map = new HashMap<>(2, 1);
        map.put(FILES_TO_RENAME_KEY, 0);
        map.put(FILES_RENAMED_KEY, 0);
        return map;
    }
}
