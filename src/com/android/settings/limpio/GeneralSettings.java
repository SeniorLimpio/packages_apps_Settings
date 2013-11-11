package com.android.settings.limpio;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.util.Helpers;

public class GeneralSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String RESTART_SYSTEMUI = "restart_systemui";
    private static final String SHOW_CPU_INFO_KEY = "show_cpu_info";

    private Preference mRestartSystemUI;
    private CheckBoxPreference mShowCpuInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.general_settings);

        mRestartSystemUI = findPreference(RESTART_SYSTEMUI);

	mShowCpuInfo = (CheckBoxPreference) findPreference(SHOW_CPU_INFO_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void writeCpuInfoOptions() {
        boolean value = mShowCpuInfo.isChecked();
        Settings.Global.putInt(getActivity().getContentResolver(),
                Settings.Global.SHOW_CPU, value ? 1 : 0);
        Intent service = (new Intent())
                .setClassName("com.android.systemui", "com.android.systemui.CPUInfoService");
        if (value) {
            getActivity().startService(service);
        } else {
            getActivity().stopService(service);
        }
    }	

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mRestartSystemUI) {
            Helpers.restartSystemUI();
	} else if (preference == mShowCpuInfo) {
            writeCpuInfoOptions();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
