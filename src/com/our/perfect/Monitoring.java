package com.our.perfect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Monitoring extends Activity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitoring);
		Button buttonmsg= (Button)findViewById(R.id.msgbut1);
		Button buttonnet= (Button)findViewById(R.id.opennet);
		Button buttonwifi= (Button)findViewById(R.id.openwifi);
		Button buttonphone= (Button)findViewById(R.id.phone);
		Button buttonpho= (Button)findViewById(R.id.getpho);
		Button buttonblue= (Button)findViewById(R.id.openbule);
		Button buttonstate= (Button)findViewById(R.id.state);


		buttonmsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", 1);
				
				intent.putExtras(bundle);
				intent.setClass(Monitoring.this, PerList.class);
				startActivity(intent);

			}
		});
		
		
		
		
		buttonnet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", 2);
				
				intent.putExtras(bundle);
				intent.setClass(Monitoring.this, PerList.class);
				startActivity(intent);

			}
		});
		buttonwifi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", 3);
				
				intent.putExtras(bundle);
				intent.setClass(Monitoring.this, PerList.class);
				startActivity(intent);

			}
		});
		buttonphone.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
		// TODO Auto-generated method stub
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("position", 4);
		
					intent.putExtras(bundle);
		intent.setClass(Monitoring.this, PerList.class);
		startActivity(intent);

	}
		});
		buttonpho.setOnClickListener(new OnClickListener() {
	
	@Override
	  public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("position", 5);
		
		intent.putExtras(bundle);
		intent.setClass(Monitoring.this, PerList.class);
		startActivity(intent);

     	}
           });
		buttonblue.setOnClickListener(new OnClickListener() {
	
	    @Override
       	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("position", 6);
		
		intent.putExtras(bundle);
		intent.setClass(Monitoring.this, PerList.class);
		startActivity(intent);

	   }
           });
		buttonstate.setOnClickListener(new OnClickListener() {
			
	  @Override
	  public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("position", 7);
		
		intent.putExtras(bundle);
		intent.setClass(Monitoring.this, PerList.class);
		startActivity(intent);

	  }
    });
		
	  }
	

		


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.monitoring, menu);
		return true;
	}

}
