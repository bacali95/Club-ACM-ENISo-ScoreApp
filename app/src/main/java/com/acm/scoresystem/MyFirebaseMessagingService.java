package com.acm.scoresystem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.acm.scoresystem.Model.Notification;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;

import static com.acm.scoresystem.NotificationActivity.notifications;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("notifications");

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        assert remoteMessage.getNotification() != null;
        Notification notification = new Notification(remoteMessage.getNotification().getBody(), new Date(remoteMessage.getSentTime()).toGMTString());
        if (notifications == null) {
            notifications = new ArrayList<>();
        }
        notifications.add(notification);
        reference.setValue(notifications);
        builder.setContentTitle("Club ACM ENISo");
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentIntent(pendingIntent);
        builder.setVibrate(new long[]{500, 200, 100, 200, 100});
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }

}
