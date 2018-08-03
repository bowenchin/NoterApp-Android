package com.bowenchin.android.noter;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by bowenchin on 29/12/15.
 */
public class PreferencesActivity extends AppCompatActivity {

    private AppCompatDelegate mDelegate;
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.applyTheme(this);
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        setDarkStatusIcon(true);

        //addPreferences(R.xml.task_preferences);
        //Preferences.sync(getPreferencesManager());
        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //Preferences.sync(getPreferenceManager(), key);
                if (key.equals(getString(R.string.pref_theme))) {
                    finish();
                    //change to launch into activity upon retheme
                    Intent intentToBeNewRoot = new Intent(PreferencesActivity.this, TaskListActivity.class);
                    ComponentName cn = intentToBeNewRoot.getComponent();
                    Intent intent = Intent.makeRestartActivityTask(cn);
                    startActivity(intent);
                }
            }

        };


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    public void setDarkStatusIcon(boolean bDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = getWindow().getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(bDark){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else{
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void onResume() {
        super.onResume();
        //getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    public void onPause() {
        //getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }
    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
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
}