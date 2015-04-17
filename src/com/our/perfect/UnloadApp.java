package com.our.perfect;


import java.util.List;

import com.our.authormanager.AppInfo;
import com.our.authormanager.AppInfoProvider;
import com.our.authormanager.AppList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UnloadApp extends Activity {
	TextView mTextView;
	private  ListView listView;
	private AppInfoProvider provider;
	private List<AppInfo> appInfolist;
	private AppManagerAdapter adapter;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unload_app);
		
		provider = new AppInfoProvider(UnloadApp.this);
		appInfolist = provider.getAllApps();
		listView = (ListView)findViewById(R.id.unloadlist);
		adapter=new AppManagerAdapter(appInfolist);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				AppInfo item = appInfolist.get(arg2);
		String packagename=item.getPackageName();
		Uri packageUri = Uri.parse("package:"+packagename);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);         
		startActivity(uninstallIntent);
				
			}
		});

		 
	}
	private class AppManagerAdapter extends BaseAdapter
	{
		private List<AppInfo> appInfos;
		
		public AppManagerAdapter(List<AppInfo> appInfos)
		{
			this.appInfos = appInfos;
		}
		
		//设置adapter的数据
		public void setAppInfos(List<AppInfo> appInfos)
		{
			this.appInfos = appInfos;
		}

		@Override
		public int getCount()
		{
			return appInfos.size();
		}

		@Override
		public Object getItem(int position)
		{
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			final AppInfo info = appInfos.get(position);
			if(convertView == null)
			{
				View view = View.inflate(UnloadApp.this, R.layout.applistitem, null);
				AppManagerViews views = new AppManagerViews();
				views.iv_app_icon = (ImageView) view.findViewById(R.id.appicon1);
				views.tv_app_name = (TextView) view.findViewById(R.id.appname1);
		
				views.iv_app_icon.setImageDrawable(info.getIcon());
				views.tv_app_name.setText(info.getAppName());
				view.setTag(views);

				
				
				return view;
			}
			else
			{
				AppManagerViews views = (AppManagerViews) convertView.getTag();
				views.iv_app_icon.setImageDrawable(info.getIcon());
				views.tv_app_name.setText(info.getAppName());
				return convertView;
			}
			
			
			
		}
		
	}
	//用来优化listview的类
	private class AppManagerViews
	{
		public Button iv_button;
		ImageView iv_app_icon;
		TextView tv_app_name;
	}


}
