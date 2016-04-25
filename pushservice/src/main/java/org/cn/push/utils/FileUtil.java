package org.cn.push.utils;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtil {

    public static String readData(String fileName) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            fileName = getCachePath(fileName);
            fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = fis.read(buffer)) >= 0) {
                baos.write(buffer, 0, count);
            }
            return baos.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static void writeData(String fileName, String data) {
        FileOutputStream fos = null;
        try {
            fileName = getCachePath(fileName);
            fos = new FileOutputStream(fileName, true);
            fos.write(data.getBytes(Charset.forName("UTF-8")));
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory() && file.list() != null) {
            for (File f : file.listFiles()) {
                deleteFile(f.getPath());
            }
        }
    }

    private static String getCachePath(String fileName) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        dir = dir + File.separator + "mq_push";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }
}
