package com.android.settings.limpio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface; 
import android.content.Intent;
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
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.android.settings.util.Helpers;

import java.util.Date;
import java.util.List;

public class RecentsPanelSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "RecentsPanelSettings";

    private static final String CUSTOM_RECENT_MODE = "custom_recent_mode";
    private static final String CLEAR_RECENTS_BUTTON = "clear_recents_button";
    private static final String RAM_BAR_MODE = "ram_bar_mode";
    private static final String RAM_BAR_COLOR_APP_MEM = "ram_bar_color_app_mem";
    private static final String RAM_BAR_COLOR_CACHE_MEM = "ram_bar_color_cache_mem";
    private static final String RAM_BAR_COLOR_TOTAL_MEM = "ram_bar_color_total_mem";
    private static final String CIRCLE_MEM_BUTTON = "circle_mem_button";

    private static final int MENU_RESET = Menu.FIRST;

    static final int DEFAULT_MEM_COLOR = 0xff8d8d8d;
    static final int DEFAULT_CACHE_COLOR = 0xff00aa00;
    static final int DEFAULT_ACTIVE_APPS_COLOR = 0xff33b5e5;

    private CheckBoxPreference mRecentsCustom;
    private ListPreference mClearAllButton;
    private ListPreference mRamBarMode;
    private ColorPickerPreference mRamBarAppMemColor;
    private ColorPickerPreference mRamBarCacheMemColor;
    private ColorPickerPreference mRamBarTotalMemColor;
    private ListPreference mCircleMemButton;
    private ColorPickerPreference mRecentsColor;
    private ContentResolver mContentResolver;
    private Context mContext;  

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int intColor;
        String hexColor;

        addPreferencesFromResource(R.xml.recents_panel_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        boolean enableRecentsCustom = Settings.System.getBoolean(getContentResolver(),
                                      Settings.System.CUSTOM_RECENT_TOGGLE, false);
        mRecentsCustom = (CheckBoxPreference) findPreference(CUSTOM_RECENT_MODE);
        mRecentsCustom.setChecked(enableRecentsCustom);
        mRecentsCustom.setOnPreferenceChangeListener(this);

        // clear recents position
        mClearAllButton = (ListPreference) findPreference(CLEAR_RECENTS_BUTTON);
        int clearStatus = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.CLEAR_RECENTS_BUTTON, 4);
        mClearAllButton.setValue(String.valueOf(clearStatus));
        mClearAllButton.setSummary(mClearAllButton.getEntry());
        mClearAllButton.setOnPreferenceChangeListener(this);

        mCircleMemButton = (ListPreference) findPreference(CIRCLE_MEM_BUTTON);
        int circleStatus = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.CIRCLE_MEM_BUTTON, 0);
        mCircleMemButton.setValue(String.valueOf(clearStatus));
        mCircleMemButton.setSummary(mCircleMemButton.getEntry());
        mCircleMemButton.setOnPreferenceChangeListener(this);

        mRamBarMode = (ListPreference) prefSet.findPreference(RAM_BAR_MODE);
        int ramBarMode = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.RECENTS_RAM_BAR_MODE, 0);
        mRamBarMode.setValue(String.valueOf(ramBarMode));
        mRamBarMode.setSummary(mRamBarMode.getEntry());
        mRamBarMode.setOnPreferenceChangeListener(this);

        mRamBarAppMemColor = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_APP_MEM);
        mRamBarAppMemColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, DEFAULT_ACTIVE_APPS_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRamBarAppMemColor.setSummary(hexColor);
        mRamBarAppMemColor.setNewPreviewColor(intColor);

        mRamBarCacheMemColor = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_CACHE_MEM);
        mRamBarCacheMemColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, DEFAULT_CACHE_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRamBarCacheMemColor.setSummary(hexColor);
        mRamBarCacheMemColor.setNewPreviewColor(intColor);

        mRamBarTotalMemColor = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_TOTAL_MEM);
        mRamBarTotalMemColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_RAM_BAR_MEM_COLOR, DEFAULT_MEM_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRamBarTotalMemColor.setSummary(hexColor);
        mRamBarTotalMemColor.setNewPreviewColor(intColor);

        mRecentsColor = (ColorPickerPreference) findPreference("recents_panel_color");
        mRecentsColor.setOnPreferenceChangeListener(this);

        updateRecentsOptions();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.ram_bar_button_reset)
                .setIcon(R.drawable.ic_settings_backup) // use the backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default: 
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.ram_bar_reset);
        alertDialog.setMessage(R.string.ram_bar_reset_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ramBarColorReset();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    } 

    public boolean onPreferenceChange(Preference preference, Object objValue) {
	boolean result = false;

        	if (preference == mRecentsCustom) { // Enable||disbale Slim Recent
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.CUSTOM_RECENT_TOGGLE,
                    ((Boolean) objValue) ? true : false);
            updateRecentsOptions();
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mRecentsColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_PANEL_COLOR, intHex);
            Helpers.restartSystemUI();
            return true;
	} else if (preference == mClearAllButton) {
            int value = Integer.valueOf((String) objValue);
            int index = mClearAllButton.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.CLEAR_RECENTS_BUTTON, value);
            mClearAllButton.setSummary(mClearAllButton.getEntries()[index]);
            return true;
        } else if (preference == mCircleMemButton) {
            int value = Integer.valueOf((String) objValue);
            int index = mCircleMemButton.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.CIRCLE_MEM_BUTTON, value);
            mCircleMemButton.setSummary(mCircleMemButton.getEntries()[index]);
            return true;
        } else if (preference == mRamBarMode) {
            int ramBarMode = Integer.valueOf((String) objValue);
            int index = mRamBarMode.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.RECENTS_RAM_BAR_MODE, ramBarMode);
            mRamBarMode.setSummary(mRamBarMode.getEntries()[index]);
            updateRecentsOptions();
            return true;
        } else if (preference == mRamBarAppMemColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, intHex);
            return true;
        } else if (preference == mRamBarCacheMemColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, intHex);
            return true;
        } else if (preference == mRamBarTotalMemColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_RAM_BAR_MEM_COLOR, intHex);
            return true;
        }
        return false;
    }

    private void ramBarColorReset() {
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, DEFAULT_ACTIVE_APPS_COLOR);
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, DEFAULT_CACHE_COLOR);
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_RAM_BAR_MEM_COLOR, DEFAULT_MEM_COLOR);

        mRamBarAppMemColor.setNewPreviewColor(DEFAULT_ACTIVE_APPS_COLOR);
        mRamBarCacheMemColor.setNewPreviewColor(DEFAULT_CACHE_COLOR);
        mRamBarTotalMemColor.setNewPreviewColor(DEFAULT_MEM_COLOR);
        String hexColor = String.format("#%08x", (0xffffffff & DEFAULT_ACTIVE_APPS_COLOR));
        mRamBarAppMemColor.setSummary(hexColor);
        hexColor = String.format("#%08x", (0xffffffff & DEFAULT_ACTIVE_APPS_COLOR));
        mRamBarCacheMemColor.setSummary(hexColor);
        hexColor = String.format("#%08x", (0xffffffff & DEFAULT_MEM_COLOR));
        mRamBarTotalMemColor.setSummary(hexColor);
    }


    private void updateRecentsOptions() {
        int ramBarMode = Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.RECENTS_RAM_BAR_MODE, 3);
        boolean recentsStyle = Settings.System.getBoolean(getActivity().getContentResolver(),
               Settings.System.CUSTOM_RECENT_TOGGLE, false);
        if (recentsStyle) {
            mRamBarMode.setEnabled(false);
            mRamBarAppMemColor.setEnabled(false);
            mRamBarCacheMemColor.setEnabled(false);
            mRamBarTotalMemColor.setEnabled(false);
        } else {
            mRamBarMode.setEnabled(true);
            if (ramBarMode == 0) {
                mRamBarAppMemColor.setEnabled(false);
                mRamBarCacheMemColor.setEnabled(false);
                mRamBarTotalMemColor.setEnabled(false);
            } else if (ramBarMode == 1) {
                mRamBarAppMemColor.setEnabled(true);
                mRamBarCacheMemColor.setEnabled(false);
                mRamBarTotalMemColor.setEnabled(false);
            } else if (ramBarMode == 2) {
                mRamBarAppMemColor.setEnabled(true);
                mRamBarCacheMemColor.setEnabled(true);
                mRamBarTotalMemColor.setEnabled(false);
            } else {
                mRamBarAppMemColor.setEnabled(true);
                mRamBarCacheMemColor.setEnabled(true);
                mRamBarTotalMemColor.setEnabled(true);
            }
        }
    }
}