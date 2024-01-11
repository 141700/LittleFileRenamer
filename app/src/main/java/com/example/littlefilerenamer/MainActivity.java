package com.example.littlefilerenamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import com.example.littlefilerenamer.databinding.ActivityMainBinding;
import com.example.littlefilerenamer.ui.preferences.PreferencesActivity;

public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration appBarConfiguration;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndAskFilesAccessPermission();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Switch cameraSwitch = findViewById(R.id.camera_switch);
        setSwitchPreference(R.id.camera_switch, getSharedPreferences().getBoolean(getString(R.string.camera_switch_default), true));
        Switch logSwitch = findViewById(R.id.log_switch);
        logSwitch.setEnabled(getSharedPreferences().getBoolean(getString(R.string.camera_switch_default), true));
        cameraSwitch.setOnCheckedChangeListener((arg0, isChecked) -> {
            setSwitchPreference(R.id.camera_switch, isChecked);
            updateLogSwitch(isChecked, logSwitch);
        });
        logSwitch.setOnCheckedChangeListener((arg0, isChecked) -> setSwitchPreference(R.id.log_switch, isChecked));
        binding.fab.setOnClickListener(view -> finishAndRemoveTask());
    }

    private void updateLogSwitch(boolean isChecked, Switch logSwitch) {
        if (!isChecked) {
            logSwitch.setChecked(false);
        }
        logSwitch.setEnabled(isChecked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.preferencesButton) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void checkAndAskFilesAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
        }
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setSwitchPreference(int id, boolean checked) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(getResources().getResourceEntryName(id), checked);
        editor.apply();
    }
}