/*
 * Copyright (C) 2013 Team AOSPAL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.liquid;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.preference.SeekBarPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.WindowManager;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockscreenNotifications extends SettingsPreferenceFragment
      implements Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCKSCREEN_NOTIFICATIONS = "lockscreen_notifications";
    private static final String KEY_POCKET_MODE = "pocket_mode";
    private static final String KEY_SHOW_ALWAYS = "show_always";
    private static final String KEY_HIDE_LOW_PRIORITY = "hide_low_priority";
    private static final String KEY_HIDE_NON_CLEARABLE = "hide_non_clearable";
    private static final String KEY_DISMISS_ALL = "dismiss_all";
    private static final String KEY_EXPANDED_VIEW = "expanded_view";
    private static final String KEY_FORCE_EXPANDED_VIEW = "force_expanded_view";
    private static final String KEY_WAKE_ON_NOTIFICATION = "wake_on_notification";
    private static final String KEY_NOTIFICATIONS_HEIGHT = "notifications_height";
    private static final String KEY_PRIVACY_MODE = "privacy_mode";
    private static final String KEY_OFFSET_TOP = "offset_top";
    private static final String KEY_CATEGORY_GENERAL = "category_general";
    private static final String KEY_EXCLUDED_APPS = "excluded_apps";
    private static final String KEY_NOTIFICATION_COLOR = "notification_color";
    private static final String KEY_DYNAMIC_WIDTH = "dynamic_width";

    private CheckBoxPreference mLockscreenNotifications;
    private CheckBoxPreference mPocketMode;
    private CheckBoxPreference mShowAlways;
    private CheckBoxPreference mWakeOnNotification;
    private CheckBoxPreference mHideLowPriority;
    private CheckBoxPreference mHideNonClearable;
    private CheckBoxPreference mDismissAll;
    private CheckBoxPreference mExpandedView;
    private CheckBoxPreference mForceExpandedView;
    private NumberPickerPreference mNotificationsHeight;
    private CheckBoxPreference mPrivacyMode;
    private SeekBarPreference mOffsetTop;
    private AppMultiSelectListPreference mExcludedAppsPref;
    private ColorPickerPreference mNotificationColor;
    private CheckBoxPreference mDynamicWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_notifications);
        PreferenceScreen prefs = getPreferenceScreen();
        final ContentResolver cr = getActivity().getContentResolver();

        mLockscreenNotifications = (CheckBoxPreference) prefs.findPreference(KEY_LOCKSCREEN_NOTIFICATIONS);
        mLockscreenNotifications.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS, 1) == 1);

        boolean hasProximitySensor = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY);

        if (hasProximitySensor) {
		    mPocketMode = (CheckBoxPreference) prefs.findPreference(KEY_POCKET_MODE);
		    mPocketMode.setChecked(Settings.System.getInt(cr,
		                Settings.System.LOCKSCREEN_NOTIFICATIONS_POCKET_MODE, 0) == 1);
		    mPocketMode.setEnabled(mLockscreenNotifications.isChecked());
		    disablePref();

		    mShowAlways = (CheckBoxPreference) prefs.findPreference(KEY_SHOW_ALWAYS);
		    mShowAlways.setChecked(Settings.System.getInt(cr,
		                Settings.System.LOCKSCREEN_NOTIFICATIONS_SHOW_ALWAYS, 0) == 1);
		    mShowAlways.setEnabled(mPocketMode.isChecked() && mPocketMode.isEnabled());
        }
        mWakeOnNotification = (CheckBoxPreference) prefs.findPreference(KEY_WAKE_ON_NOTIFICATION);
        mWakeOnNotification.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_WAKE_ON_NOTIFICATION, 0) == 1);
        mWakeOnNotification.setEnabled(mLockscreenNotifications.isChecked());

        mHideLowPriority = (CheckBoxPreference) prefs.findPreference(KEY_HIDE_LOW_PRIORITY);
        mHideLowPriority.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_LOW_PRIORITY, 0) == 1);
        mHideLowPriority.setEnabled(mLockscreenNotifications.isChecked());

        mHideNonClearable = (CheckBoxPreference) prefs.findPreference(KEY_HIDE_NON_CLEARABLE);
        mHideNonClearable.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_NON_CLEARABLE, 0) == 1);
        mHideNonClearable.setEnabled(mLockscreenNotifications.isChecked());

        mDismissAll = (CheckBoxPreference) prefs.findPreference(KEY_DISMISS_ALL);
        mDismissAll.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_DISMISS_ALL, 1) == 1);
        mDismissAll.setEnabled(!mHideNonClearable.isChecked() && mLockscreenNotifications.isChecked());

        mPrivacyMode = (CheckBoxPreference) prefs.findPreference(KEY_PRIVACY_MODE);
        mPrivacyMode.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_PRIVACY_MODE, 0) == 1);
        mPrivacyMode.setEnabled(mLockscreenNotifications.isChecked());

        mExpandedView = (CheckBoxPreference) prefs.findPreference(KEY_EXPANDED_VIEW);
        mExpandedView.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_EXPANDED_VIEW, 1) == 1);
        mExpandedView.setEnabled(mLockscreenNotifications.isChecked() && !mPrivacyMode.isChecked());

        mForceExpandedView = (CheckBoxPreference) prefs.findPreference(KEY_FORCE_EXPANDED_VIEW);
        mForceExpandedView.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_FORCE_EXPANDED_VIEW, 0) == 1);
        mForceExpandedView.setEnabled(mLockscreenNotifications.isChecked() && mExpandedView.isChecked()
                    && !mPrivacyMode.isChecked());

        mOffsetTop = (SeekBarPreference) prefs.findPreference(KEY_OFFSET_TOP);
        mOffsetTop.setProgress((int)(Settings.System.getFloat(cr,
                Settings.System.LOCKSCREEN_NOTIFICATIONS_OFFSET_TOP, 0.3f) * 100));
        mOffsetTop.setTitle(getResources().getText(R.string.offset_top) + " " + mOffsetTop.getProgress() + "%");
        mOffsetTop.setOnPreferenceChangeListener(this);
        mOffsetTop.setEnabled(mLockscreenNotifications.isChecked());

        mNotificationsHeight = (NumberPickerPreference) prefs.findPreference(KEY_NOTIFICATIONS_HEIGHT);
        mNotificationsHeight.setValue(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HEIGHT, 4));
        Point displaySize = new Point();

        ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(displaySize);
        int max = Math.round((float)displaySize.y * (1f - (mOffsetTop.getProgress() / 100f)) /
                (float)mContext.getResources().getDimensionPixelSize(R.dimen.notification_row_min_height));
        mNotificationsHeight.setMinValue(1);
        mNotificationsHeight.setMaxValue(max);
        mNotificationsHeight.setOnPreferenceChangeListener(this);
        mNotificationsHeight.setEnabled(mLockscreenNotifications.isChecked());

        mNotificationColor = (ColorPickerPreference) prefs.findPreference(KEY_NOTIFICATION_COLOR);
        mNotificationColor.setAlphaSliderEnabled(true);
        int color = Settings.System.getInt(cr,
                Settings.System.LOCKSCREEN_NOTIFICATIONS_COLOR, 0x55555555);
        String hexColor = String.format("#%08x", (0xffffffff & color));
        mNotificationColor.setSummary(hexColor);
        mNotificationColor.setDefaultValue(color);
        mNotificationColor.setNewPreviewColor(color);
        mNotificationColor.setOnPreferenceChangeListener(this);

        mExcludedAppsPref = (AppMultiSelectListPreference) findPreference(KEY_EXCLUDED_APPS);
        Set<String> excludedApps = getExcludedApps();
        if (excludedApps != null) mExcludedAppsPref.setValues(excludedApps);
        mExcludedAppsPref.setOnPreferenceChangeListener(this);

        mDynamicWidth = (CheckBoxPreference) prefs.findPreference(KEY_DYNAMIC_WIDTH);
        mDynamicWidth.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_DYNAMIC_WIDTH, 0) == 1);
        mDynamicWidth.setEnabled(mLockscreenNotifications.isChecked());
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ContentResolver cr = getActivity().getContentResolver();
        if (preference == mLockscreenNotifications) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS,
                    mLockscreenNotifications.isChecked() ? 1 : 0);
            if (mPocketMode != null) {
		        mPocketMode.setEnabled(mLockscreenNotifications.isChecked());
		        mShowAlways.setEnabled(mPocketMode.isChecked() && mPocketMode.isEnabled());
            }
            mWakeOnNotification.setEnabled(mLockscreenNotifications.isChecked());
            mHideLowPriority.setEnabled(mLockscreenNotifications.isChecked());
            mHideNonClearable.setEnabled(mLockscreenNotifications.isChecked());
            mDismissAll.setEnabled(!mHideNonClearable.isChecked() && mLockscreenNotifications.isChecked());
            mNotificationsHeight.setEnabled(mLockscreenNotifications.isChecked());
            mOffsetTop.setEnabled(mLockscreenNotifications.isChecked());
            mForceExpandedView.setEnabled(mLockscreenNotifications.isChecked() && mExpandedView.isChecked()
                        && !mPrivacyMode.isChecked());
            mExpandedView.setEnabled(mLockscreenNotifications.isChecked() && !mPrivacyMode.isChecked());
        } else if (preference == mPocketMode) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_POCKET_MODE,
                    mPocketMode.isChecked() ? 1 : 0);
            mShowAlways.setEnabled(mPocketMode.isChecked());
        } else if (preference == mShowAlways) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_SHOW_ALWAYS,
                    mShowAlways.isChecked() ? 1 : 0);
        } else if (preference == mWakeOnNotification) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_WAKE_ON_NOTIFICATION,
                    mWakeOnNotification.isChecked() ? 1 : 0);
        } else if (preference == mHideLowPriority) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_LOW_PRIORITY,
                    mHideLowPriority.isChecked() ? 1 : 0);
        } else if (preference == mHideNonClearable) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_HIDE_NON_CLEARABLE,
                    mHideNonClearable.isChecked() ? 1 : 0);
            mDismissAll.setEnabled(!mHideNonClearable.isChecked());
        } else if (preference == mDismissAll) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_DISMISS_ALL,
                    mDismissAll.isChecked() ? 1 : 0);
        } else if (preference == mExpandedView) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_EXPANDED_VIEW,
                    mExpandedView.isChecked() ? 1 : 0);
            mForceExpandedView.setEnabled(mExpandedView.isChecked());
        } else if (preference == mForceExpandedView) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_FORCE_EXPANDED_VIEW,
                    mForceExpandedView.isChecked() ? 1 : 0);
        } else if (preference == mPrivacyMode) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_PRIVACY_MODE,
                    mPrivacyMode.isChecked() ? 1 : 0);
            mForceExpandedView.setEnabled(mLockscreenNotifications.isChecked() && mExpandedView.isChecked()
                        && !mPrivacyMode.isChecked());
            mExpandedView.setEnabled(mLockscreenNotifications.isChecked() && !mPrivacyMode.isChecked());
        } else if (preference == mDynamicWidth) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_NOTIFICATIONS_DYNAMIC_WIDTH,
                    mDynamicWidth.isChecked() ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object value) {
        if (pref == mNotificationsHeight) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_HEIGHT, (Integer)value);
        } else if (pref == mOffsetTop) {
            Settings.System.putFloat(getContentResolver(), Settings.System.LOCKSCREEN_NOTIFICATIONS_OFFSET_TOP,
                    (Integer)value / 100f);
            mOffsetTop.setTitle(getResources().getText(R.string.offset_top) + " " + (Integer)value + "%");
            Point displaySize = new Point();
            ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(displaySize);
            int max = Math.round((float)displaySize.y * (1f - (mOffsetTop.getProgress() / 100f)) /
                    (float)mContext.getResources().getDimensionPixelSize(R.dimen.notification_row_min_height));
            mNotificationsHeight.setMaxValue(max);
        } else if (pref == mNotificationColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(value)));
            pref.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_NOTIFICATIONS_COLOR, intHex);
            return true;
        } else if (pref == mExcludedAppsPref) {
            storeExcludedApps((Set<String>) value);
            return true;
        } else {
            return false;
        }
        return true;
    }

    private Set<String> getExcludedApps() {
        String excluded = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_NOTIFICATIONS_EXCLUDED_APPS);
        if (TextUtils.isEmpty(excluded))
            return null;

        return new HashSet<String>(Arrays.asList(excluded.split("\\|")));
    }

    private void storeExcludedApps(Set<String> values) {
        StringBuilder builder = new StringBuilder();
        String delimiter = "";
        for (String value : values) {
            builder.append(delimiter);
            builder.append(value);
            delimiter = "|";
        }
        Settings.System.putString(getContentResolver(),
                Settings.System.LOCKSCREEN_NOTIFICATIONS_EXCLUDED_APPS, builder.toString());
    }

     private void disablePref() {
          ContentResolver resolver = getActivity().getContentResolver();
          boolean enabled = (Settings.System.getInt(resolver,
                  Settings.System.ENABLE_ACTIVE_DISPLAY, 0) == 1) ||
                  (Settings.System.getInt(resolver,
                  Settings.System.PEEK_STATE, 0) == 1);
        if (enabled) {
            Settings.System.putInt(resolver,
                Settings.System.LOCKSCREEN_NOTIFICATIONS_POCKET_MODE, 0);
            mPocketMode.setEnabled(false);
            mPocketMode.setSummary(R.string.pocket_mode_disabled_summary);
        }
    }
}
