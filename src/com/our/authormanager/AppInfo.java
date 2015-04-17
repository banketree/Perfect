package com.our.authormanager;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

public class AppInfo
{
	private Drawable icon;
	private String appName;
	private String packageName;
	private boolean isSystemApp;
	private PackageInfo packageInfo;
	private String sharedPkgList[];
	
	public Drawable getIcon()
	{
		return icon;
	}
	public String[] getSharedPkgList() {
		return sharedPkgList;
	}
	public void setSharedPkgList(String[] sharedPkgList) {
		this.sharedPkgList = sharedPkgList;
	}
	public void setIcon(Drawable icon)
	{
		this.icon = icon;
	}
	public PackageInfo getPackageInfo() {
		return packageInfo;
	}
	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}
	public String getAppName()
	{
		return appName;
	}
	public void setAppName(String appName)
	{
		this.appName = appName;
	}
	public String getPackageName()
	{
		return packageName;
	}
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	public boolean isSystemApp()
	{
		return isSystemApp;
	}
	public void setSystemApp(boolean isSystemApp)
	{
		this.isSystemApp = isSystemApp;
	}

}
