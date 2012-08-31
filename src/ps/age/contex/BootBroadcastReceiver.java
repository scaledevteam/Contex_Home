package ps.age.contex;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.e("TimeBroadcastReceiver", "success");
		SettingsActivity.setAlarm(context);

		AlarmBroadcastReceiver.fireNotification(context,false,false);
        new Thread(new Runnable(){
        	@Override
        	public void run(){
        		SettingsActivity.copyDB(context);
        	}
        }).start();
	}
}
