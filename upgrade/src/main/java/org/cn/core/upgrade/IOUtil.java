package org.cn.core.upgrade;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable ac : closeables) {
                if (ac != null) {
                    try {
                        ac.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
    }

    public static void copyStream(InputStream is, OutputStream os) {
        try {
            byte[] buffer = new byte[1024 * 5];
            int len = -1;
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(is, os);
        }
    }

    public static String asString(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024 * 5];
            int len = -1;
            while ((len = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(is, baos);
        }
        return null;
    }

}
