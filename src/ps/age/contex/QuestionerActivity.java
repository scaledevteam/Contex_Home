package ps.age.contex;

import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import ps.age.contex.R;
import ps.age.util.DBManager;
import static ps.age.contex.SettingsActivity.*;
import static ps.age.contex.AlarmBroadcastReceiver.NOTIFICATION_ID;

public class QuestionerActivity extends Activity {
	
	TextView mQuestion;
	RadioButton mRadio0;
	RadioButton mRadio1;
	RadioButton mRadio2;
	RadioButton mRadio3;
	RadioGroup  mGroup;
	Button mDone;
	Question currentQuestion;
	int mPending;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questioner);
        mPending = getSharedPreferences(PREF,MODE_PRIVATE).getInt(PENDING, 0);
        if(mPending == 0){
        	finish();
        	return;
        }
        mQuestion = (TextView) 	 findViewById(R.id.question);
        mRadio0   = (RadioButton) findViewById(R.id.radio0);
        mRadio1   = (RadioButton) findViewById(R.id.radio1);
        mRadio2   = (RadioButton) findViewById(R.id.radio2);
        mRadio3   = (RadioButton) findViewById(R.id.radio3);
        mGroup    = (RadioGroup) findViewById(R.id.radioGroup1);
        mDone     = (Button)      findViewById(R.id.done);
        
        mDone.setOnClickListener(listener);
        
        loadQuestion();
        //get Question

    }
    @Override
    protected void onPause(){
    	super.onPause();
    	Log.e(tag, "onPause "+String.valueOf(mPending));
    	if(isFinishing()){
			SharedPreferences pref = getSharedPreferences(PREF,MODE_PRIVATE);
			pref.edit().putInt(PENDING, mPending)
				.commit();
			if(mPending>0)
				AlarmBroadcastReceiver.fireNotification(getApplicationContext(),false,true);
			else{
				NotificationManager notiMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notiMan.cancel(NOTIFICATION_ID);
			}
    	}
    }
    private void loadQuestion(){
    	DBManager manager = new DBManager(this);
    	currentQuestion = manager.getQuestion();
    	if(currentQuestion == null)
    	{
    		Toast.makeText(this, R.string.error_empty, Toast.LENGTH_LONG).show();
    		finish();
    		return;
    	}
    	mQuestion.setText(currentQuestion.getQuestion());
    	mRadio0.setText(currentQuestion.getA());
    	mRadio1.setText(currentQuestion.getB());
    	mRadio2.setText(currentQuestion.getC());
    	mRadio3.setText(currentQuestion.getD());
    	
    	
    }
    private void saveQuestion(){
    	DBManager manager = new DBManager(this);
    	manager.updateQuestion(currentQuestion);
    }
    final OnClickListener listener = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			
			

			int id = mGroup.getCheckedRadioButtonId();
			switch(id){
			case R.id.radio0: currentQuestion.setAnswer(1); break;
			case R.id.radio1: currentQuestion.setAnswer(2); break;
			case R.id.radio2: currentQuestion.setAnswer(3); break;
			case R.id.radio3: currentQuestion.setAnswer(4); break;
			default: finish(); return;
			}
			saveQuestion();
			mPending --;
			
			if(mPending > 0 ){
				loadQuestion();
				return;
			}
			else{
				finish();
			}
		}
    	
    };
}
