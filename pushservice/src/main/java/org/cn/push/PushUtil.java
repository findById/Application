package org.cn.push;

import org.cn.push.core.MessageType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PushUtil {

    private static ExecutorService mThreadPool;

    public static ExecutorService getThreadPool() {
        if (mThreadPool == null) {
            mThreadPool = Executors.newFixedThreadPool(10);
        }
        return mThreadPool;
    }

    public static String transformTopic(MessageType type, String domain) {
        StringBuffer sb = new StringBuffer();
        sb.append(domain);
        sb.append("/");
        sb.append(type.toString());
        return sb.toString();
    }

}
