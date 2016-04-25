package org.cn.push;

public class PushConst {

    public static final String CMD_ACTION = "push.action";
    public static final String CMD_GET_CLIENTID = "push.client.id";

    public static final String SYS_VERSION = "_V1";
    public static final String SYS_TOPIC_HEADER = "/sys";

    public static final String VERSION = "_V1";

    public static final String SERVICE_INTENT = "org.cn.push.message.service.v1";

    public static final String SERVICE_START_ACTION = "action.service.start" + VERSION;
    public static final String SERVICE_RECONNECT_ACTION = "action.service.reconnect" + VERSION;
    public static final String SERVICE_HEARTBEAT_ACTION = "action.service.heartbeat" + VERSION;
    public static final String SERVICE_HEARTBEAT_TIMEOUT_ACTION = "action.service.heartbeat.timeout" + VERSION;

}
