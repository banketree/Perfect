package com.our.perfect;




import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

public class SearchMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_search_main);
		
        
		LinearLayout MonitoringTextViesw = (LinearLayout) findViewById(R.id.monitoring);		
		MonitoringTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SearchMainActivity.this,Monitoring.class);				
				startActivity(intent);				
			}
		});
		LinearLayout NetworkTextViesw = (LinearLayout) findViewById(R.id.network);		
		NetworkTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SearchMainActivity.this,Premission.class);		
				startActivity(intent);				
			}
		});
		
		
		LinearLayout ElectricTextViesw = (LinearLayout) findViewById(R.id.electric);		
		ElectricTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SearchMainActivity.this,Electric.class);				
				startActivity(intent);				
			}
		});
		LinearLayout SpiteAppTextViesw = (LinearLayout) findViewById(R.id.spiteapp);		
		SpiteAppTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SearchMainActivity.this,SpiteApp.class);				
				startActivity(intent);				
			}
		});
		LinearLayout ToManageTextViesw = (LinearLayout) findViewById(R.id.to_manage_main);		
		ToManageTextViesw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SearchMainActivity.this,ManageMainActivity.class);				
				startActivity(intent);				
			}
		});
		
	}


}
