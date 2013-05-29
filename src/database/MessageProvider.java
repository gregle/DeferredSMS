package database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class MessageProvider extends ContentProvider {
	
	// All URIs share these parts
    public static final String AUTHORITY = "your.package.provider";
    public static final String SCHEME = "content://";

    // URIs
    // Used for all messages
    public static final String MESSAGES = SCHEME + AUTHORITY + "/messages";
    public static final Uri URI_MESSAGES = Uri.parse(MESSAGES);
    // Used for a single message, just add the id to the end
    public static final String MESSAGES_BASE = MESSAGES + "/";
	
	public MessageProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Implement this to handle requests to insert a new row.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        if (URI_MESSAGES.equals(uri)) {
        	final long id = Long.parseLong(uri.getLastPathSegment());
            result = DatabaseHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
                    .query(Message.TABLE_NAME, Message.FIELDS,
                            Message.COL_ID + " IS ?",
                            new String[] { String.valueOf(id) }, null, null,
                            null, null);
            result.setNotificationUri(getContext().getContentResolver(), URI_MESSAGES);
        }
        else if (uri.toString().startsWith(MESSAGES_BASE)) {
        	result = DatabaseHandler
                    .getInstance(getContext())
                    .getReadableDatabase()
                    .query(Message.TABLE_NAME, Message.FIELDS, null, null, null,
                            null, null, null);
        	result.setNotificationUri(getContext().getContentResolver(), URI_MESSAGES);
        }
        else {
        throw new UnsupportedOperationException("Not yet implemented");
        }

        return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO: Implement this to handle requests to update one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
