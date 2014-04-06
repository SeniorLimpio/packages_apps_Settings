/*
 *  Copyright (C) 2013 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.android.settings.limpio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import java.util.List;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.R;
import com.android.settings.util.Helpers;
import com.android.internal.util.slim.DeviceUtils;

public class StatusBarFeatures extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "StatusBarFeatures";

    private static final String KEY_SMS_BREATH = "sms_breath";
    private static final String KEY_MISSED_CALL_BREATH = "missed_call_breath";
    private static final String KEY_VOICEMAIL_BREATH = "voicemail_breath";
    private static final String STATUS_BAR_CUSTOM_HEADER = "custom_status_bar_header";

    private CheckBoxPreference mStatusBarCustomHeader;
    private CheckBoxPreference mSMSBreath;
    private CheckBoxPreference mMissedCallBreath;
    private CheckBoxPreference mVoicemailBreath;
    private CheckBoxPreference mStatusBarNetworkActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.limpio_statusbar_features);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarCustomHeader = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_CUSTOM_HEADER);
        mStatusBarCustomHeader.setChecked(Settings.System.getInt(resolver,
            Settings.System.STATUS_BAR_CUSTOM_HEADER, 0) == 1);
        mStatusBarCustomHeader.setOnPreferenceChangeListener(this);

        mSMSBreath = (CheckBoxPreference) prefSet.findPreference(KEY_SMS_BREATH);
        mSMSBreath.setChecked((Settings.System.getInt(resolver, 
                      Settings.System.KEY_SMS_BREATH, 0) == 1));

        mMissedCallBreath = (CheckBoxPreference) prefSet.findPreference(KEY_MISSED_CALL_BREATH);
        mMissedCallBreath.setChecked((Settings.System.getInt(resolver, 
                      Settings.System.KEY_MISSED_CALL_BREATH, 0) == 1));

        mVoicemailBreath = (CheckBoxPreference) prefSet.findPreference(KEY_VOICEMAIL_BREATH);
        mVoicemailBreath.setChecked((Settings.System.getInt(resolver, 
                      Settings.System.KEY_VOICEMAIL_BREATH, 0) == 1));
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarCustomHeader) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                Settings.System.STATUS_BAR_CUSTOM_HEADER, value ? 1 : 0);
           Helpers.restartSystemUI();
        } else {
            return false;
        }
        return true;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;

        if (preference == mSMSBreath) {
            value = mSMSBreath.isChecked();
            Settings.System.putInt(resolver, 
                    Settings.System.KEY_SMS_BREATH, value ? 1 : 0);
            return true;
        } else if (preference == mMissedCallBreath) {
            value = mMissedCallBreath.isChecked();
            Settings.System.putInt(resolver,
                    Settings.System.KEY_MISSED_CALL_BREATH, value ? 1 : 0);
            return true;
        } else if (preference == mVoicemailBreath) {
            value = mVoicemailBreath.isChecked();
            Settings.System.putInt(resolver,
                    Settings.System.KEY_VOICEMAIL_BREATH, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
