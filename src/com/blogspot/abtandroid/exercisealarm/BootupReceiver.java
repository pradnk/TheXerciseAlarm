package com.blogspot.abtandroid.exercisealarm;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context c, Intent i) {
		System.out.println("*************************BOOTED HOORAY!!!!!! "+i.getAction());
		
		//TODO: Restore the alarmmanager timings back from SharedPrefs?
		SharedPreferences sharedPreferences = c.getSharedPreferences("TheXerciseAlarm", Context.MODE_PRIVATE);
		long alarm = sharedPreferences.getLong("alarm", new Date().getTime());
		SetAlarm.setAlarm(c, alarm);
		int daysElapsed = sharedPreferences.getInt("days", 0) + 1;
		sharedPreferences.edit().putInt("days", daysElapsed).commit();
	}
}