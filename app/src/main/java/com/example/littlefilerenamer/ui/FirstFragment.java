package com.example.littlefilerenamer.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.littlefilerenamer.R;
import com.example.littlefilerenamer.databinding.FragmentFirstBinding;
import com.example.littlefilerenamer.service.FileRenameService;
import com.example.littlefilerenamer.service.DirStatisticService;
import com.example.littlefilerenamer.service.RenamingStatisticService;
import com.example.littlefilerenamer.service.SettingsKeySupplier;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    TextView showCameraTextView;

    TextView showResultTextView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View fragmentFirstLayout = binding.getRoot();
        initTextViews(fragmentFirstLayout);
        return fragmentFirstLayout;
    }

    private void initTextViews(View fragmentFirstLayout) {
        showCameraTextView = fragmentFirstLayout.findViewById(R.id.textview_camera);
        showCameraTextView.setText(DirStatisticService.getDirStatistic(
                new File(getSharedPreferences().getString(getString(R.string.camera_dir_key), getString(R.string.camera_dir_default)))));
        showResultTextView = fragmentFirstLayout.findViewById(R.id.textview_result);
    }

    private SharedPreferences getSharedPreferences() {
        Context hostActivity = getActivity();
        return PreferenceManager.getDefaultSharedPreferences(hostActivity);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonStart.setOnClickListener(view1 -> renameFiles());
    }

    private void renameFiles() {
        SharedPreferences preferences = getSharedPreferences();
        boolean isCameraSwitch = preferences.getBoolean(getString(R.string.camera_switch_key), false);
        SettingsKeySupplier keySupplier = new SettingsKeySupplier(getActivity());
        showResultTextView.setText(
                isCameraSwitch ? RenamingStatisticService.getRenamingResults(preferences, keySupplier) : "Renaming is turned off");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}