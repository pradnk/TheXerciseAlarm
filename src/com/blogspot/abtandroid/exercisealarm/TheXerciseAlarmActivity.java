package com.blogspot.abtandroid.exercisealarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Hours;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.abtandroid.exercisealarm.socialnetwork.FacebookSocialNetwork;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class TheXerciseAlarmActivity extends Activity {

	private SharedPreferences sharedPreferences;
	private TextView topTitle;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		sharedPreferences = getSharedPreferences("TheXerciseAlarm", MODE_PRIVATE);
		// ******** Start of Action Bar configuration
		ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		actionbar.setHomeLogo(R.drawable.home);
		actionbar.addAction(createEditAction());
		actionbar.addAction(createSettingsAction());
		// ******** End of Action Bar configuration
		
		LinearLayout top = (LinearLayout)findViewById(R.id.top);
		top.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TheXerciseAlarmActivity.this, EditAlarm.class));
			}
		});
		
		topTitle = (TextView)top.findViewById(R.id.title_top);
		
		showTopTitle(topTitle);
		
		LinearLayout bottom = (LinearLayout)findViewById(R.id.bottom);
		
		TextView bottomTitle = (TextView)bottom.findViewById(R.id.title_bottom);
		int days = sharedPreferences.getInt("days", 1);
		bottomTitle.setText("Woohoo! You have used TheXerciseAlarm "+days+" times now. \nKeep going!");
		bottomTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FacebookSocialNetwork s = new FacebookSocialNetwork(TheXerciseAlarmActivity.this);
				s.postStatus("Used TheXerciseAlarm "
						+ sharedPreferences.getInt("days", 1)
						+ " times already and loving it! Makes sure I wake up and also motivates me to Xercise!! Check it out at GOOGLE PLAY (to be launched)");
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showTopTitle(topTitle);
	}
	
	private void showTopTitle(TextView view) {
		SimpleDateFormat displayFull = new SimpleDateFormat("dd/MM/yyyy @ hh:mm a");
		long alarm = sharedPreferences.getLong("alarm", new Date().getTime());
		
		Calendar cal_alarm = Calendar.getInstance();
		cal_alarm.setTimeInMillis(alarm);
		
		DateTime n = new DateTime();
		DateTime a = new DateTime(alarm);
		
		Hours h = Hours.hoursBetween(n, a);
		view.setText("TheXerciseAlarm set at "+displayFull.format(cal_alarm.getTime())+"\n\nXercise In "+h.getHours()+" hours");
	}

	@Override
	protected void onStart() {
		super.onStart();

		SharedPreferences sharedPreferences = getSharedPreferences(
				"TheXerciseAlarm", MODE_PRIVATE);
		long alarm = sharedPreferences.getLong("alarm", -1);

		if (alarm == -1) {
			startActivity(new Intent(this, EditAlarm.class));
			Toast.makeText(
					this,
					"Looks like this is the first time you are using TheXerciseAlarm. Please set the time",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindDrawables(findViewById(R.id.mainbg));
		System.gc();
	}

	private void unbindDrawables(View view) {
		try {
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}
				((ViewGroup) view).removeAllViews();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Constants.ALARM_ON) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private Action createEditAction() {
		IntentAction clickAction = new IntentAction(this, createEditIntent(),
				android.R.drawable.ic_menu_edit);

		return clickAction;
	}
	
	private Intent createEditIntent() {
		Intent intent = new Intent(this, EditAlarm.class);		
		return intent;

	}
	
	private Action createSettingsAction() {
		IntentAction clickAction = new IntentAction(this, createSettingsIntent(),
				android.R.drawable.ic_menu_preferences);

		return clickAction;
	}
	
	private Intent createSettingsIntent() {
		Intent intent = new Intent(this, SettingsActivity.class);		
		return intent;

	}
	
	@Override
	public void onConfigurationChanged(
			android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}