package com.rastelliJ.futuretext;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.net.Uri;
import android.content.ContentValues;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.database.Cursor;
import android.content.DialogInterface;
import java.util.List;

public class MainActivity extends Activity {
	
	private EditText phoneNo, messageText;
	private Button sendButt;
	private static final int CONTACT_PICKER_RESULT = 1;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        phoneNo = (EditText)findViewById(R.id.phoneNo);
        messageText = (EditText)findViewById(R.id.txtMessage);
        sendButt = (Button)findViewById(R.id.btnSendSMS);
        phoneNo.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, CONTACT_PICKER_RESULT);
			}
		});
        
        sendButt.setOnClickListener(
    		new View.OnClickListener()
    		{
				public void onClick(View v) {
					if (phoneNo.getText().toString().trim().length() == 0)
					{
						Toast.makeText(getApplicationContext(), "Please enter a phone number", Toast.LENGTH_LONG).show();
						return;
					}
					if (messageText.getText().toString().trim().length() == 0)
					{
						Toast.makeText(getApplicationContext(), "Please enter your message", Toast.LENGTH_LONG).show();
						return;
					}
					if (messageText.getText().toString().trim().length() > 160)
					{
						sendLongSMS();
						phoneNo.setText("");
						messageText.setText("");
					}
					else
					{
						sendSMS();
						phoneNo.setText("");
						messageText.setText("");
					}
				}
    		});
    }

    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case CONTACT_PICKER_RESULT:
                final EditText phoneInput = (EditText) findViewById(R.id.phoneNo);
                Cursor cursor = null;  
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                try {  
                    Uri result = data.getData();  
                    String id = result.getLastPathSegment();  
                    cursor = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);  
                    phoneIdx = cursor.getColumnIndex(Phone.DATA);
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            phoneNumber = cursor.getString(phoneIdx);
                            allNumbers.add(phoneNumber);
                            cursor.moveToNext();
                        }
                    } else {
                        //no results actions
                    }  
                } catch (Exception e) {  
                   //error actions
                } finally {  
                    if (cursor != null) {  
                        cursor.close();
                    }

                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Choose a number");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String selectedNumber = items[item].toString();
                            selectedNumber = selectedNumber.replace("-", "");
                            phoneInput.setText(selectedNumber);
                        }
                    });
                    AlertDialog alert = builder.create();
                    if(allNumbers.size() > 1) {
                        alert.show();
                    } else {
                        String selectedNumber = phoneNumber.toString();
                        selectedNumber = selectedNumber.replace("-", "");
                        phoneInput.setText(selectedNumber);
                    }

                    if (phoneNumber.length() == 0) {  
                        //no numbers found actions  
                    }  
                }  
                break;  
            }  
        } else {
           //activity result error actions
        }  
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void sendSMS()
    {
    	SmsManager smsMan = SmsManager.getDefault();
    	smsMan.sendTextMessage(phoneNo.getText().toString(), null, messageText.getText().toString(), null, null);
    	addToSent(phoneNo.getText().toString(),messageText.getText().toString());
    	Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
    }
    
    public void sendLongSMS()
    {
    	SmsManager smsMan = SmsManager.getDefault();
    	ArrayList<String> parts = smsMan.divideMessage(messageText.getText().toString());
    	smsMan.sendMultipartTextMessage(phoneNo.getText().toString(), null, parts, null, null);
    	addToSent(phoneNo.getText().toString(),messageText.getText().toString());
    	Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
    }
    
    public void addToSent(String address, String body)
    {
    	ContentValues values = new ContentValues();
    	values.put("address", address);
    	values.put("body", body);
    	getContentResolver().insert(Uri.parse("content://sms/sent"),  values);
    	
    }
}
