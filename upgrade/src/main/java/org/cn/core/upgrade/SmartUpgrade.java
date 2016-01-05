package org.cn.core.upgrade;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by chenning on 15-12-15.
 */
public class SmartUpgrade {

    public String getAppSourceDir(Context ctx) {
        if (ctx == null) {
            return null;
        }
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_PERMISSIONS);
            Log.d("Smart", info.applicationInfo.sourceDir);
            Log.d("Smart", info.applicationInfo.loadLabel(pm) + "");
            return info.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int execBuildPatch(String oldFile, String newFile, String patchFile) {
        return Bsdiff.bsdiff(oldFile, newFile, patchFile);
    }

    public int execApplyPatch(String srcFile, String destFile, String patchFile) {
        return Bspatch.bspatch(srcFile, destFile, patchFile);
    }

    public void smart(Context ctx, String patch, OnSmartListener listener) {
        this.listener = listener;

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        final File file = new File(path, "smart_temp.apk");
        File source = new File(path, "smart_source.apk");

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(getAppSourceDir(ctx));
            fos = new FileOutputStream(source);
            IOUtil.copyStream(fis, fos);
            new Thread(new SmartRunnable(source.getPath(), file.getPath(), patch)).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(fis, fos);
        }

    }

    class SmartRunnable implements Runnable {
        private String source;
        private String dest;
        private String patch;

        public SmartRunnable(String source, String dest, String patch) {
            this.source = source;
            this.dest = dest;
            this.patch = patch;
        }

        @Override
        public void run() {
            try {
                long start = System.currentTimeMillis();
                int result = execApplyPatch(source, dest, patch);
                Log.d("Smart", "result:" + result + " used:" + (System.currentTimeMillis() - start) + "ms.");
                delete(source, patch);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onSmart(200, dest);
                        }
                    }
                });
            } catch (Throwable e) {
                if (listener != null) {
                    listener.onSmart(400, dest);
                }
            }
        }

        private void delete(String... paths) {
            if (paths != null && paths.length > 0) {
                for (String p : paths) {
                    try {
                        new File(p).delete();
                    } catch (Throwable e) {
                        // ignore
                    }
                }
            }
        }
    }

    static {
        System.loadLibrary("smartupgrade");
    }

    private OnSmartListener listener;

    public interface OnSmartListener {
        void onSmart(int statusCode, String path);
    }

}
