package org.cn.core.upgrade;

/**
 * Created by chenning on 15-12-15.
 */
public class Bsdiff {

    public static native int bsdiff(String oldFile, String newFile, String patchFile);

}
