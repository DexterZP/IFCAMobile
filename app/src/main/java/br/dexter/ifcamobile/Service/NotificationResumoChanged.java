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
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;

import br.dexter.ifcamobile.R;
import br.dexter.ifcamobile.Semana.ResumoSemanal;
import br.dexter.ifcamobile.SistemaLogin.SystemLogin;

public class NotificationResumoChanged extends BroadcastReceiver
{
    private static final String BaseAction = "br.dexter.ifcamobile";
    private DatabaseReference databaseReference;

    public NotificationResumoChanged() { }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
        if(GetCodigo(context) == null) {
            return;
        }

        databaseReference.child(GetCodigo(context)).child("Resumo Semanal").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(BaseAction, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

                        notificationChannel.setDescription("EDMT Channel");
                        notificationChannel.enableLights(true);
                        notificationChannel.setLightColor(context.getColor(R.color.azure));
                        notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                        notificationChannel.enableLights(true);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, BaseAction);
                    notificationBuilder.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Resumo Semanal")
                            .setColor(ContextCompat.getColor(context, R.color.azure))
                            .setContentText("O resumo semanal da sua turma foi atualizado")
                            .setAutoCancel(true);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent(context, ResumoSemanal.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.setContentIntent(pendingIntent);

                    notificationManager.notify(1, notificationBuilder.build());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, NotificationResumoChanged.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void startBaseAlarmManager(@NonNull Context context)
    {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationResumoChanged.class);
        intent.setAction(BaseAction);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(alarmMgr != null) {
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 2000, alarmIntent);
        }
    }

    public String GetCodigo(Context context)
    {
        try
        {
            FileInputStream fin = context.openFileInput(SystemLogin.nameCodTxt);
            StringBuilder sb = new StringBuilder();
            int size;

            while ((size = fin.read()) != -1)
            {
                sb.append((char) size);
            }

            if(sb.toString().contains(SystemLogin.cod_Informatica3)) {
                return SystemLogin.nameInformatica3;
            }
            else if(sb.toString().contains(SystemLogin.cod_Informatica2)) {
                return SystemLogin.nameInformatica2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Administracao1)) {
                return SystemLogin.nameAdministracao1;
            }
            else if(sb.toString().contains(SystemLogin.cod_Administracao2)) {
                return SystemLogin.nameAdministracao2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Administracao3)) {
                return SystemLogin.nameAdministracao3;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agroindustria1)) {
                return SystemLogin.nameAgroindustria1;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agroindustria2)) {
                return SystemLogin.nameAgroindustria2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agroindustria3)) {
                return SystemLogin.nameAgroindustria3;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agropecuaria1)) {
                return SystemLogin.nameAgropecuaria1;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agropecuaria2)) {
                return SystemLogin.nameAgropecuaria2;
            }
            else if(sb.toString().contains(SystemLogin.cod_Agropecuaria3)) {
                return SystemLogin.nameAgropecuaria3;
            }
            else return "Não encontrada";
        }
        catch (Exception error)
        {
            error.printStackTrace();
        }

        return null;
    }
}