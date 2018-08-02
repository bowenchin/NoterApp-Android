package com.bowenchin.android.noter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.text.method.DigitsKeyListener;

/**
 * Created by bowenchin on 29/12/15.
 */
public class PreferencesFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // This static call will reset default values only on the first ever read
        PreferenceManager.setDefaultValues(getActivity(), R.xml.task_preferences, false);
        //Construct the preferences screen form XML config
        addPreferencesFromResource(R.xml.task_preferences);

        //Use the number keyboard when editing the time preference
        EditTextPreference timeDefault = (EditTextPreference)findPreference(getString(R.string.pref_default_time_from_now_key));
        timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance());

        findPreference(this.getString(R.string.title_instructions)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), IntroActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //Preferences.sync(getPreferenceManager(), key);
                if (key.equals(getActivity().getString(R.string.pref_theme))) {
                    getActivity().finish();
                    final Intent intent = getActivity().getIntent();
                    getActivity().startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
        super.onPause();
    }
}
