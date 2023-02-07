package com.example.littlefilerenamer.ui.preferences;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.example.littlefilerenamer.R;

public class PreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}