package com.our.perfect;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Premission extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_premission);
		Button buttonmsg= (Button)findViewById(R.id.premsg);
		Button buttontell= (Button)findViewById(R.id.pretell);
		Button buttonlocal= (Button)findViewById(R.id.prelocal);
		Button buttonime= (Button)findViewById(R.id.preime);
		Button buttoncam= (Button)findViewById(R.id.precam);
		Button buttonsys= (Button)findViewById(R.id.presys);
		Button buttonsmp= (Button)findViewById(R.id.premp);
		Button buttoncai= (Button)findViewById(R.id.precai);


		buttonmsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", 11);
				
				intent.putExtras(bundle);
				intent.setClass(Premission.this, PerList.class);
				startActivity(intent);

			}
		});
		
		buttontell.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", 12);
				
				intent.putExtras(bundle);
				intent.setClass(Premission.this, PerList.class);
				startActivity(intent);

			}
		});
		buttonlocal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", 13);
				
				intent.putExtras(bundle);
				intent.setClass(Premission.this, PerList.class);
				startActivity(intent);

			}
		});
		buttonime.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
		// TODO Auto-generated method stub
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("position",14);
		
					intent.putExtras(bundle);
		intent.setClass(Premission.this, PerList.class);
		startActivity(intent);

	}
		});
		buttoncam.setOnClickListener(new OnClickListener() {
	
	@Override
	  public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("position", 15);
		
		intent.putExtras(bundle);
		intent.setClass(Premission.this, PerList.class);
		startActivity(intent);

     	}
           });
		buttonsys.setOnClickListener(new OnClickListener() {
	
	    @Override
       	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("position", 16);
		
		intent.putExtras(bundle);
		intent.setClass(Premission.this, PerList.class);
		startActivity(intent);

	   }
           });
		buttonsmp.setOnClickListener(new OnClickListener() {
			
	  @Override
	  public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("position", 17);
		
		intent.putExtras(bundle);
		intent.setClass(Premission.this, PerList.class);
		startActivity(intent);

	  }
    });
		buttoncai.setOnClickListener(new OnClickListener() {
			
			  @Override
			  public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("position", 18);
				
				intent.putExtras(bundle);
				intent.setClass(Premission.this, PerList.class);
				startActivity(intent);

			  }
		    });
			
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.network, menu);
		return true;
	}

}
