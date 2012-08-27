package com.blogspot.abtandroid.exercisealarm;

import java.util.Date;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

public class ReceiveAlarm extends IntentService implements SensorEventListener {

	public ReceiveAlarm() {
		super("RECEIVEALARM");
	}

    private long lastUpdate = -1;
	private float last_x = 0, last_y = 0, last_z = 0;
	private final int TIME_INTERVAL = 10;
	private int counter = 0;
	private final int SHAKE_THRESHOLD = 1000;
	private final int SHAKE_COUNT = 5;
	private SensorManager sensorManager;
	private AlarmManager manager;
	
	private PendingIntent operation;
	private Vibrator vibrator;
	private Sensor mAccelerometer;
	private MediaPlayer player;
	
	private void addSensorsAndRegister() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	}

	private void deRegisterListener() {
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
			sensorManager = null;
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		int X = (int) sensorEvent.values[0];
		int Y = (int) sensorEvent.values[1];
		int Z = (int) sensorEvent.values[2];
		
		if(sensorEvent.sensor == mAccelerometer) {
			analyzeValuesAcc(X, Y, Z);
		}
	}
	
	private boolean analyzeValuesAcc(int x, int y, int z) {
		updateCordinates(x, y, z);
		return false;
	}
	
	private void updateCordinates(float x, float y, float z) {
		try {
			long curTime = System.currentTimeMillis();
			long diffTime = (curTime - lastUpdate);
			if (diffTime >= TIME_INTERVAL) {
				lastUpdate = curTime;

				float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
				if (speed >= SHAKE_THRESHOLD) {
					counter++;
					if(counter == SHAKE_COUNT) {
						deRegisterListener();
						stopAlarm();
						return;
					}
				}
				last_x = x;
				last_y = y;
				last_z = z;

			} else
				lastUpdate = System.currentTimeMillis();
		} catch (Exception e) {
		}
	}

	private void stopAlarm() {
		Constants.ALARM_ON = false;
		vibrator.cancel();
		try {
			player.stop();
			player.release();
		} catch(Exception e) {
			e.printStackTrace();
		}
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(101);

		SharedPreferences sharedPreferences = getSharedPreferences("TheXerciseAlarm", MODE_PRIVATE);
		long alarm = sharedPreferences.getLong("alarm", new Date().getTime());
		SetAlarm.setAlarm(this, alarm);
		int daysElapsed = sharedPreferences.getInt("days", 0) + 1;
		sharedPreferences.edit().putInt("days", daysElapsed).commit();
		
		//TODO: FACEBOOK
		
		manager.cancel(operation);		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if(intent != null) {
			operation = (PendingIntent)intent.getParcelableExtra("operation");
			try {
				startAlarm();
			} catch (Exception e) {
				e.printStackTrace();
			}
			addSensorsAndRegister();
		}
	}

	private void startAlarm() throws Exception {
		startActivity(new Intent(this, DisplayAlarm.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		Constants.ALARM_ON = true;
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100000);
		int randomIndex = (int)(Math.random() * (Constants.sounds.length - 1));
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0);
		player = MediaPlayer.create(this, Constants.sounds[randomIndex]);
		player.setScreenOnWhilePlaying(true);
		player.setLooping(true);
		player.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Toast.makeText(ReceiveAlarm.this, "MediaPlayer Error. Only vibration!", Toast.LENGTH_LONG).show();
				return true;
			}
		});
		try {
			player.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}