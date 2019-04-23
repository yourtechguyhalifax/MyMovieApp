package com.example.android.MyMovieApp.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.example.android.MyMovieApp.R;

import java.util.Iterator;
import java.util.Map;


public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preferences);

        //initilise summary here
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String currentValue = sharedPreferences.getString(getString(R.string.sort_order_key),getString(R.string.most_popular_value));

        Iterator iter = sharedPreferences.getAll().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry)iter.next();
            // Check the value here
            if(pair.getValue().equals(currentValue)){
                //get key
                printSummary(sharedPreferences,pair.getKey().toString());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       printSummary(sharedPreferences,key);
    }

    private void printSummary(SharedPreferences sharedPreferences, String key){
        Preference  preference = findPreference(key);

        if(preference == null){
            return;
        }

        if (preference instanceof ListPreference) {
            // List Preference
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());

        }
    }
}
