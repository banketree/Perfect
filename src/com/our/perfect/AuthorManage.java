package com.our.perfect;


import java.util.List;






import com.our.authormanager.AppInfo;
import com.our.authormanager.AppInfoProvider;
import com.our.authormanager.AppList;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AuthorManage extends Activity {
	
	private  ListView listView;
	private List<PackageInfo> list ;
	private AppInfoProvider provider;
	private List<AppInfo> appInfolist;
	private AppManagerAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_author_manage);

		
		provider = new AppInfoProvider(AuthorManage.this);
		appInfolist = provider.getAllApps();
		listView = (ListView)findViewById(R.id.applist);
		adapter=new AppManagerAdapter(appInfolist);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				AppInfo appInfo = appInfolist.get(position);
				System.out.println("posintion"+position);
				PackageInfo packageInfo = appInfo.getPackageInfo();
				bundle.putParcelable("packageinfo", packageInfo);;
	//			bundle.putSerializable("packagename", packageInfo);
				
				
				intent.putExtras(bundle);
				intent.setClass(AuthorManage.this, AppList.class);
				startActivity(intent);
				
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
			AppInfo info = appInfos.get(position);
			if(convertView == null)
			{
				View view = View.inflate(AuthorManage.this, R.layout.applistitem, null);
				AppManagerViews views = new AppManagerViews();
				views.iv_app_icon = (ImageView) view.findViewById(R.id.appicon1);
				views.tv_app_name = (TextView) view.findViewById(R.id.appname1);
				views.iv_detail = (TextView) view.findViewById(R.id.detail1);
				views.iv_lean=(LinearLayout) view.findViewById(R.id.appyou);
				views.iv_app_icon.setImageDrawable(info.getIcon());
				views.tv_app_name.setText(info.getAppName());
				if(info.getSharedPkgList()!=null){
					views.iv_detail.setText(R.string.permission_adminer);
				}
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
	
	
	private class AppManagerViews
	{
		public LinearLayout iv_lean;
		public TextView iv_detail;
		ImageView iv_app_icon;
		TextView tv_app_name;
	}


		


	 



}
