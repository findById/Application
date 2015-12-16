package org.cn.core.upgrade;

/**
 * Created by chenning on 15-12-15.
 */
public class Bspatch {

    public static native int bspatch(String srcFile, String destFile, String patchFile);

}
