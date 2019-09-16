package com.xiashengming.plugin.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CopyApkUtils {
    /**
     * 把Assets里面得文件复制到 /data/data/files 目录下
     *
     */
    public static File copyApk2InternelFiles(Context context, String sourceName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        File apk = null;
        try {
            is = am.open(sourceName);
            apk = context.getFileStreamPath(sourceName);
            fos = new FileOutputStream(apk);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(is);
            close(fos);
        }
        return apk;
    }

    private static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignore
        }
    }
}
