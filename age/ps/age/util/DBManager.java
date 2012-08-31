package ps.age.util;

import ps.age.contex.Question;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager {
	
	  public static final String DATABASE_NAME = "database.db";
	  private static final int DATABASE_VERSION = 1;
	  public Context context;
	  private SQLiteDatabase db;
	  OpenHelper openHelper;
	  
	  private static final String QUESTIONS_TABLE   = "questions";
	  private static final String ANSWER            = "answer";
	  private static final String QUESTION          = "question";
	  private static final String CHOICE_A  	    = "a";
	  private static final String CHOICE_B 			= "b";
	  private static final String CHOICE_C			= "c";
	  private static final String CHOICE_D	  		= "d";	  
	  private static final String ID   				= "id";
	  private static final String LOCATION          = "lcoation";
	  /*
	   * type of the extra associated with question , currently only URL's are supported 
	   */
	  	  
	  public DBManager(Context context) {
		  this.context = context;
		  openHelper = new OpenHelper(context);
		  db = openHelper.getWritableDatabase();		  
	}		

	  public Question getQuestion(){
		  
		  Cursor cursor = db.query(QUESTIONS_TABLE, null, ANSWER+"=?", new String[] { Long.toString(0)}, null, null, null);
		  Question object = null;
		  
		  if((cursor != null) && cursor.moveToFirst()){
			  object = new Question();
			  int question 	= cursor.getColumnIndex(QUESTION);
			  int id        = cursor.getColumnIndex(ID);
			  int a    		= cursor.getColumnIndex(CHOICE_A);
			  int b    		= cursor.getColumnIndex(CHOICE_B);
			  int c	        = cursor.getColumnIndex(CHOICE_C);
			  int d   	    = cursor.getColumnIndex(CHOICE_D);
			  int loc       = cursor.getColumnIndex(LOCATION);
			  object.setId(cursor.getInt(id));
			  object.setQuestion(cursor.getString(question));
			  object.setA(cursor.getString(a));
			  object.setB(cursor.getString(b));
			  object.setC(cursor.getString(c));
			  object.setD(cursor.getString(d));				
			  object.setLocation(cursor.getString(loc));
			  cursor.close();
			  return object;
		  }
		  
		  if(cursor != null)
			  cursor.close();
		  return null;
	  }
	  public void clearDB() {
		 
	  }
	  public boolean updateQuestion(Question question){
		  ContentValues values = new ContentValues();
		  values.put(ID, question.getId());
		  values.put(QUESTION, question.getQuestion());
		  values.put(CHOICE_A, question.getA());
		  values.put(CHOICE_B, question.getB());
		  values.put(CHOICE_C, question.getC());
		  values.put(CHOICE_D, question.getD());
		  values.put(ANSWER, question.getAnswer());
		  values.put(LOCATION, question.getLocation());
		  int numRows = db.update(QUESTIONS_TABLE, values, "id=?", new String[] { Long.toString(question.getId()) });
		  if(numRows == 1)
			  return true;		  
		  return false;
	  }
	  public void close(){
		  openHelper.close();
	  }
	  
	  private static class OpenHelper extends SQLiteOpenHelper {
		  OpenHelper(Context context) {
			  super(context, DATABASE_NAME, null, DATABASE_VERSION);
			 }
		  @Override 
		  public void onCreate(SQLiteDatabase db) {
	  }
		  @Override 
		  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	  }
		  }

	  }

