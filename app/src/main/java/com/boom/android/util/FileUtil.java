package com.boom.android.util;

import com.boom.android.ui.videos.bean.VideoItemInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile != null && realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    continue;
//                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    public static List<File> getMp4Files(String realpath, List<File> files){
        FilenameFilter filenameFilter = (dir, filename) -> {
            if(filename.endsWith(".mp4")) return true;
            return false;
        };

        File realFile = new File(realpath);
        if (realFile != null && realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles(filenameFilter);
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    continue;
//                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;

    }

    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFiles(path, new ArrayList<>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, (file, newFile) -> {
                if (file.lastModified() < newFile.lastModified()) {
                    return -1;
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    return 1;
                }
            });
        }
        return list;
    }

    public static List<File> listMp4FileSortByModifyTime(String path) {
        List<File> list = getMp4Files(path, new ArrayList<>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, (file, newFile) -> {
                if (file.lastModified() < newFile.lastModified()) {
                    return 1;
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
            });
        }
        return list;
    }
}
