package com.bowenchin.android.noter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.bowenchin.android.noter.interfaces.OnEditTask;

public class TaskListActivity extends AppCompatActivity implements OnEditTask {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.replaceMenu(R.menu.menu_task_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Adding task...", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                editTask(0);
            }
        });

        //Check if app is launch on first time. If yes, display dialogue help box
        if (isFirstTime()) {
            //Onboarding
            Intent i = new Intent(this,IntroActivity.class);
            startActivity(i);
            // show dialog
            new AlertDialog.Builder(this).setTitle("Welcome to Noter").setMessage("Add a new to-do task by tapping on the \"+\" button to get started! \n \nLearn more by going to \"Settings\".").setNeutralButton("GOT IT", null).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.home) {
            //use onBackPressed() OR finish();
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(),PreferencesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    @Override
    public void editTask(long id){
        //When we are asked to edit a reminder, start the TaskEditActivity with the id of the task to edit.
        startActivity(new Intent(this, TaskEditActivity.class).putExtra(TaskEditActivity.EXTRA_TASKID,id));
    }
}