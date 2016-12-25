package com.bowenchin.android.remindr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.bowenchin.android.remindr.provider.TaskProvider;
import com.bowenchin.android.remindr.util.ReminderManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by bowenchin on 28/12/15.
 */
public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Cursor cursor = context.getContentResolver().query(TaskProvider.CONTENT_URI, null, null,
                null,null);

        //If db is empty, don't do anything
        if(cursor == null)
            return;

        try{
            cursor.moveToFirst();
            int taskIdColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
            int dateTimeColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_DATE_TIME);
            int titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);

            while(!cursor.isAfterLast()){
                long taskId = cursor.getLong(taskIdColumnIndex);
                long dateTime = cursor.getLong(dateTimeColumnIndex);
                String title = cursor.getString(titleColumnIndex);

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(dateTime));

                ReminderManager.setReminder(context, taskId, title, cal);

                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
    }
}
