package ps.age.contex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import ps.age.util.DBManager;


import static ps.age.contex.SettingsActivity.*;
/*
 * Broadcast Receiver to receive the notification for the next time the user get's notified
 * 
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

	public static final int NOTIFICATION_ID = 12536513;
	public static final int CODE = 869437898;	
	public static final String tag = "receiver";
	Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		SharedPreferences mPref = context.getSharedPreferences(
				PREF, Context.MODE_PRIVATE);
		Log.e(tag, "new broadcast");
		int start = mPref.getInt(DAY_MIN, 9);
		int end = mPref.getInt(DAY_MAX, 16);
		int total = mPref.getInt(DAY_TOTAL, 2);
		long start_time = getExactTime(start, false);
		long end_time   = getExactTime(end, false);
		long interval = (end_time-start_time)/total;
		/*
		 * last Alarm of the day
		 */
		if((end_time-interval)<System.currentTimeMillis())
			SettingsActivity.setAlarm(context);
		
		fireNotification(context,true,false);

	}

	

	


	public static void fireNotification(Context context,boolean new_notification,boolean quiet){
		int total = context.getSharedPreferences(PREF, MODE_PRIVATE).getInt(PENDING, 0);
		/*
		 * In case an error occured and no notifications to deliver yet the code gets called
		 */
		if( total == 0 && !new_notification)
			return;
		if(new_notification)
			total++;
		Log.e(tag, String.valueOf(total)+" "+context.toString());
	    //Get the Notification Service
	    NotificationManager notifier = (NotificationManager)context.
	    													getSystemService(NOTIFICATION_SERVICE);
	    
	    //Get the icon for the notification
	    Notification notification = new Notification(android.R.drawable.ic_menu_edit,context.getText(R.string.notification),System.currentTimeMillis());
	    
	    //Setup the Intent to open this Activity when clicked
	    Intent toLaunch = new Intent(context,QuestionerActivity.class);
	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, toLaunch, 0);
	    
	    //Set the Notification Info
	    notification.setLatestEventInfo(context, context.getResources().getString(R.string.notification), null, contentIntent);
	    
	    //Setting Notification Flags
	    notification.defaults |= Notification.DEFAULT_ALL;
	    notification.flags    |= Notification.FLAG_NO_CLEAR;
	    if(quiet)
	    	notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
    	
	    //Send the notification
	    notification.number = total;
	    
	    notifier.notify(NOTIFICATION_ID, notification);
	    
	    SharedPreferences pref = context.getSharedPreferences(PREF, MODE_PRIVATE);
	    pref.edit().putInt(PENDING, total)
	    	.commit();
	}
}
