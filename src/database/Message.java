package database;

import android.content.ContentValues;
import android.database.Cursor;

public class Message {
	public static final String TABLE_NAME = "text";
	public static final String COL_ID = "_id";
	public static final String DEFAULT_SORT_ORDER = "stime DESC";
	public static final String COLUMN_NAME_RECIPIENT = "recipient";
	public static final String COLUMN_NAME_MESSAGE = "message";
	public static final String COLUMN_NAME_SEND_TIME = "stime";
	public static final String AUTHORITY = "com.rastelliJ.deferredSMS";
	
	public static final String[] FIELDS = {COL_ID, COLUMN_NAME_MESSAGE, COLUMN_NAME_RECIPIENT, COLUMN_NAME_SEND_TIME};
	
	
	private static final String TEXT_TYPE = " TEXT NOT NULL DEFAULT ''";
	private static final String COMMA_SEP = ",";
	public static final String CREATE_TABLE =
	    "CREATE TABLE " + TABLE_NAME + " (" 
		+ COL_ID + " INTEGER PRIMARY KEY," 
		+ COLUMN_NAME_MESSAGE + TEXT_TYPE + COMMA_SEP 
		+ COLUMN_NAME_RECIPIENT + TEXT_TYPE + COMMA_SEP 
		+ COLUMN_NAME_SEND_TIME + TEXT_TYPE + " )";
	
	public long id = -1;
	public String recipient = "";
	public String message = "";
	public String stime = "";
	
	public Message(){}
	
	public Message(final Cursor cursor)
	{
		this.id = cursor.getLong(0);
		this.recipient = cursor.getString(1);
		this.message = cursor.getString(2);
		this.stime = cursor.getString(3);
	}
	
	public ContentValues getContent(){
		final ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_MESSAGE, message);
		values.put(COLUMN_NAME_RECIPIENT, recipient);
		values.put(COLUMN_NAME_SEND_TIME, stime);
		
		return values;
	}

}