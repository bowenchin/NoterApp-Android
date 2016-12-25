package com.bowenchin.android.remindr;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.Toast;

import com.bowenchin.android.remindr.interfaces.OnEditFinished;
import com.bowenchin.android.remindr.provider.TaskProvider;
import com.bowenchin.android.remindr.util.ReminderManager;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by bowenchin on 27/12/15.
 */
public class TaskEditFragment extends Fragment implements OnDateSetListener,OnTimeSetListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final String DEFAULT_FRAGMENT_TAG = "taskEditFragment";
    static final String TASK_ID = "taskId";
    static final String TASK_DATE_AND_TIME = "taskDateAndTime";
    public static final String DATE_PICKER = "datePicker";


    //Views
    View rootView;
    EditText titleText;
    EditText notesText;
    TextView dateButton;
    TextView timeButton;
    ImageView exitButton;

    long taskId;
    Calendar taskDateAndTime;

    public static TaskEditFragment newInstance(long id){
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle args = new Bundle();
        args.putLong(TaskEditActivity.EXTRA_TASKID,id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null){
            taskId = arguments.getLong(TaskEditActivity.EXTRA_TASKID,0L);
        }

        if(savedInstanceState != null){
            taskId = savedInstanceState.getLong(TASK_ID);
            taskDateAndTime = (Calendar)savedInstanceState.getSerializable(TASK_DATE_AND_TIME);
        }

        //If no previous date, use "now"
        if(taskDateAndTime == null){
            taskDateAndTime = Calendar.getInstance();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //This field may have changed while our activity was running, make sure we save it to outState
        //restore later in onCreate
        outState.putLong(TASK_ID, taskId);
        outState.putSerializable(TASK_DATE_AND_TIME, taskDateAndTime);
    }

    //Call this method whenever task date and time has changed
    private void updateDateAndTimeButtons(){
        //Set the time button text
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        String timeForButton = timeFormat.format(taskDateAndTime.getTime());
        timeButton.setText(timeForButton);

        //Set the date button text
        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateForButton = dateFormat.format(taskDateAndTime.getTime());
        dateButton.setText(dateForButton);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_task_edit,container,false);

        rootView = v.getRootView();
        titleText = (EditText)v.findViewById(R.id.title);
        notesText = (EditText)v.findViewById(R.id.notes);
        dateButton = (TextView)v.findViewById(R.id.task_date);
        timeButton = (TextView)v.findViewById(R.id.task_time);
        exitButton = (ImageView)v.findViewById(R.id.exit);

        //Configure exit button (TEMP)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            exitButton.setVisibility(View.VISIBLE);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), TaskListActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
            exitButton.setVisibility(View.GONE);


        //Configure FAB
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saving task...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String tle = titleText.getText().toString();

                if(TextUtils.isEmpty(tle)) {
                    titleText.setError("Task is empty, add item before saving...");
                    return;
                }
                else {
                    save();
                    ((OnEditFinished) getActivity()).finishedEditingTask();
                }
            }
        });

        updateDateAndTimeButtons();

        //Tell the date and time buttons what to do when clicked.
        dateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    showDatePicker();
                }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        if(taskId == 0){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey,null);
            String defaultTime = prefs.getString(defaultTimeKey,null);

            if(defaultTitle != null){
                titleText.setText(defaultTitle);
            }

            if(defaultTime != null && defaultTime.length() > 0){
                taskDateAndTime.add(Calendar.MINUTE, Integer.parseInt(defaultTime));
            }

            updateDateAndTimeButtons();
        }
        else {
            //Fire off a background loader to retrieve the data from database
            getLoaderManager().initLoader(0,null,this);
        }

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        Uri taskUri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, taskId);
        return new CursorLoader(getActivity(),taskUri,null,null,null,null);
    }

    //This method is called when the loader has finished loading its data
    public void onLoadFinished(Loader<Cursor> loader, Cursor task){
        if(task.getCount()==0){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((OnEditFinished)getActivity()).finishedEditingTask();
                }
            });
            return;
        }
        titleText.setText(task.getString(task.getColumnIndex(TaskProvider.COLUMN_TITLE)));
        notesText.setText(task.getString(task.getColumnIndexOrThrow(TaskProvider.COLUMN_NOTES)));
        Long dateInMillis = task.getLong(task.getColumnIndexOrThrow(TaskProvider.COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        taskDateAndTime.setTime(date);

        updateDateAndTimeButtons();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0){
        //nothign to reset for this fragment
    }

    //A helper method to show our DatePicker
    private void showDatePicker(){
        //Create a fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(taskDateAndTime);
        newFragment.show(ft, "datePicker");
    }

    private void showTimePicker(){
        //Create a fragment transaction
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        TimePickerDialogFragment fragment = TimePickerDialogFragment.newInstance(taskDateAndTime);
        fragment.show(ft,"timePicker");
    }

    //This is the method that our DatePicker dialog will call when the user picks a date.
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
        taskDateAndTime.set(Calendar.YEAR, year);
        taskDateAndTime.set(Calendar.MONTH, monthOfYear);
        taskDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateAndTimeButtons();
    }

    //This is the method that our TimePicker dialog will call when the user picks a time
    @Override
    public void onTimeSet(TimePicker view, int hour, int minute){
        taskDateAndTime.set(Calendar.HOUR_OF_DAY,hour);
        taskDateAndTime.set(Calendar.MINUTE,minute);
        updateDateAndTimeButtons();
    }

    private void save(){
        //Put all values the user entered into a ContentValues object
        String title = titleText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(TaskProvider.COLUMN_TITLE, title);
        values.put(TaskProvider.COLUMN_NOTES, notesText.getText().toString());
        values.put(TaskProvider.COLUMN_DATE_TIME, taskDateAndTime.getTimeInMillis());

        //taskId==0 when we create a new task,
        //otherwise it's the id of the task being edited
        if(taskId == 0){
            //Create the new task and set taskId to the id of the new task.
            Uri itemUri = getActivity().getContentResolver().insert(TaskProvider.CONTENT_URI,values);
            taskId = ContentUris.parseId(itemUri);
        }
        else{
            //Update the existing task
            Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, taskId);
            int count = getActivity().getContentResolver().update(uri,values,null,null);

            //if somehow we didn't edit exactly one task, throw an error
            if(count != 1)
                throw new IllegalStateException("Unable to update " + taskId);
        }
        Toast.makeText(getActivity(),getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();

        //Create a reminder for this task
        ReminderManager.setReminder(getActivity(), taskId, title, taskDateAndTime);
    }
}
