package com.Beem.vergitsin;

import androidx.core.app.NotificationCompat;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


public class MyFirebaseMessagingServices extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private static final String CHANNEL_BORC_ISTEGI = "borc_istegi_channel";
    private static final String CHANNEL_VERILEN_BORC = "verilen_borc_channel";
    private static final String CHANNEL_ALINAN_BORC = "alinan_borc_channel";
    private static final String CHANNEL_HATIRLATMA = "hatirlatma_channel";
    private static final String CHANNEL_HATA = "hata";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("servis basladı");
        Log.d(TAG, "Mesaj alındı: " + remoteMessage.getData());

        String title = null;
        String body = null;
        String channelId = CHANNEL_HATIRLATMA; // Default kanal

        // notification kısmı varsa kullan
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // Eğer data varsa ve title/body null ise data içinden al
        if ((title == null || body == null) && !remoteMessage.getData().isEmpty()) {
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
        }

        if (title == null || body == null) {
            Log.w(TAG, "Bildirim için title veya body yok");
            return;
        }

        // Gelen mesaj tipine göre kanal seçimi (title üzerinden örnek kontrol)
        if (title.contains("Borç İsteği")) {
            channelId = CHANNEL_BORC_ISTEGI;
        } else if (title.contains("Verilen Borç")) {
            channelId = CHANNEL_VERILEN_BORC;
        } else if (title.contains("Alınan Borç")) {
            channelId = CHANNEL_ALINAN_BORC;
        } else if (title.contains("Hatırlatma") || title.contains("Borcunu Hatırlatma")) {
            channelId = CHANNEL_HATIRLATMA;
        }
        else{
            System.out.println("Girilemedi");
            channelId = CHANNEL_HATA;
        }

        showNotification(title, body, channelId);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);

            NotificationChannel borcIstegiChannel = new NotificationChannel(
                    CHANNEL_BORC_ISTEGI,
                    "Borç İsteği Bildirimleri",
                    NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(borcIstegiChannel);

            NotificationChannel verilenBorcChannel = new NotificationChannel(
                    CHANNEL_VERILEN_BORC,
                    "Verilen Borç Bildirimleri",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(verilenBorcChannel);

            NotificationChannel alinanBorcChannel = new NotificationChannel(
                    CHANNEL_ALINAN_BORC,
                    "Alınan Borç Bildirimleri",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(alinanBorcChannel);

            NotificationChannel hatirlatmaChannel = new NotificationChannel(
                    CHANNEL_HATIRLATMA,
                    "Hatırlatma Bildirimleri",
                    NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(hatirlatmaChannel);

            NotificationChannel hata = new NotificationChannel(
                    CHANNEL_HATA,
                    "Hata Bildirimleri",
                    NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(hata);
        }
    }

    private void showNotification(String title, String body, String channelId) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_bildirim)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Yeni FCM token: " + token);
        // Token'ı backend'e gönderme kodunu buraya ekle
        if(MainActivity.kullanicistatic!=null && MainActivity.kullanicistatic.getKullaniciId()!=null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(MainActivity.kullanicistatic.getKullaniciId())
                    .update("fcmToken", token);
        }
    }
}

