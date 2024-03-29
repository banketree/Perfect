package com.our.perfect;

import java.util.List;




import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Electric extends Activity {
	private final int PROGRESS_DIALOG_ID = 1;
	
	private TextView batterySummary;
	private ListView listView;
	private customAdapter adapter;
	private BatteryInfo info;
	private ProgressDialog progressDialog;
	private List<BatterySipper> mList;
	private String mBatterySummary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_electric);

		batterySummary = (TextView) findViewById(R.id.batterySummary);
		listView = (ListView) findViewById(R.id.listview);
		adapter = new customAdapter();
		listView.setAdapter(adapter);

		info = new BatteryInfo(this);
		info.setMinPercentOfTotal(0.01);
		
		registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		getBatteryStats();
	}

	private void getBatteryStats() {
		showDialog(PROGRESS_DIALOG_ID);
		new Thread() {
			@Override
			public void run() {
				mList = info.getBatteryStats();
				mHandler.sendEmptyMessage(1);
			}
		}.start();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case 1:
				if(isFinishing())
					return;
				progressDialog.dismiss();
				batterySummary.setText(mBatterySummary + "\n测试信息：获取方式" + (info.testType == 1 ? "(根据记录文件)" : "(根据CPU使用时间)"));
				adapter.setData(mList);
				break;
			}
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG_ID:
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("请稍候...");
			return progressDialog;
		}
		return null;
	};

	class customAdapter extends BaseAdapter {
		private List<BatterySipper> list;
		private LayoutInflater inflater;

		public customAdapter() {
			inflater = LayoutInflater.from(Electric.this);
		}

		public void setData(List<BatterySipper> list) {
			this.list = list;
			
			for (int i = list.size() - 1; i >= 0; i--) {
				final BatterySipper sipper = list.get(i);
				String name = sipper.getName();
				if (name == null) {
					Drawable icon = sipper.getIcon();
					switch (sipper.getDrainType()) {
					case CELL:
						name = getString(R.string.power_cell);
						icon = getResources().getDrawable(R.drawable.ic_settings_cell_standby);
						break;
					case IDLE:
						name = getString(R.string.power_idle);
						icon = getResources().getDrawable(R.drawable.ic_settings_phone_idle);
						break;
					case BLUETOOTH:
						name = getString(R.string.power_bluetooth);
						icon = getResources().getDrawable(R.drawable.ic_settings_bluetooth);
						break;
					case WIFI:
						name = getString(R.string.power_wifi);
						icon = getResources().getDrawable(R.drawable.ic_settings_wifi);
						break;
					case SCREEN:
						name = getString(R.string.power_screen);
						icon = getResources().getDrawable(R.drawable.ic_settings_display);
						break;
					case PHONE:
						name = getString(R.string.power_phone);
						icon = getResources().getDrawable(R.drawable.ic_settings_voice_calls);
						break;
					case KERNEL:
						name = getString(R.string.process_kernel_label);
						icon = getResources().getDrawable(R.drawable.ic_power_system);
						break;
					case MEDIASERVER:
						name = getString(R.string.process_mediaserver_label);
						icon = getResources().getDrawable(R.drawable.ic_power_system);
						break;
					default:
						break;
					}
					
					if (name != null) {
						sipper.setName(name);
						if (icon == null) {
							PackageManager pm = getPackageManager();
							icon = pm.getDefaultActivityIcon();
						}
						sipper.setIcon(icon);
					} else {
						list.remove(i);
					}
				}
			}
			notifyDataSetInvalidated();
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public BatterySipper getItem(int position) {
			return list == null ? null : list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = inflater.inflate(R.layout.listview_item, null);
				holder.appIcon = (ImageView) convertView.findViewById(R.id.appIcon);
				holder.appName = (TextView) convertView.findViewById(R.id.appName);
				holder.txtProgress = (TextView) convertView.findViewById(R.id.txtProgress);
				holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			BatterySipper sipper = getItem(position);
			holder.appName.setText(sipper.getName());
			holder.appIcon.setImageDrawable(sipper.getIcon());
			
			double percentOfTotal = sipper.getPercentOfTotal();
			holder.txtProgress.setText(format(percentOfTotal));
			holder.progress.setProgress((int) percentOfTotal);

			return convertView;
		}
	}

	class Holder {
		ImageView appIcon;
		TextView appName;
		TextView txtProgress;
		ProgressBar progress;
	}

	private String format(double size) {
		return String.format("%1$.2f%%", size);
		// return new BigDecimal("" + size).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBatteryInfoReceiver);
		super.onDestroy();
	}

	private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				String batteryLevel = Utils.getBatteryPercentage(intent);
				String batteryStatus = Utils.getBatteryStatus(Electric.this.getResources(), intent);
				mBatterySummary = context.getResources().getString(R.string.power_usage_level_and_status, batteryLevel, batteryStatus);
			}
		}
	};
}
