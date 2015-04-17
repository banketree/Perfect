package com.our.authormanager;

import java.util.ArrayList;
import java.util.List;

import com.our.perfect.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;


public class TaskInfoProvider
{
	private PackageManager packageManager;
	private ActivityManager activityManager;
	private Drawable defaultIcon;

	public TaskInfoProvider(Context context)
	{
		defaultIcon = context.getResources().getDrawable(R.drawable.ic_launcher);
		packageManager = context.getPackageManager();
		activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public List<TaskInfo> getAllTask(
			List<RunningAppProcessInfo> runningAppProcessInfos)
	{
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos)
		{
			TaskInfo taskInfo = new TaskInfo();
			int id = runningAppProcessInfo.pid;
			taskInfo.setId(id);
			String packageName = runningAppProcessInfo.processName;
			taskInfo.setPackageName(packageName);
			try
			{
				
				ApplicationInfo applicationInfo = packageManager
						.getPackageInfo(packageName, 0).applicationInfo;
				
				Drawable icon = applicationInfo.loadIcon(packageManager);
				taskInfo.setIcon(icon);

				String name = applicationInfo.loadLabel(packageManager)
						.toString();
				taskInfo.setName(name);

				taskInfo.setSystemProcess(!filterApp(applicationInfo));
			}
			catch (Exception e)
			{
				e.printStackTrace();
		
				taskInfo.setName(packageName);
				taskInfo.setSystemProcess(true);
				taskInfo.setIcon(defaultIcon);
			}
		MemoryInfo[] memoryInfos = activityManager
					.getProcessMemoryInfo(new int[] { id });
		
			int memory = memoryInfos[0].getTotalPrivateDirty();
			taskInfo.setMemory(memory);
			taskInfos.add(taskInfo);
			taskInfo = null;
		}
		return taskInfos;
	}


	public boolean filterApp(ApplicationInfo info)
	{

		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
		{
			return true;
		}
		else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
		{
			return true;
		}
		return false;
	}

}
