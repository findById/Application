package org.cn.push.core;

public enum MessageType {

    PING("ping"),
    KEEP_ALIVE("KeepAlive"),
    RECONNECT("reconnect"),
    ACK("ack"),
    ALL("All"),
    SERVICE_DISCONNECT("service.disconnect"),
    SPECIFY("specify");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
