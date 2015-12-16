package org.cn.core.upgrade;

/**
 * Created by chenning on 15-12-15.
 */
public class SmartUpgrade {

    public int diff(String oldFile, String newFile, String patchFile) {
        return Bsdiff.bsdiff(oldFile, newFile, patchFile);
    }

    public int patch(String srcFile, String destFile, String patchFile) {
        return Bspatch.bspatch(srcFile, destFile, patchFile);
    }

    static {
        System.loadLibrary("smartupgrade");
    }
}
