package com.blogspot.abtandroid.exercisealarm;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.blogspot.abtandroid.exercisealarm.socialnetwork.FacebookSocialNetwork;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class DisplayAlarm extends Activity {

	private RelativeLayout share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displayalarm);
		LinearLayout bg = (LinearLayout)findViewById(R.id.bg);
		int random = (int)(Math.random() * (Constants.inspiration.length - 1)); 
		bg.setBackgroundResource(Constants.inspiration[random]);
		
		share = (RelativeLayout)findViewById(R.id.share);
		final SharedPreferences sharedPrefs = getSharedPreferences("Settings",
				MODE_PRIVATE);
		if(!sharedPrefs.getBoolean("share", false)) {
			share.setVisibility(View.VISIBLE);
			ImageView shareImg = (ImageView)findViewById(R.id.shareImg);
			shareImg.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					shareOnFacebook();
				}
			});
		} else {
			//Share automatically.
			shareOnFacebook();
		}
	}
	
	@Override
    protected void onDestroy() {
	    super.onDestroy();
	
	    unbindDrawables(findViewById(R.id.bg));
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
    	if(Constants.ALARM_ON) {
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

	private void shareOnFacebook() {
		FacebookSocialNetwork facebook = new FacebookSocialNetwork(DisplayAlarm.this);
		SimpleDateFormat displayFull = new SimpleDateFormat("hh:mm a");
		Date date = new Date();
		facebook.postStatus("TheXerciseAlarm promptly woke me up at "+displayFull.format(date)+" and helped me Xercise! Awesome! Coming soon on Google Play!");
	}
}