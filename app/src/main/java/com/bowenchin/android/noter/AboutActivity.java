package com.bowenchin.android.noter;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferences.applyTheme(this);
        super.onCreate(savedInstanceState);

        setDarkStatusIcon(true);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher_round)
                .setDescription("Noter is a free, simple, private note and todo app designed with the latest Material Design UI.\n\nEverything is located in a single list and stored locally on your smartphone or tablet.")
                .addItem(new Element().setTitle("Version 1.6"))
                .addGroup("Connect with us")
                .addWebsite("http://bowenchin.com/")
                .addPlayStore("com.bowenchin.android.noter")
                .addGitHub("bowenchin")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setGravity(Gravity.CENTER);
        return copyRightsElement;
    }

    public void setDarkStatusIcon(boolean bDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currentTheme = prefs.getString(getResources().getString(R.string.pref_theme), "Light");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = getWindow().getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(bDark && currentTheme.equals("Light")){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

}
