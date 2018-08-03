package com.bowenchin.android.noter.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.bowenchin.android.noter.R;
import com.bowenchin.android.noter.TaskEditActivity;
import com.bowenchin.android.noter.TaskListActivity;
import com.bowenchin.android.noter.provider.TaskProvider;

/**
 * Created by bowenchin on 28/12/15.
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        //IMPORTANT: Do not do any asynchronous operations in BroadcastReceive.onReceive!

        //TODO: Clicking on notification does not pull up that task
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent taskEditIntent = new Intent(context, TaskEditActivity.class);
        long taskId = intent.getLongExtra(TaskProvider.COLUMN_TASKID, -1);
        String title = intent.getStringExtra(TaskProvider.COLUMN_TITLE);
        taskEditIntent.putExtra(TaskProvider.COLUMN_TASKID, taskId);

        int requestID = (int) System.currentTimeMillis();
        taskEditIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, requestID , taskEditIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti;
        String channelId = "REMINDERS_CHANNEL";// The id of the channel.
        CharSequence channelName = "Noter Reminders";
        int importance = NotificationManager.IMPORTANCE_LOW;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);

            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.YELLOW);
            notificationChannel.enableVibration(true);
            nm.createNotificationChannel(notificationChannel);

            //Build the Notification object using Notification.Builder
            noti = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(title)
                    .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .build();
        } else {
            //Build the Notification object using Notification.Builder
            noti = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(title)
                    .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
        }


        //Send the notification
        nm.notify((int)taskId, noti);
    }
}
