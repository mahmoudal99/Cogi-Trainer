package onipractice.mahmoud.com.fitnessapp.CloudMessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import onipractice.mahmoud.com.fitnessapp.R;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String clickAction = remoteMessage.getNotification().getClickAction();
        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();
        String senderId = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification_title)
                    .setContentText(notification_message);

        Intent resultPendingIntent = new Intent(clickAction);
        resultPendingIntent.putExtra("user_id", senderId);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                  this, 0, resultPendingIntent, PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(pendingIntent);

        int notificationId = (int) System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());

    }
}
