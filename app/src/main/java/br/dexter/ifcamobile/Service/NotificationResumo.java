package br.dexter.ifcamobile.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.Semana.ResumoSemanal;

public class NotificationResumo extends BroadcastReceiver
{
    public static final String BaseAction = "br.dexter.ifcamobile";

    public NotificationResumo() { }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            startBaseAlarmManager(context);
        }
        else if(BaseAction.equals(intent.getAction()))
        {
            StartYourService(context);
        }
    }

    private void StartYourService(@NonNull final Context context)
    {
        SharedPreferences sp = context.getSharedPreferences("AppStorage", 0);
        String verify = sp.getString("segundaFeira", null);
        String segunda = sp.getString("segundaFeira", "");
        String terca = sp.getString("tercaFeira", "");
        String quarta = sp.getString("quartaFeira", "");
        String quinta = sp.getString("quintaFeira", "");
        String sexta = sp.getString("sextaFeira", "");

        if(verify == null) {
            return;
        }

        if(segunda.contains("yep") && terca.contains("yep") && quarta.contains("yep") && quinta.contains("yep") && sexta.contains("yep")) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(BaseAction, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

                notificationChannel.setDescription("EDMT Channel");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(context.getColor(R.color.azure));
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 3, new Intent(context, ResumoSemanal.class), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, BaseAction);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Resumo Semanal")
                    .setColor(ContextCompat.getColor(context, R.color.azure))
                    .setContentText("Você ainda tem algumas atividades pendentes")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(3, notificationBuilder.build());
        }
    }

    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, NotificationResumo.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void startBaseAlarmManager(@NonNull Context context)
    {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationResumo.class);
        intent.setAction(BaseAction);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(alarmMgr != null) {
            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 60 * 1000 * 750, alarmIntent);
        }
    }
}