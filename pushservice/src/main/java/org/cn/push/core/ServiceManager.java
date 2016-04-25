package org.cn.push.core;

public class ServiceManager {
    public static final String TAG = "ServiceManager";

    private static ServiceManager instance;

    private ServiceManager() {

    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }
}
