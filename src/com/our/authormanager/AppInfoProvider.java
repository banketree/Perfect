package com.our.authormanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;



public class AppInfoProvider
{
	private PackageManager packageManager;
	
	public AppInfoProvider(Context context)
	{
		//拿到�?��包管理器
		packageManager = context.getPackageManager();
	}

	public List<AppInfo> getAllApps()
	{
		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo myAppInfo;
	
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		for(int i=0;i<packageInfos.size();i++)
		{
			PackageInfo packageInfo = packageInfos.get(i);
			if((packageInfo.applicationInfo.flags&packageInfo.applicationInfo.FLAG_SYSTEM)<=0){
			myAppInfo = new AppInfo();
			String packageName = packageInfo.packageName;
			ApplicationInfo appInfo = packageInfo.applicationInfo;
			Drawable icon = appInfo.loadIcon(packageManager);
			String appName = appInfo.loadLabel(packageManager).toString();
			String sharedPkgList[] = packageInfo.requestedPermissions;
			myAppInfo.setAppName(appName);
			myAppInfo.setPackageName(packageName);
			myAppInfo.setIcon(icon);
			myAppInfo.setPackageInfo(packageInfo);
			myAppInfo.setSharedPkgList(sharedPkgList);
			if(filterApp(appInfo))
			{
				myAppInfo.setSystemApp(false);
			}
			else
			{
				myAppInfo.setSystemApp(true);
			}
			list.add(myAppInfo);
			}
		}
		return list;
	}
	

	public boolean filterApp(ApplicationInfo info)
	{

		if((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
		{
			return true;
		}
		else if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
		{
			return true;
		}
		return false;
	}

}
