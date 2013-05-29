package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static DatabaseHandler singleton;

    public static DatabaseHandler getInstance(final Context context) {
        if (singleton == null) {
            singleton = new DatabaseHandler(context);
        }
        return singleton;
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "messages";

    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Good idea to use process context here
        this.context = context.getApplicationContext();
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Message.CREATE_TABLE);
	}
	
	public synchronized Message getMessage(final long id)
	{
		final SQLiteDatabase db = this.getReadableDatabase();
		final Cursor cursor = db.query(Message.TABLE_NAME, 
				Message.FIELDS, Message.COL_ID + " IS ?", 
				new String[] {String.valueOf(id)}, null, null, null, null);
		if (cursor == null || cursor.isAfterLast())
		{
			return null;
		}
		Message item = null;
		if (cursor.moveToFirst()){
			item = new Message(cursor);
		}
		cursor.close();
		return item;
	}
	
	public synchronized boolean putMessage(final Message message)
	{
		boolean success = false;
        int result = 0;
        final SQLiteDatabase db = this.getWritableDatabase();

        if (message.id > -1) {
            result += db.update(Message.TABLE_NAME, message.getContent(),
                    Message.COL_ID + " IS ?",
                    new String[] { String.valueOf(message.id) });
        }

        if (result > 0) {
            success = true;
        } else {
            // Update failed or wasn't possible, insert instead
            final long id = db.insert(Message.TABLE_NAME, null,
                    message.getContent());

            if (id > -1) {
                message.id = id;
                success = true;
            }
        }
        if (success) {
            notifyProviderOnPersonChange();
        }
        return success;
	}
	
	public synchronized int removeMessage(final Message message)
	{
		final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(Message.TABLE_NAME,
                Message.COL_ID + " IS ?",
                new String[] { Long.toString(message.id) });
        if (result > 0) {
            notifyProviderOnPersonChange();
        }
        return result;
	}
	
	private void notifyProviderOnPersonChange() {
        context.getContentResolver().notifyChange(
                MessageProvider.URI_MESSAGES, null, false);
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
