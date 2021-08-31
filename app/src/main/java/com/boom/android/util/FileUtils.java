package com.boom.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
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

    public static List<File> getMp4Files(String realpath, List<File> files) {
        FilenameFilter filenameFilter = (dir, filename) -> {
            if (filename.endsWith(".mp4")) return true;
            return false;
        };

        File realFile = new File(realpath);
        if (realFile != null && realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles(filenameFilter);
            if(subfiles == null) {
                return files;
            }

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

    public String getFileSize(String fpath) {
        File path = new File(fpath);
        if (path.exists()) {
            DecimalFormat df = new DecimalFormat("#.00");
            String sizeStr = "";
            long size = 0;
            try {
                FileInputStream fis = new FileInputStream(path);
                size = fis.available();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "未知大小";
            } catch (IOException e) {
                e.printStackTrace();
                return "未知大小";
            }
            if (size < 1024) {
                sizeStr = size + "B";
            } else if (size < 1048576) {
                sizeStr = df.format(size / (double) 1024) + "KB";
            } else if (size < 1073741824) {
                sizeStr = df.format(size / (double) 1048576) + "MB";
            } else {
                sizeStr = df.format(size / (double) 1073741824) + "GB";
            }
            return sizeStr;
        } else {
            return "未知大小";
        }
    }
}
