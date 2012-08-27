package com.blogspot.abtandroid.exercisealarm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class EditAlarm extends Activity {
	protected int hh;
	protected int mm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.editalarm);
		Button setAlarm = (Button)findViewById(R.id.setAlarm);
		final TimePicker timePicker= (TimePicker)findViewById(R.id.time);
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				hh = hourOfDay;
				mm = minute;
				System.out.println(hourOfDay+" "+minute);
			}
		});
		setAlarm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.out.println(hh+" "+mm);
				SetAlarm.setAlarm(EditAlarm.this, hh, mm);
				finish();
			}
		});
		
	}
}