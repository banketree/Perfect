package com.our.perfect;

import java.util.ArrayList;
import java.util.List;

import com.our.authormanager.TaskInfo;
import com.our.authormanager.TaskInfoProvider;
import com.our.authormanager.TextFormater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class RamManager extends Activity implements OnClickListener
{
	private static final int LOAD_FINISH = 1;

	private TextView tv_process_count;
	private TextView tv_process_memory;

	private LinearLayout ll_process_load;
	private ListView lv_process_list;
	private Button bt_process_clear;
	private CheckBox cb_process_state;

	private ActivityManager activityManager;
	private List<RunningAppProcessInfo> runningAppProcessInfos;
	private TaskInfoProvider taskInfoProvider;
	private List<TaskInfo> taskInfos;
	private TaskInfoAdapter adapter;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case LOAD_FINISH:
					ll_process_load.setVisibility(View.INVISIBLE);
					adapter = new TaskInfoAdapter();
					lv_process_list.setAdapter(adapter);
					break;

				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		// 请求一个自己定义的title，但有一些Android系统是被修改过的，
		// 所以有可能是无法请求的，如乐Phone或小米的手机，这些系统是被修改过的，
		// 所以就要判断一下是否请求成功
		boolean flags = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_ram_manager);

		if (flags)
		{
			// 设置自定义的title
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.process_manager_title);
		}

		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_process_memory = (TextView) findViewById(R.id.tv_process_memory);

		ll_process_load = (LinearLayout) findViewById(R.id.ll_process_load);
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);
		bt_process_clear = (Button) findViewById(R.id.bt_process_clear);
		bt_process_clear.setOnClickListener(this);
		

		initData();

		lv_process_list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				// adapter里面的getItem返回的值
				Object obj = lv_process_list.getItemAtPosition(position);
				if (obj instanceof TaskInfo)
				{
					cb_process_state = (CheckBox) view
							.findViewById(R.id.cb_process_manager_state);
					TaskInfo taskInfo = (TaskInfo) obj;
					if (taskInfo.isCheck())
					{
						taskInfo.setCheck(false);
						cb_process_state.setChecked(false);
					}
					else
					{
						taskInfo.setCheck(true);
						cb_process_state.setChecked(true);
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.bt_process_clear:
				killTask();
				break;

			default:
				break;
		}
	}

	private void initData()
	{
		// 因为这个title是要显示当前进程数目和可用内存的，所以我们每次在这里都调用一下，以更新数据
		initTitle();
		ll_process_load.setVisibility(View.VISIBLE);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				taskInfoProvider = new TaskInfoProvider(
						RamManager.this);
				taskInfos = taskInfoProvider.getAllTask(runningAppProcessInfos);

				Message msg = new Message();
				msg.what = LOAD_FINISH;
				handler.sendMessage(msg);
			}
		}).start();
	}

	// 拿到当前运行的进程数目
	private int getRunningAppCount()
	{
		runningAppProcessInfos = activityManager.getRunningAppProcesses();
		return runningAppProcessInfos.size();
	}

	// 拿到系统剩余的内存
	private String getAvailMemory()
	{
		// new一个内存的对象
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		// 拿到现在系统里面的内存信息
		activityManager.getMemoryInfo(memoryInfo);
		// 拿到有效的内存空间
		long size = memoryInfo.availMem;
		return TextFormater.dataSizeFormat(size);
	}

	// 设置title的信息
	private void initTitle()
	{
		tv_process_count.setText("进程数目：" + getRunningAppCount());
		tv_process_memory.setText("剩余内存：" + getAvailMemory());
	}

	// 一键清理的函数
	private void killTask()
	{
		int total = 0;
		int memorySize = 0;
		for (TaskInfo taskInfo : systemTaskInfos)
		{
			if(taskInfo.isCheck())
			{
				// 杀死进程
				activityManager.killBackgroundProcesses(taskInfo.getPackageName());
				total++;
				memorySize += taskInfo.getMemory();
				taskInfos.remove(taskInfo);
			}
		}

		for (TaskInfo taskInfo : userTaskInfos)
		{
			if(taskInfo.isCheck())
			{
				activityManager.killBackgroundProcesses(taskInfo.getPackageName());
				total++;
				memorySize += taskInfo.getMemory();
				taskInfos.remove(taskInfo);
			}
		}

		Toast.makeText(
				this,
				"已经杀死了" + total + "个进程！释放了"
						+ TextFormater.getSizeFromKB(memorySize) + "空间",
				Toast.LENGTH_SHORT).show();
		
		//重新加载界面
		adapter = new TaskInfoAdapter();
		lv_process_list.setAdapter(adapter);
	}

	// ===========================================================================

	private class TaskInfoAdapter extends BaseAdapter
	{

		public TaskInfoAdapter()
		{
			// 存放用户的应用进程
			userTaskInfos = new ArrayList<TaskInfo>();
			// 存放系统的应用进程
			systemTaskInfos = new ArrayList<TaskInfo>();

			for (TaskInfo taskInfo : taskInfos)
			{
				if (taskInfo.isSystemProcess())
				{
					systemTaskInfos.add(taskInfo);
				}
				else
				{
					userTaskInfos.add(taskInfo);
				}
			}
		}

		@Override
		public int getCount()
		{
			// 加上两个标签，一个是系统标签，一个是用户标签
			return taskInfos.size() + 2;
		}

		@Override
		public Object getItem(int position)
		{
			if (position == 0)
			{
				return 0; // 显示成用户应用的标签
			}
			else if (position <= userTaskInfos.size())
			{
				return userTaskInfos.get(position - 1); // 用户应用进程的条目
			}
			else if (position == userTaskInfos.size() + 1)
			{
				return position; // 显示成系统进程的标签
			}
			else if (position <= taskInfos.size() + 2)
			{
				// 系统应用进程的条目
				return systemTaskInfos.get(position - userTaskInfos.size() - 2);
			}
			else
			{
				return position;
			}
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view;
			TaskInfoViews views;
			TaskInfo taskInfo;
			if (position == 0)
			{
				// 显示成用户应用的标签
				return newTextView("用户进程(" + userTaskInfos.size() + ")");
			}
			else if (position <= userTaskInfos.size())
			{
				// 用户应用进程的条目
				taskInfo = userTaskInfos.get(position - 1);
			}
			else if (position == userTaskInfos.size() + 1)
			{
				// 显示成系统进程的标签
				return newTextView("系统进程(" + systemTaskInfos.size() + ")");
			}
			else if (position <= taskInfos.size() + 2)
			{
				// 系统应用进程的条目
				taskInfo = systemTaskInfos.get(position - userTaskInfos.size()
						- 2);
			}
			else
			{
				taskInfo = new TaskInfo();
			}
			if (convertView == null || convertView instanceof TextView)
			{
				view = View.inflate(RamManager.this,
						R.layout.process_manager_item, null);

				views = new TaskInfoViews();
				views.iv_process_icon = (ImageView) view
						.findViewById(R.id.iv_process_manager_icon);
				views.tv_process_name = (TextView) view
						.findViewById(R.id.tv_process_manager_name);
				views.tv_process_memory = (TextView) view
						.findViewById(R.id.tv_process_manager_memory);
				views.cb_process_state = (CheckBox) view
						.findViewById(R.id.cb_process_manager_state);
				view.setTag(views);
			}
			else
			{
				view = convertView;
				views = (TaskInfoViews) view.getTag();
			}
			views.iv_process_icon.setImageDrawable(taskInfo.getIcon());
			views.tv_process_name.setText(taskInfo.getName());
			views.tv_process_memory.setText("占用内存："
					+ TextFormater.getSizeFromKB(taskInfo.getMemory()));
			views.cb_process_state.setChecked(taskInfo.isCheck());
			return view;
		}

		private TextView newTextView(String title)
		{
			TextView tv_title = new TextView(RamManager.this);
			tv_title.setText(title);
			return tv_title;
		}

	}

	private class TaskInfoViews
	{
		ImageView iv_process_icon;
		TextView tv_process_name;
		TextView tv_process_memory;
		CheckBox cb_process_state;
	}

}
