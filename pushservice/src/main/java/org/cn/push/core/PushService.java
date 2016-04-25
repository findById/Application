package org.cn.push.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttAdvancedCallback;
import com.ibm.mqtt.MqttBrokerUnavailableException;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttNotConnectedException;
import com.ibm.mqtt.MqttPersistenceException;
import com.ibm.mqtt.MqttSimpleCallback;

import org.cn.push.Log;
import org.cn.push.PushConst;
import org.cn.push.PushUtil;
import org.cn.push.utils.FileUtil;

import java.nio.charset.Charset;
import java.util.UUID;

public class PushService extends Service {
    public static final String TAG = "PushService";

    private static final int CONNECTED = 1;
    private static final int DISCONNECTED = 0;
    private static final int REGISTERED = 3;
    private static final int REGISTER_FAILED = 2;

    private boolean isPrepared;
    private boolean isStarted;
    private boolean isStopped;
    private boolean isReconnected;

    private long waitTime;
    private int retryCount = 0;

    private String host = "192.168.99.119";
    private int port = 1883;

    private String mClientId;
    private IMqttClient mClient;

    public static final String SHARED_PREFERENCE_NAME = "push";

    private static final int QUALITY_OF_SERVICE_LEVEL = 1;
    // We don't need to remember any state between the connections, so we use a clean start.
    private static final boolean CLEAN_START = true;
    // Let's set the internal keep alive for MQTT to 15 mins. I haven't tested this value much. It could probably be increased.
    private static short KEEP_ALIVE_SECONDS = 60 * 15;

    private NetworkConnectivityReceiver mNetworkConnectivityReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Create service.");
        generateClientId();
        init(host, port);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onPrepareStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isStarted) {
            disconnect();
        }
        super.onDestroy();
        System.exit(0);
    }

    private void onPrepareStart(Intent intent, final int startId) {
        this.isPrepared = true;
        if (intent == null) {
            intent = new Intent(this, PushService.class);
        }

        if (PushConst.SERVICE_INTENT.equals(intent.getAction())) {
            if (intent.hasExtra(PushConst.SERVICE_RECONNECT_ACTION)) {
                Log.d(TAG, "reconnect is running.");
                disconnect();
                init();
                platformReConnect();
                return;
            }
            if (intent.hasExtra(PushConst.SERVICE_HEARTBEAT_ACTION)) {
                Log.d(TAG, "Heartbeat receiver is running.");
                PushUtil.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        sendHeartBeat();
                    }
                });
                return;
            }
            if (intent.hasExtra(PushConst.SERVICE_HEARTBEAT_TIMEOUT_ACTION)) {
                Log.d(TAG, "heartbeat reconnect is running.");
                PushUtil.getThreadPool().execute(new ReConnectRunnable());
                return;
            }
            return;
        }

        Log.d(TAG, "prepare start.");
        if (!isConnected()) {
            connect(intent, startId + "");
        }

    }

    // start timer
    private void setAlarm(String request, int requestCode, long keepAlive) {
        Intent intent = new Intent(PushConst.SERVICE_INTENT);
        intent.putExtra(request, true);
        PendingIntent pi = PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + keepAlive, pi);
    }

    // stop timer
    private void cancelAlarm(String request, int requestCode) {
        Intent intent = new Intent(PushConst.SERVICE_INTENT);
        intent.putExtra(request, true);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT));
    }

    protected void init() {
        init(host, port);
    }

    private void init(String host, int port) {
        try {
            // Create connection spec
            String spec = "tcp://" + host + "@" + port;
            // Create the client and connect
            mClient = MqttClient.createMqttClient(spec, null);
            // register this client app has being able to receive messages
            mClient.registerSimpleHandler(new SimpleListener());
            // mClient.registerAdvancedHandler(new AdvancedListener());
        } catch (MqttException e) {
            mClient = null;
            Log.e(TAG, "create client failed.", e);
        }
    }

    private void platformConnect(Intent intent, String id) {
        try {
            mClient.connect(mClientId, CLEAN_START, KEEP_ALIVE_SECONDS);

            String[] topics = new String[4];
            topics[3] = SHARED_PREFERENCE_NAME;
            topics[0] = SHARED_PREFERENCE_NAME + "/" + mClientId;
            topics[1] = SHARED_PREFERENCE_NAME + "/" + MessageType.ALL.getValue();
            topics[2] = SHARED_PREFERENCE_NAME + "/" + MessageType.KEEP_ALIVE.getValue();

            subscribe(topics, new int[]{0, 0, 0, 0});

            //sendMessage(MessageType.KEEP_ALIVE, SHARED_PREFERENCE_NAME, "{}");
            isStarted = true;
        } catch (MqttBrokerUnavailableException e) {
            Log.e(TAG, "send message failed --> broker unavailable.", e);
        } catch (MqttNotConnectedException e) {
            Log.e(TAG, "send message failed --> connection is broken.", e);
        } catch (MqttPersistenceException e) {
            Log.e(TAG, "send message failed --> persistence exception.", e);
        } catch (MqttException e) {
            Log.e(TAG, "send message failed.", e);
        } finally {
            if (!isConnected()) {
                Log.e(TAG, "wait 1000ms.");
                setAlarm(PushConst.SERVICE_RECONNECT_ACTION, 2, 1000);
            }
        }
    }

    private void platformReConnect() {
        try {
            mClient.connect(SHARED_PREFERENCE_NAME + "/" + mClientId, CLEAN_START, KEEP_ALIVE_SECONDS);

            String[] topics = new String[4];
            topics[3] = SHARED_PREFERENCE_NAME;
            topics[0] = SHARED_PREFERENCE_NAME + "/" + mClientId;
            topics[1] = SHARED_PREFERENCE_NAME + "/" + MessageType.ALL.getValue();
            topics[2] = SHARED_PREFERENCE_NAME + "/" + MessageType.KEEP_ALIVE.getValue();

            subscribe(topics, new int[]{0, 0, 0, 0});

        } catch (MqttBrokerUnavailableException e) {
            Log.e(TAG, "send message failed --> broker unavailable.", e);
        } catch (MqttNotConnectedException e) {
            Log.e(TAG, "send message failed --> connection is broken.", e);
        } catch (MqttPersistenceException e) {
            Log.e(TAG, "send message failed --> persistence exception.", e);
        } catch (MqttException e) {
            Log.e(TAG, "send message failed.", e);
        } finally {
            check();
        }
    }

    private void check() {
        if (!isConnected()) {
            setAlarm(PushConst.SERVICE_RECONNECT_ACTION, 2, 30000);
        }
    }

    private void tryReconnect() {
        isReconnected = true;
        if (retryCount == 0) {
            waitTime = 1000 * 30;
            Log.d(TAG, "first time to reconnect and sleep " + waitTime + "ms.");
        }
        if (retryCount > 5) {
            waitTime = 300000;
        }
        if (retryCount > 5) {
            waitTime = 60000 * retryCount;
        }

        Log.d(TAG, "now wait " + waitTime + "ms.");
        setAlarm(PushConst.SERVICE_RECONNECT_ACTION, 2, waitTime);
    }

    protected synchronized boolean isConnected() {
        return mClient != null && mClient.isConnected();
    }

    protected void sendServiceMessage(String message) {
        Intent intent = new Intent();

    }

    protected void sendHeartBeat() {
        if (isConnected()) {
            return;
        }
        Log.d(TAG, "send heat beat message to server.");
        sendMessage(MessageType.PING, null, "{}");
    }

    private synchronized void sendMessage(MessageType type, String domain, String message) {
        Log.d(TAG, "send topic: " + type.toString() + " domain:" + domain + " message:" + message);
        if (isConnected()) {
            String topic = PushUtil.transformTopic(type, domain);
            publish(topic, message);
        } else {

        }
    }

    private void connect(Intent intent, String id) {
        Log.d(TAG, "client is connect to host:" + host + ",port:" + port);
        PushUtil.getThreadPool().execute(new ConnectRunnable(intent, id));
    }

    protected synchronized void disconnect() {
        try {
            if (mClient != null && mClient.isConnected()) {
                Log.d(TAG, "disconnect from server");
                mClient.disconnect();
            }
        } catch (Throwable e) {
            Log.e(TAG, "disconnect failed.", e);
        } finally {
            mClient = null;
            isStarted = false;
            isPrepared = false;
        }
    }

    protected synchronized void publish(String topicName, String message) {
        if (mClient != null && mClient.isConnected()) {
            try {
                mClient.publish(topicName, message.getBytes(Charset.forName("UTF-8")), QUALITY_OF_SERVICE_LEVEL, false);
            } catch (MqttNotConnectedException e) {
                Log.e(TAG, "send message failed --> connection is broken", e);
            } catch (MqttPersistenceException e) {
                Log.e(TAG, "send message failed --> persistence exception", e);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "send message failed --> IllegalArgumentException", e);
            } catch (MqttException e) {
                Log.e(TAG, "send message failed.", e);
            }
        }
    }

    protected synchronized void subscribe(String[] topics, int[] qos) throws MqttException {
        if (mClient != null && mClient.isConnected()) {
            Log.d(TAG, "subscribe:" + topics);
            mClient.subscribe(topics, qos);
        }
    }

    protected synchronized void unsubscribe(String[] topics) throws MqttException {
        if (mClient != null && mClient.isConnected()) {
            Log.d(TAG, "unsubscribe:" + topics.toString());
            mClient.unsubscribe(topics);
        }
    }

    class ConnectRunnable implements Runnable {

        private Intent intent;
        private String startId;

        public ConnectRunnable(Intent intent, String startId) {
            this.intent = intent;
            this.startId = startId;
        }

        @Override
        public void run() {
            Log.d(TAG, "start to connect the server.");
            platformConnect(intent, startId);
        }
    }

    class ReConnectRunnable implements Runnable {

        @Override
        public void run() {
            Log.d(TAG, "try to connect.");
            tryReconnect();
        }
    }

    private void generateClientId() {
        mClientId = FileUtil.readData("push.data");
        if (mClientId == null || mClientId.length() <= 0) {
            // long timestamp = System.currentTimeMillis();
            // String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            // mClientId = androidId;
            mClientId = UUID.randomUUID().toString().replaceAll("-", "");
            if (mClientId.length() > 16) {
                mClientId = mClientId.substring(0, 16);
            }
            Log.d(TAG, "client id: " + mClientId);
            FileUtil.writeData("push.data", mClientId);
        }
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                Log.d(TAG, "Network is available");
                return true;
            }
        } catch (Throwable ignored) {
        }
        Log.d(TAG, "Network is unavailable");
        return false;
    }

    private void doConnectionLost() {
        isReconnected = false;
        cancelAlarm(PushConst.SERVICE_HEARTBEAT_ACTION, 1);
        cancelAlarm(PushConst.SERVICE_HEARTBEAT_TIMEOUT_ACTION, 0);
        disconnect();

    }

    class SimpleListener implements MqttSimpleCallback {

        @Override
        public void connectionLost() throws Exception {
            if (!isNetworkAvailable()) {
                Log.d(TAG, "network is not available now, service will wait.");
                disconnect();
                return;
            }
            if (isReconnected) {
                return;
            }
            if (isConnected()) {
                return;
            }

            doConnectionLost();
            Log.d(TAG, "connection is lost and try to reconnect.");
            tryReconnect();
        }

        @Override
        public void publishArrived(String topicName, byte[] payload, int qos, boolean retained) throws Exception {
            String result = new String(payload);
            Log.d(TAG, "topic: " + topicName + ", payload: " + result + ", qos: " + qos + ", retained: " + retained);

            Intent intent = new Intent("org.cn.push." + getPackageName());
            intent.putExtra("payload", result);

            getApplicationContext().sendBroadcast(intent);
        }
    }

    class AdvancedListener implements MqttAdvancedCallback {

        @Override
        public void published(int i) {
        }

        @Override
        public void subscribed(int i, byte[] bytes) {
        }

        @Override
        public void unsubscribed(int i) {
        }

        @Override
        public void connectionLost() throws Exception {
        }

        @Override
        public void publishArrived(String topicName, byte[] payload, int qos, boolean retained) throws Exception {
        }
    }

    class NetworkConnectivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (isNetworkAvailable()) {

            }

        }
    }

}
