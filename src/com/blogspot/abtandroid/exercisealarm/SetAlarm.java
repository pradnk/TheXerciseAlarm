package com.blogspot.abtandroid.exercisealarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class SetAlarm {
	private static AlarmManager manager;
	private static PendingIntent operation;

	public static void setAlarm(Context context, long alarm) {
    	Date dat  = new Date();//initializes to now
	    Calendar cal_alarm = Calendar.getInstance();
	    Calendar cal_now = Calendar.getInstance();
	    cal_now.setTime(dat);
	    cal_alarm.setTimeInMillis(alarm);
	    if(cal_alarm.before(cal_now)) { //if its in the past increment
	        cal_alarm.add(Calendar.DATE, 1);
	    }
		setAlarmNow(context, cal_alarm);
	}

	public static void setAlarm(Context context, int hh, int mm) {
		Calendar cal_alarm = Calendar.getInstance();
		cal_alarm.set(Calendar.HOUR_OF_DAY, hh);
		cal_alarm.set(Calendar.MINUTE, mm);
		cal_alarm.set(Calendar.SECOND, 0);
		Date dat  = new Date();//initializes to now
	    Calendar cal_now = Calendar.getInstance();
	    cal_now.setTime(dat);
	    if(cal_alarm.before(cal_now)) { //if its in the past increment
	        cal_alarm.add(Calendar.DATE, 1);
	    }
	    setAlarmNow(context, cal_alarm);
	}
	
	private static void setAlarmNow(Context context, 
			Calendar cal_alarm) {
		manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy @ hh:mm a");
		Toast.makeText(context, "Alarm set at "+f.format(cal_alarm.getTime())+"", Toast.LENGTH_LONG).show();
		System.out.println(cal_alarm.getTimeInMillis());
		Intent alarmIntent = new Intent(context, ReceiveAlarm.class).putExtra("operation", operation);
		operation = PendingIntent.getService(context, 100, alarmIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		manager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), operation);
		
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				R.drawable.alarm_notify, "TheXerciseAlarm at: "
						+ f.format(cal_alarm.getTime()), cal_alarm.getTimeInMillis());
		notification.flags = Notification.FLAG_NO_CLEAR;
		CharSequence contentTitle = "TheXerciseAlarm @ "+f.format(cal_alarm.getTime());
		CharSequence contentText = "TheXerciseAlarm @ "+f.format(cal_alarm.getTime());
		Intent notificationIntent = new Intent(context, TheXerciseAlarmActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notificationManager.notify(101, notification);
		
		//Store chosen hh:mm by user into SharedPreferences.
		//ALSO use to restore alarm when device restarts canceling all alarms in alarmmanager.
		SharedPreferences sharedPreferences = context.getSharedPreferences("TheXerciseAlarm", Context.MODE_PRIVATE);
		sharedPreferences.edit().putLong("alarm", cal_alarm.getTimeInMillis()).commit();
	}
}
