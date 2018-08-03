package com.bowenchin.android.noter;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

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
                .setImage(R.drawable.ic_launcher_about)
                .setDescription("Noter is a free, simple, private note and todo app. Everything is located in a central location on a single list and stored privately on your smartphone or tablet. It is also designed with the latest Material Design components and guidelines.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Connect with us")
                .addWebsite("http://bowenchin.com/")
                .addTwitter("medyo80")
                .addPlayStore("com.bowenchin.android.noter")
                .addInstagram("thebowenchin")
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

}
