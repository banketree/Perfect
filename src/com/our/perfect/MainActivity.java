package com.our.perfect;


import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.our.network.NetWorkMainActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	SlidingMenu slidingMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(slidingMenu.LEFT);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenuoffset);
		slidingMenu.setTouchModeAbove(slidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.attachToActivity(this, slidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

	

    LinearLayout AuthorManagerTextViesw = (LinearLayout) findViewById(R.id.authormanage);		
	AuthorManagerTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,AuthorManage.class);				
			startActivity(intent);				
		}
	});
	
	LinearLayout NetControlTextViesw = (LinearLayout) findViewById(R.id.netcontrol);		
	NetControlTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,NetWorkMainActivity.class);				
			startActivity(intent);				
		}
	});
	LinearLayout RamManagerTextViesw = (LinearLayout) findViewById(R.id.rammanager);		
	RamManagerTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,RamManager.class);				
			startActivity(intent);				
		}
	});
	
	LinearLayout UnloadAppTextViesw = (LinearLayout) findViewById(R.id.unloadapp);		
	UnloadAppTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,UnloadApp.class);				
			startActivity(intent);				
		}
	});
	LinearLayout ComplaintTextViesw = (LinearLayout) findViewById(R.id.complaint);		
	ComplaintTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,Complaint.class);				
			startActivity(intent);				
		}
	});
	LinearLayout MonitoringTextViesw = (LinearLayout) findViewById(R.id.monitoring);		
	MonitoringTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,Monitoring.class);				
			startActivity(intent);				
		}
	});
	LinearLayout NetworkTextViesw = (LinearLayout) findViewById(R.id.network);		
	NetworkTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,Premission.class);		
			startActivity(intent);				
		}
	});
	
	
	LinearLayout ElectricTextViesw = (LinearLayout) findViewById(R.id.electric);		
	ElectricTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,Electric.class);				
			startActivity(intent);				
		}
	});
	LinearLayout SpiteAppTextViesw = (LinearLayout) findViewById(R.id.spiteapp);		
	SpiteAppTextViesw.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this,SpiteApp.class);				
			startActivity(intent);				
		}
	});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			slidingMenu.toggle(true);
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}



}
