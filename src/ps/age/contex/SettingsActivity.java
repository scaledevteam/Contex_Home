package ps.age.contex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ps.age.util.DBManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	EditText 	 startTime;
	EditText 	 endTime;
	EditText 	 dailyTotal;
	private static SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
	public static final String tag = SettingsActivity.class.getSimpleName();
	
	public static final String PREF     = "pref";
	public static final String DAY_MIN  = "day_start_time";
	public static final String DAY_MAX  = "day_end_time";
	public static final String DAY_TOTAL=  "day_total_alarms";
	public static final String PENDING  = "tota_pending";
	public static final int    DAILY_MAX= 10;
	int mStart;
	int mEnd;
	int mTotal;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        startTime 	  = (EditText) findViewById(R.id.startTime);
        endTime   	  = (EditText) findViewById(R.id.endTime);
        dailyTotal    = (EditText) findViewById(R.id.total);
        
        SharedPreferences mPref = getSharedPreferences(PREF,MODE_PRIVATE);
        
        mStart = mPref.getInt(DAY_MIN, 9);
        mEnd = mPref.getInt(SettingsActivity.DAY_MAX, 16);
        mTotal = mPref.getInt(SettingsActivity.DAY_TOTAL, 2);
		
        startTime.setText(String.valueOf(mStart));
        endTime.setText(String.valueOf(mEnd));
        dailyTotal.setText(String.valueOf(mTotal));
        
        endTime.setOnFocusChangeListener(focusListener);
        startTime.setOnFocusChangeListener(focusListener);
        dailyTotal.setOnFocusChangeListener(focusListener);
        
        setAlarm(this);
        new Thread(new Runnable(){
        	@Override
        	public void run(){
        		copyDB(SettingsActivity.this);
        	}
        }).start();
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	if(isFinishing()){
    		saveAlarm();
    		setAlarm(this);
    	}
    }
    private final OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(hasFocus)
				return;
			String txt = ((EditText) v).getText().toString();
			switch(v.getId()){
			case R.id.endTime: 
				if(txt.length() == 0 || ((Integer.parseInt(txt)<mStart))){
					endTime.setText(String.valueOf(mEnd));
					Toast.makeText(SettingsActivity.this, R.string.error_input, Toast.LENGTH_LONG).show();

				}else{
					mEnd = Integer.parseInt(txt);
				}
				break;
			case R.id.startTime:
				if(txt.length() == 0 || ((Integer.parseInt(txt)>mEnd))){
					startTime.setText(String.valueOf(mStart));
					Toast.makeText(SettingsActivity.this, R.string.error_input, Toast.LENGTH_LONG).show();

				}else{
					mStart = Integer.parseInt(txt);
				}
				break;
			case R.id.total:
				if(txt.length() == 0 || ((Integer.parseInt(txt)>DAILY_MAX))){
					dailyTotal.setText(String.valueOf(mTotal));
					Toast.makeText(SettingsActivity.this, R.string.error_input, Toast.LENGTH_LONG).show();

				}else{
					mTotal = Integer.parseInt(txt);
				}
				break;				
			}
			
		}
	};
    	
   
    private void saveAlarm(){
    	
    	int value = Integer.parseInt(startTime.getText().toString());
    	mStart = value < mEnd ? value : mStart;
    	value = Integer.parseInt(endTime.getText().toString());
    	mEnd = value > mStart ? value : mEnd;
    	value = Integer.parseInt(dailyTotal.getText().toString());
    	mTotal = value <= DAILY_MAX   ? value : mTotal;
    	
    	SharedPreferences mPref = getSharedPreferences(PREF,MODE_PRIVATE);
    	mPref.edit().putInt(DAY_MIN, mStart)
    				.putInt(DAY_MAX,mEnd )
    				.putInt(DAY_TOTAL,mTotal )
    				.commit();
    	
    }
    public static final void setAlarm(Context context){
        	
    	SharedPreferences mPref = context.getSharedPreferences(PREF,MODE_PRIVATE);
        
        int start = mPref.getInt(DAY_MIN, 9);
        int end   = mPref.getInt(SettingsActivity.DAY_MAX, 16);
        int total = mPref.getInt(SettingsActivity.DAY_TOTAL, 2);
		Calendar cal = Calendar.getInstance();

		long start_time = getExactTime(start, false);
		long end_time = getExactTime(end, false);

		long[] window = new long[total];
		long next_start = -1;
		for (int i = 0; i < total; i++) {
			long next = i > 0 ? 
					(((end_time - start_time) / total) + window[i - 1]) : start_time;
			window[i] = next < end_time ? next : end_time;
			if ((next_start == -1) && window[i] > cal.getTimeInMillis())
				next_start = window[i];
		}
		/*
		 * final notification of the day , set notification for tomorrow
		 */
		if (next_start == -1)
			next_start = getExactTime(start, true);

		int length = (int) (window.length > 1 ? (window[1] - window[0])
				: (end_time - start_time));
        
		Intent next_intent = new Intent(context.getApplicationContext(), AlarmBroadcastReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(context.getApplicationContext(), AlarmBroadcastReceiver.CODE,
				next_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pending);
      
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, next_start, length, pending);
        Log.e(tag, String.valueOf(length)+":"+String.valueOf(next_start));
       Log.e("AlarmsetTo", fmt.format(new Date(next_start)));
    }
	/*
	 * Returns system time representation of a 24 hour value
	 */
	public static final long getExactTime(int time, boolean tomorrow) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, time);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		if (tomorrow)
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);

		return cal.getTimeInMillis();
	}
	public static void copyDB(Context context){
			File dbFile = context.getApplicationContext().getDatabasePath(DBManager.DATABASE_NAME);
			if(!dbFile.getParentFile().exists())
				dbFile.getParentFile().mkdirs();
			Log.e(tag, "Copying Db");

			if(dbFile.exists()) 
				return;
			Log.e(tag, "Copying Db");

			AssetManager aManager = context.getAssets();
			try {
				InputStream is = aManager.open(DBManager.DATABASE_NAME);
				
				FileOutputStream os = new FileOutputStream(dbFile);
				int read = 0;
				byte[] buffer = new byte[1024];
				while(read != -1){
					read = is.read(buffer, 0, buffer.length);
					Log.e(tag, String.valueOf(read));
					if(read == -1) break;
					os.write(buffer, 0, read);
				}
				is.close();
				os.close();
			} catch (IOException e) {

				e.printStackTrace();
				Log.e(tag, e.toString());
			}
		}
		
	
}
