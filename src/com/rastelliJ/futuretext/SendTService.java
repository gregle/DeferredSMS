package com.rastelliJ.futuretext;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SendTService extends IntentService{

	public SendTService() {
		super("SendTService");
	}
	
	protected void onHandleIntent(Intent intent)
	{
		String entPhoneNo = intent.getStringExtra("phoneNo");
		String entMsg = intent.getStringExtra("msgTxt");
		if (entMsg.trim().length() > 160) sendLongSMS(entPhoneNo, entMsg);
		else sendSMS(entPhoneNo, entMsg);		
		
	}
    public void sendSMS(String phoneNo, String msgTxt)
    {
    	SmsManager smsMan = SmsManager.getDefault();
    	smsMan.sendTextMessage(phoneNo, null, msgTxt, null, null);
    	addToSent(phoneNo, msgTxt);
    	String notice = "Message Sent to " + phoneNo;
    	Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
    }
    
    public void sendLongSMS(String phoneNo, String msgTxt)
    {
    	SmsManager smsMan = SmsManager.getDefault();
    	ArrayList<String> parts = smsMan.divideMessage(msgTxt);
    	smsMan.sendMultipartTextMessage(phoneNo, null, parts, null, null);
    	addToSent(phoneNo, msgTxt);
    	String notice = "Message Sent to " + phoneNo;
    	Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
    }
    
    public void addToSent(String address, String body)
    {
    	ContentValues values = new ContentValues();
    	values.put("address", address);
    	values.put("body", body);
    	getContentResolver().insert(Uri.parse("content://sms/sent"),  values);
    }
}
