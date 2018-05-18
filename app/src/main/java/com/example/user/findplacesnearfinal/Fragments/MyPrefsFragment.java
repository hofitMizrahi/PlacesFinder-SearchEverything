package com.example.user.findplacesnearfinal.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.example.user.findplacesnearfinal.R;

public class MyPrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_fragment);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

