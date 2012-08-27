package com.blogspot.abtandroid.exercisealarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	private SharedPreferences sharedPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("TheXerciseAlarm : Settings");
		addPreferencesFromResource(R.xml.settings);

		sharedPrefs = getSharedPreferences("Settings",
				MODE_PRIVATE);
		
		final CheckBoxPreference sync = (CheckBoxPreference) findPreference("share");
		sync.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				boolean value = (Boolean) newValue;
				sharedPrefs.edit().putBoolean("share", value).commit();
				if (value) {
					sync.setSummary("Share automatically everytime you use TheXersiseAlarm");
				} else {
					sync.setSummary("You decide when to share manually");
				}
				return true;
			}
		});

		final Preference share = (Preference) findPreference("brag");
		share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent newIntent = new Intent(Intent.ACTION_SEND);
				newIntent.setType("text/plain");
				newIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this cool new app TheXerciseAlarm!\nIt will wake you up and also motivate you to exercise! Check out at: ");
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(newIntent);
				return true;
			}
		});
	}
}