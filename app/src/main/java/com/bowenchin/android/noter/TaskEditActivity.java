package com.bowenchin.android.noter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bowenchin.android.noter.interfaces.OnEditFinished;
import com.bowenchin.android.noter.provider.TaskProvider;

public class TaskEditActivity extends AppCompatActivity implements OnEditFinished {
    public static final String EXTRA_TASKID = "taskId";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Preferences.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        long id = getIntent().getLongExtra(TaskProvider.COLUMN_TASKID,0L);
        Fragment fragment = TaskEditFragment.newInstance(id);

        String fragmentTag = TaskEditFragment.DEFAULT_FRAGMENT_TAG;

        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().add(R.id.container,fragment,fragmentTag).commit();
    }

    //Called when the user finishes editing a task
    @Override
    public void finishedEditingTask(){
        //When the user dismisses the editor, call finish to destroy this activity
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.empty_menu, menu);
        return true;
    }

}
