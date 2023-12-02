package com.msh.pushnotification;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyAndroidFirebaseInstanceIdService extends FirebaseMessagingService
{
    NotificationManager manager;
    NotificationCompat.Builder builder;
    String CHANNEL_ID;
    CharSequence name;
    int importance;

    @Override
    public void onNewToken(String token)
    {
        super.onNewToken(token);

        User.getInstance().setValue("userToken", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        User.getInstance().setContext(App.getAppContext());

        Map<String, String> notification = remoteMessage.getData();
        Log.d("pushNotif", "notification.getbody: " + notification.get("body"));

        try
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);

            CHANNEL_ID = getString(R.string.firebase_channel_id);
            name = getString(R.string.default_notification_channel_id);
            importance = NotificationManager.IMPORTANCE_HIGH;

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                mChannel.enableLights(true);
                mChannel.setLightColor(Color.YELLOW);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[] {100, 200, 300});

                manager.createNotificationChannel(mChannel);
            }

            PendingIntent PI;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
                PI = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
            else
                PI = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.icon)
                    .setTicker(notification.get("title") + "\n" + notification.get("body"))
                    .setContentTitle(notification.get("title"))
                    .setContentText(notification.get("body"))
                    .setContentIntent(PI)
                    .setChannelId(CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            User.getInstance().setValue("message", notification.get("title") + "\n" + notification.get("body"));

            builder.setDefaults(Notification.DEFAULT_SOUND);
            manager.notify(0, builder.build());
        }
        catch (Exception ex)
        {
            Log.d("pushNotif", "exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
