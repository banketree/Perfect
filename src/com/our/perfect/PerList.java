package com.our.perfect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.bool;
import android.R.integer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PerList extends Activity {
	private List<PackageInfo> list ;
	private List<HashMap<String, Object>> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_per_list);
		String position;
		Intent intent = this.getIntent(); 	
		int index = intent.getExtras().getInt("position");

		
		
		
		
		
			 ListView listView =(ListView)findViewById(R.id.msglist);
			 mData = new ArrayList<HashMap<String, Object>>();
			 PackageManager packageManager = getPackageManager();
			 list = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

				 for(int i=0;i<list.size();i++){
					 PackageInfo packageInfo = list.get(i);
					 if((packageInfo.applicationInfo.flags&packageInfo.applicationInfo.FLAG_SYSTEM)<=0){
						 
					 HashMap<String, Object> hashMap = new HashMap<String, Object >();
					 String sharedPkgList[] = packageInfo.requestedPermissions;
					 PermissionGroupInfo pgi;
					 if(sharedPkgList!=null){
						for(int j=0;j<sharedPkgList.length;j++){
							String permName = sharedPkgList[j];
							try {
								PermissionInfo p = packageManager.getPermissionInfo(permName, 0);
								pgi=packageManager.getPermissionGroupInfo(p.group, 0);
								String permission = p.loadLabel(packageManager).toString();
								if(getResult(index, pgi, p, packageManager)==true){
									
									hashMap.put("appname",  packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
									mData.add(hashMap);
									break;
								}	
							} catch (Exception e) {
								// TODO: handle exception
							}

							}
							
						
					 }

					 }

				 }


			 System.out.println("sixziei"+mData.size());
			 SimpleAdapter simpleAdapter = new SimpleAdapter(PerList.this, mData,
					 R.layout.applistitem, new String[] {"appname"},
					 new int[] {R.id.appname1});
			 
			 listView.setAdapter(simpleAdapter);
			
			
		}
		
	private boolean getResult(int index,PermissionGroupInfo pgi,PermissionInfo p,PackageManager packageManager){
		String position=getPosition(index);
		if(index<10){
	
			 String permission1 = p.loadLabel(packageManager).toString();
			 if(permission1.equals(position)){
				 return true;
			 }
			 
		}
		else {
			
			 String permission2 = pgi.loadLabel(packageManager).toString();
			 if(permission2.equals(position)){
				 return true;
			 }
		}
	
		
		return false;
		
	}
	

	private String getPosition(int index) {
		// TODO Auto-generated method stub
		String position=null;
		switch (index) {
		case 1:
			position="发送短信";
			break;
		case 2:
			position="更改网络连接性";
			break;
		case 3:
			position="完全的网络访问权限";
			break;
		case 4:
			position="读取手机状态和身份";
			break;
		case 5:
			position="直接拨打电话号码";
			break;
		case 6:
			position="与蓝牙设备配对";
			break;
		case 11:
			position="您的信息";
			break;
		case 12:
			position="您的社交信息";
			break;
		case 13:
			position="您的位置";
			break;
		case 14:
			position="您的账户";
			break;
		case 15:
			position="相机";
			break;
		case 16:
			position="系统工具";
			break;
		case 17:
			position="麦克风";
			break;
		case 18:
			position="存储";
			break;


		}
		
		return position;
	}

	




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_list, menu);
		return true;
	}

}
