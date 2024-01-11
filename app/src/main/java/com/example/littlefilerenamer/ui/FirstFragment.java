package com.example.littlefilerenamer.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.littlefilerenamer.R;
import com.example.littlefilerenamer.databinding.FragmentFirstBinding;
import com.example.littlefilerenamer.service.DirStatisticService;
import com.example.littlefilerenamer.service.RenamingStatisticService;
import com.example.littlefilerenamer.service.SettingsKeySupplier;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Boolean.getBoolean;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    TextView showCameraTextView;

    TextView showCountdownTextView;

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
        showCountdownTextView = fragmentFirstLayout.findViewById(R.id.textview_countdown);
        showResultTextView = fragmentFirstLayout.findViewById(R.id.textview_result);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean isAutostart = getSharedPreferences().getBoolean(getString(R.string.autostart_key), getBoolean(getString(R.string.autostart_default)));
        if (isAutostart) {
            binding.buttonStart.setText(getString(R.string.autostart_button));
            binding.buttonStart.setOnClickListener(view1 -> stopCountdown());
            proceedAutoStart();
        } else {
            binding.buttonStart.setOnClickListener(view1 -> renameFiles());
        }
    }

    private void proceedAutoStart() {
        executorService.execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            int countdownTime = Integer.parseInt(getString(R.string.seconds_to_autostart));
            for (int i = countdownTime; i > 0; --i) {
                int finalI = i;
                Runnable showCountdownText = () -> {
                    showCountdownTextView.setText(String.format(Locale.getDefault(), "%d %s", finalI, getString(R.string.countdown_message)));
                };
                mainHandler.post(showCountdownText);
                try {
                    Thread.sleep(1000);
                    --countdownTime;
                } catch (InterruptedException e) {
                    break;
                }
            }
            if (countdownTime == 0) {
                showCountdownTextView.setText(getString(R.string.renaming_started));
                Runnable runRenaming = this::renameFiles;
                mainHandler.post(runRenaming);
            }
        });
    }

    public void stopCountdown() {
        executorService.shutdownNow();
    }

    private void renameFiles() {
        SharedPreferences preferences = getSharedPreferences();
        boolean isCameraSwitch = preferences.getBoolean(getString(R.string.camera_switch_key), false);
        SettingsKeySupplier keySupplier = new SettingsKeySupplier(getActivity());
        showResultTextView.setText(
                isCameraSwitch ? RenamingStatisticService.getRenamingResults(preferences, keySupplier) : getString(R.string.renaming_is_off));
        boolean isAutostart = getSharedPreferences().getBoolean(getString(R.string.autostart_key), getBoolean(getString(R.string.autostart_default)));
        if (isAutostart) {
            showCountdownTextView.setText(getString(R.string.closing_app));
            waitAndCloseApp();
        }
    }

    private void waitAndCloseApp() {
        executorService.execute(() -> {
            int autocloseTime = Integer.parseInt(getString(R.string.seconds_to_autoclose));
            try {
                Thread.sleep(autocloseTime * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.getActivity().finishAndRemoveTask();
        });
    }

    private SharedPreferences getSharedPreferences() {
        Context hostActivity = getActivity();
        return PreferenceManager.getDefaultSharedPreferences(hostActivity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopCountdown();
        binding = null;
    }
}