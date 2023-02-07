package com.example.littlefilerenamer.service;

import java.io.File;

public class DirStatisticService {

    public static String getDirStatistic(File dir) {
        return "Total files in camera directory: " + countFilesInDir(dir) + System.getProperty("line.separator")
                + "Files to rename: " + countFilesToRename(dir);
    }

    static int countFilesInDir(File dir) {
        File[] list = dir.listFiles();
        return list != null ? list.length : 0;
    }

    private static int countFilesToRename(File dir) {
        return FileRenameService.createFileListToRename(dir).size();
    }

}