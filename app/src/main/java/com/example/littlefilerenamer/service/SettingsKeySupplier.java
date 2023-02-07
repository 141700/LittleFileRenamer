package com.example.littlefilerenamer.service;

import android.content.Context;
import com.example.littlefilerenamer.R;

public class SettingsKeySupplier {

    private final Context context;

    public SettingsKeySupplier(Context context) {
        this.context = context;
    }

    public String getCameraDirKeyName() {
        return this.context.getString((R.string.camera_dir_key));
    }

    public String getLogDirKeyName() {
        return this.context.getString((R.string.log_dir_key));
    }

    public String getCameraSwitchKeyName() {
        return this.context.getString((R.string.camera_switch_key));
    }

    public String getLogSwitchKeyName() {
        return this.context.getString((R.string.log_switch_key));
    }
}
