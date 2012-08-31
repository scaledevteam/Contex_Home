package ps.age.contex;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {
	public static final String tag = "tag";
	boolean test;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("Click Here !");
        
        setContentView(button);
        final Handler handler = new Handler();
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				handler.postDelayed(new Runnable(){

					@Override
					public void run() {
					 AlarmBroadcastReceiver.fireNotification(getApplicationContext(), true,test);
					 test=!test;
					}
					
				}, 000);
			
			}
        	
        });
    }
}
