package mx.udg.alumnos.miviaje.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import mx.udg.alumnos.miviaje.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}