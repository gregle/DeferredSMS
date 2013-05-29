package com.rastelliJ.deferredSMS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import database.*;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
	private CustomDateTimePicker customDT;
	private DatabaseHandler mDbHandle;
	private EditText phoneName, messageText;
	private String phoneNum, alarmtime;
	private TextView alarmText;
	private Button sendButt;
	private int pickerHour = 0, 
				pickerMin = 0, 
				pickerYear = 0, 
				pickerMonth = 0, 
				pickerDay = 0;
	
	private static final int CONTACT_PICKER_RESULT = 1;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Set up the custom Date Time Picker
        customDT = new CustomDateTimePicker(this, new  CustomDateTimePicker.ICustomDateTimeListener() {
        		public void onSet(Dialog dialog, Calendar calendarSelected,
                    Date dateSelected, int year, String monthFullName,
                    String monthShortName, int monthNumber, int date,
                    String weekDayFullName, String weekDayShortName,
                    int hour24, int hour12, int min, int sec,
                    String AM_PM) { 
	                	// Do something with the time chosen by the user
	                	pickerYear = year;
	                	pickerMonth = monthNumber;
	                	pickerDay = date;
	            	    pickerHour = hour24;
	            	    pickerMin = min;
	            	    alarmtime = weekDayFullName + ", " + monthFullName + " " + date + ", " + year + " " + hour12 + ":" + pickerMin + " " + AM_PM;
	            	    alarmText.setText("Send Date: " + alarmtime);
	                }

                public void onCancel() {}
            });
        customDT.set24HourFormat(false);
        customDT.setDate(Calendar.getInstance());
        findViewById(R.id.startTimeSetDialog).setOnClickListener(new OnClickListener() 
        {
                public void onClick(View v) {
                	customDT.showDialog();
                }
            });
        
        // Setup global variables
        phoneName = (EditText)findViewById(R.id.phoneNo);
        messageText = (EditText)findViewById(R.id.txtMessage);
        sendButt = (Button)findViewById(R.id.btnSendSMS);
        alarmText = (TextView)findViewById(R.id.alarmPrompt);
        
        //Create/Find DB
        mDbHandle = new DatabaseHandler(this);
        
        // Start Contact finder
        phoneName.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, CONTACT_PICKER_RESULT);
			}
		});
        
        // "Send" the message
        sendButt.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) {
				
				//Make sure the fields are filled
				if (phoneName.getText().toString().trim().length() == 0)
				{
					Toast.makeText(getApplicationContext(), "Please enter a phone number", Toast.LENGTH_LONG).show();
					return;
				}
				if (messageText.getText().toString().trim().length() == 0)
				{
					Toast.makeText(getApplicationContext(), "Please enter your message", Toast.LENGTH_LONG).show();
					return;
				}
		    	
				//Create a calendar variable that equates to the desired time to be sent
		    	Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, pickerYear);
				cal.set(Calendar.MONTH, pickerMonth);
				cal.set(Calendar.DATE, pickerDay);
				cal.set(Calendar.HOUR_OF_DAY, pickerHour);
				cal.set(Calendar.MINUTE, pickerMin);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
		    	
				//Set up the pending intent and assign put it in the alarm manger
				//will change this process once db is set up proper
				Intent sIntent = new Intent(MainActivity.this, SendTService.class);
				sIntent.putExtra("phoneNo", phoneNum.toString());
				sIntent.putExtra("msgTxt", messageText.getText().toString());
				PendingIntent psIntent = PendingIntent.getService(MainActivity.this,0, sIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), psIntent);
				
				//Add the latest message to the db
				Message m = new Message();
				mDbHandle.putMessage(m);
				
				//Clear all the fields and let the user know what's going on
				phoneName.setText("");
				messageText.setText("");
				alarmText.setText("");
				Toast.makeText(getApplicationContext(), "Your Message will be sent on " + alarmtime, Toast.LENGTH_LONG).show();
			}
		});
    }
    
    //Associated with the Contact picker getting it's results
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
	            case CONTACT_PICKER_RESULT:
	                Cursor cursor = null;  
	                String phoneNumber = "";
	                List<String> allNumbers = new ArrayList<String>();
	                int phoneIdx = 0;
	                try {  
	                    Uri result = data.getData();  
	                    String id = result.getLastPathSegment();  
	                    cursor = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);  
	                    phoneIdx = cursor.getColumnIndex(Phone.DATA);
	                    if (cursor.moveToFirst())
	                    {
	                        while (cursor.isAfterLast() == false)
	                        {
	                            phoneNumber = cursor.getString(phoneIdx);
	                            allNumbers.add(phoneNumber);
	                            cursor.moveToNext();
	                        }
	                    } 
	                    else 
	                    {
	                        //no results actions
	                	}  
	            	} 
	                catch (Exception e) 
	                {  
	                   //error actions
	                } 
	                finally 
	                {  
	                    if (cursor != null) cursor.close();
	                    
	                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
	                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
	                    builder.setTitle("Choose a number");
	                    builder.setItems(items, new DialogInterface.OnClickListener() 
	                    {
	                        public void onClick(DialogInterface dialog, int item) 
	                        {
	                            phoneNum = items[item].toString();
	                            phoneNum = phoneNum.replace("-", "");
	                            phoneName.setText(phoneNum);
	                        }
	                    });
	                    AlertDialog alert = builder.create();
	                    if(allNumbers.size() > 1)
	                    {
	                        alert.show();
	                    } 
	                    else 
	                    {
	                    	phoneNum = phoneNumber.toString();
	                        phoneNum = phoneNum.replace("-", "");
	                        phoneName.setText(phoneNum);
	                    }
	
	                    if (phoneNumber.length() == 0) 
	                    {  
	                        //no numbers found actions  
	                    }  
	                }  
	                break;  
            	}  
        	} 
        else 
        {
           //activity result error actions
        }  
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}