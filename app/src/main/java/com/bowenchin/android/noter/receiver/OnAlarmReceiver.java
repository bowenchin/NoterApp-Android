package com.bowenchin.android.noter.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bowenchin.android.noter.R;
import com.bowenchin.android.noter.TaskListActivity;
import com.bowenchin.android.noter.provider.TaskProvider;

/**
 * Created by bowenchin on 28/12/15.
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        //IMPORTANT: Do not do any asynchronous operations in BroadcastReceive.onReceive!

        NotificationManager mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //TODO: Clicking on notification does not pull up that task

        Intent taskEditIntent = new Intent(context, TaskListActivity.class);
        long taskId = intent.getLongExtra(TaskProvider.COLUMN_TASKID, -1);
        String title = intent.getStringExtra(TaskProvider.COLUMN_TITLE);
        taskEditIntent.putExtra(TaskProvider.COLUMN_TASKID, taskId);

        PendingIntent pi = PendingIntent.getActivity(context, 0 , taskEditIntent, PendingIntent.FLAG_ONE_SHOT);

        //Build the Notification object using Notification.Builder
        Notification noti = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(title)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setLights(0xffffffff, 300, 300)
                .build();

        //Send the notification
        mgr.notify((int)taskId, noti);
    }
}
