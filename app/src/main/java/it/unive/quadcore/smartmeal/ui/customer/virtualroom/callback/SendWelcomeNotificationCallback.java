package it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.CustomerVirtualRoomActivity;

public class SendWelcomeNotificationCallback implements Runnable {
    @NonNull
    private final Activity activity;

    public static final String CHANNEL_ID = "WELCOME_NOTIFICATION_CHANNEL";

    public SendWelcomeNotificationCallback(@NonNull Activity activity) {
        Objects.requireNonNull(activity);
        this.activity = activity;
    }

    @Override
    public void run() {
        // crea notifica
        String notificationTitlePrefix = activity.getString(R.string.welcome_notification_title_prefix);
        String localName = CustomerStorage.getLocalDescription().getName();


        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(activity, CustomerVirtualRoomActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(String.format("%s %s", notificationTitlePrefix, localName))
                .setContentText(activity.getString(R.string.welcome_notification_description))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(activity);

        // notificationId is a unique int for each notification that you must define
        final int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }
}
