package org.opensandiego.webikesd.views.dashboard.tracking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import org.opensandiego.webikesd.R;

import static android.content.Context.NOTIFICATION_SERVICE;

final class TrackingNotification {

  // Prevent instantiations from clients
  private TrackingNotification() {

  }

  // Tracking notification constants
  static final int TRACKING_NOTIFICATION_ID = 1001;
  private static final String PACKAGE_NAME = TrackingNotification.class.getPackage().toString();
  private static final String TRACKING_NOTIFICATION_CHANNEL_ID = PACKAGE_NAME + ".tracking_channel";

  /**
   * Tracking Notification
   * Helper method to build a a simple foreground notification to notify user that the app
   * is current tracking the device location, and allows user to manipulate the tracking
   * service state
   *
   * @param context application context
   * @return a foreground notification that shows user
   */
  @NonNull
  static Notification build(@NonNull Context context) {
    NotificationManager notificationManager = (NotificationManager)
        context.getSystemService(NOTIFICATION_SERVICE);
    assert notificationManager != null;

    /* Implement NotificationChannel for Devices running Android O or later */
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      String notificationChannelDescription = context.getString(
          R.string.tracking_notification_description);

      NotificationChannel notificationChannel = new NotificationChannel(
          TRACKING_NOTIFICATION_CHANNEL_ID,
          notificationChannelDescription,
          NotificationManager.IMPORTANCE_HIGH
      );

      // Configure the notification channel.
      notificationChannel.setDescription(notificationChannelDescription);
      notificationChannel.enableLights(false);
      notificationChannel.setLightColor(ContextCompat.getColor(context, R.color.colorPrimary));
      notificationChannel.enableVibration(false);
      notificationManager.createNotificationChannel(notificationChannel);
    }

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
        TRACKING_NOTIFICATION_CHANNEL_ID);

    builder.setContentTitle(context.getString(R.string.app_name))
        .setContentText(context.getString(R.string.tracking_notification_content))
        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        .setAutoCancel(true)
        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
        .setOnlyAlertOnce(false);

    // If the build version is greater than JELLY_BEAN and lower than OREO,
    // set the notification's priority to PRIORITY_HIGH.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      builder.setPriority(NotificationCompat.PRIORITY_HIGH);
    }

    return builder.build();
  }
}
