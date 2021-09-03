package com.boom.android.util.cache;

import com.boom.android.BoomApplication;

import java.io.File;

public class AndroidPrivateFileUtil {

    public static File getPrivateDir(String dirName) {
        File file = new File(BoomApplication.getInstance().getApplicationContext().getFilesDir(), dirName);
        file.setReadable(true, true);
        return file;
    }

}
