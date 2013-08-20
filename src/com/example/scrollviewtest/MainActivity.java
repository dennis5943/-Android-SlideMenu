package com.example.scrollviewtest;

import com.ui.customview.ScrollMenu;
import com.ui.customview.ScrollMenu.ScrollMenuListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener,ScrollMenuListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn = (Button)findViewById(R.id.button);
		btn.setOnClickListener(this);
		
		ScrollMenu sm = (ScrollMenu)findViewById(R.id.scrollmenu1);
		sm.setScrollMenuListener(this);
		
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	    String imsi = mTelephonyMgr.getSubscriberId();
	    String imei = mTelephonyMgr.getDeviceId(); 
	    String simno = mTelephonyMgr.getSimSerialNumber();
	    Log.i("[TEST]", ""+imsi);
	    Log.i("[TEST]", ""+imei);
	    Log.i("[TEST]", ""+simno);
	    Log.i("[TEST]", "ss:"+mTelephonyMgr.getSimOperatorName());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ScrollMenu sm = (ScrollMenu)findViewById(R.id.scrollmenu1);
		sm.toggleMenu();
	}

	@Override
	public void onMenuStatusChange(boolean isOpen) {
		// TODO Auto-generated method stub
		Log.i("ssss", "show menu:" + isOpen);
	}
}
