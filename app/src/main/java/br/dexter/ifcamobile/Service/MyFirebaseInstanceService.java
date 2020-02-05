package br.dexter.ifcamobile.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import br.dexter.ifcamobile.R;

public class MyFirebaseInstanceService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().isEmpty() && remoteMessage.getNotification() != null)
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        else
         showNotification(remoteMessage.getData());
    }

    private void showNotification(@NonNull Map<String, String> data)
    {
        String title = data.get("title");
        String body = data.get("body");

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "br.dexter.ifcamobile";

        if(notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("EDMT Channel");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(getColor(R.color.azure));
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setColor(ContextCompat.getColor(this, R.color.azure))
                    .setContentText(body)
                    .setAutoCancel(true);

            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }
    }

    private void showNotification(String title, String body)
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "br.dexter.ifcamobile";

        if(notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("EDMT Channel");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(getColor(R.color.azure));
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setColor(ContextCompat.getColor(this, R.color.azure))
                    .setContentText(body)
                    .setAutoCancel(true);

            notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        }
    }
}
