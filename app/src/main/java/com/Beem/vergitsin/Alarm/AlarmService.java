package com.Beem.vergitsin.Alarm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.Beem.vergitsin.R;

public class AlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "alarm_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String adi = intent.getStringExtra("adi");
        String miktar = intent.getStringExtra("miktar");

        Toast.makeText(this, "AlarmService başladı. adi: " + adi + ", miktar: " + miktar, Toast.LENGTH_SHORT).show();

        mediaPlayer = MediaPlayer.create(this, R.raw.alarmsounds);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Intent activityIntent = new Intent(this, AlarmActivity.class);
        activityIntent.putExtra("adi", adi);
        activityIntent.putExtra("miktar", miktar);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Borç Hatırlatıcı")
                .setContentText("Borç miktarı: " + miktar+ "TL")
                .setSmallIcon(R.drawable.ic_alarm)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)  // Buraya ekledik
                .setAutoCancel(true)               // Bildirime tıklanınca bildirim kalkar
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AlarmDebug", "AlarmService durduruldu");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Bağlanmaya gerek yok
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Alarm Kanalı",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Borç alarm kanalı");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
