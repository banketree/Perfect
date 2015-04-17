package com.our.perfect;



import com.our.network.NetWorkMainActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class ManageMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_manage_main);
		
        LinearLayout AuthorManagerTextViesw = (LinearLayout) findViewById(R.id.authormanage);		
		AuthorManagerTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ManageMainActivity.this,AuthorManage.class);				
				startActivity(intent);				
			}
		});
		
		LinearLayout NetControlTextViesw = (LinearLayout) findViewById(R.id.netcontrol);		
		NetControlTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ManageMainActivity.this,NetWorkMainActivity.class);				
				startActivity(intent);				
			}
		});
		LinearLayout RamManagerTextViesw = (LinearLayout) findViewById(R.id.rammanager);		
		RamManagerTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ManageMainActivity.this,RamManager.class);				
				startActivity(intent);				
			}
		});
		
		LinearLayout UnloadAppTextViesw = (LinearLayout) findViewById(R.id.unloadapp);		
		UnloadAppTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ManageMainActivity.this,UnloadApp.class);				
				startActivity(intent);				
			}
		});
		LinearLayout ComplaintTextViesw = (LinearLayout) findViewById(R.id.complaint);		
		ComplaintTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ManageMainActivity.this,Complaint.class);				
				startActivity(intent);				
			}
		});
		
	}


}
