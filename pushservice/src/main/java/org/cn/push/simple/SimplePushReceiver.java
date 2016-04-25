package org.cn.push.simple;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class SimplePushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Simple Receiver");
        builder.setContentText(intent.getStringExtra("payload"));
        builder.setWhen(System.currentTimeMillis());
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        String id = System.currentTimeMillis() + "";
        id = id.substring(id.length() - id.length() / 2, id.length());

        manager.notify(Integer.valueOf(id), builder.build());
    }
}
