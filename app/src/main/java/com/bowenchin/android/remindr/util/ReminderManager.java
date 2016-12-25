package com.bowenchin.android.remindr.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bowenchin.android.remindr.provider.TaskProvider;
import com.bowenchin.android.remindr.receiver.OnAlarmReceiver;

import java.util.Calendar;

/**
 * Created by bowenchin on 28/12/15.
 */
public class ReminderManager {
    private ReminderManager(){}

    public static void setReminder(Context context, long taskId, String title, Calendar when){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, OnAlarmReceiver.class);
        i.putExtra(TaskProvider.COLUMN_TASKID, taskId);
        i.putExtra(TaskProvider.COLUMN_TITLE, title);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0 , i, PendingIntent.FLAG_ONE_SHOT);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            alarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
        }
        else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
        }

    }
}
