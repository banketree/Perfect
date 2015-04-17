package com.our.authormanager;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.our.perfect.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AppList extends Activity {
	private String packagename;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_list);
		PackageManager packageManager = getPackageManager();
		Intent intent = this.getIntent(); 
		PackageInfo packageInfo=intent.getParcelableExtra("packageinfo");
		String sharedPkgList[] = packageInfo.requestedPermissions;
		packagename = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
		TextView textView = (TextView)findViewById(R.id.packagename);
        ListView listView = (ListView)findViewById(R.id.authorlist);


		
		if (sharedPkgList!=null) {
			textView.setText(packagename);
			PermissionGroupInfo pgi;
			List<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
		
			for(int i=0;i<sharedPkgList.length;i++){
			
				try {
					String permName = sharedPkgList[i];
					PermissionInfo p = packageManager.getPermissionInfo(permName, 0);
					pgi=packageManager.getPermissionGroupInfo(p.group, 0);
					HashMap<String, String> hashMap = new HashMap<String, String >();
					
					hashMap.put("name","权限名称： "+pgi.loadLabel(packageManager).toString());
				hashMap.put("permissions","权限内容 ："+p.loadLabel(packageManager).toString());
					mData.add(hashMap);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				}
				SimpleAdapter simpleAdapter = new SimpleAdapter(AppList.this, mData, R.layout.applistitem, 
				new String[] {"name","permissions"},
				 new int[] {R.id.appname1,R.id.detail1});
					listView.setAdapter(simpleAdapter);
		}
		else {
			textView.setText("û����˽Ȩ��");
		}
		

	}	
	
	
	
	
	
	

		
	
	
	}


	

