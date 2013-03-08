package com.rastelliJ.futuretext;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.net.Uri;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.database.Cursor;
import android.content.DialogInterface;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnTimeSetListener{
	
	private EditText phoneNo, messageText;
	private TextView alarmText;
	private Button sendButt;
	private int pickerHour = 0;
	private int pickerMin = 0;
	
	private static final int CONTACT_PICKER_RESULT = 1;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        phoneNo = (EditText)findViewById(R.id.phoneNo);
        messageText = (EditText)findViewById(R.id.txtMessage);
        sendButt = (Button)findViewById(R.id.btnSendSMS);
        alarmText = (TextView)findViewById(R.id.alarmPrompt);
        
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
					
					Intent sIntent = new Intent(MainActivity.this, SendTService.class);
					sIntent.putExtra("phoneNo", phoneNo.getText().toString());
					sIntent.putExtra("msgTxt", messageText.getText().toString());
					PendingIntent psIntent = PendingIntent.getService(MainActivity.this, 0, sIntent, 0);
					AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, pickerHour);
					cal.set(Calendar.MINUTE, pickerMin);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), psIntent);

					phoneNo.setText("");
					messageText.setText("");
					alarmText.setText("");
					Toast.makeText(getApplicationContext(), "Your Message will be sent at " + pickerHour + ":" + pickerMin, Toast.LENGTH_LONG).show();
				}
    		}
		);
    }
    
    /*    
    public DatePickerFragment showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
        return newFragment;
    }

    public void showAlarmDialog(View v){  	
    	
    	datePick.show(getSupportFragmentManager(), "datePicker");
        timePick.show(getSupportFragmentManager(), "timePicker");
    }*/
    
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

    public void showTimePickerDialog(View v) {
    	TimePickerFragment newFragment = new TimePickerFragment();
        newFragment. show(getSupportFragmentManager(), "timePicker");  
    }
    
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
	    pickerHour = hourOfDay;
	    pickerMin = minute;
	    alarmText.setText("hour: " + pickerHour + " minute: " + pickerMin);
	}
}