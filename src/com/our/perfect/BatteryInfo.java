package com.our.perfect;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.BatteryStats;
import android.os.BatteryStats.Uid;
import android.os.Parcel;
import android.os.Process;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.PowerProfile;

public class BatteryInfo {
	private static final String TAG = "BatteryInfo";
	private static final boolean DEBUG = true;

	public static final int MSG_UPDATE_NAME_ICON = 1;
	private static final int MIN_POWER_THRESHOLD = 5;

	private IBatteryStats mBatteryInfo;
	private int mStatsType = BatteryStats.STATS_UNPLUGGED;
	private PowerProfile mPowerProfile;
	private static BatteryStatsImpl mStats;

	private double mMinPercentOfTotal = 0;
	private long mStatsPeriod = 0;
	public double mMaxPower = 1;
	private double mTotalPower;
	private double mWifiPower;
	private double mBluetoothPower;

	private long mAppWifiRunning;

	private final List<BatterySipper> mUsageList = new ArrayList<BatterySipper>();
	private final List<BatterySipper> mWifiSippers = new ArrayList<BatterySipper>();
	private final List<BatterySipper> mBluetoothSippers = new ArrayList<BatterySipper>();
	private Context mContext;
	public int testType;

	public enum DrainType {
		IDLE, CELL, PHONE, WIFI, BLUETOOTH, SCREEN, APP, KERNEL, MEDIASERVER;
	}

	public BatteryInfo(Context context) {
		testType = 1;
		mContext = context;
		mBatteryInfo = IBatteryStats.Stub.asInterface(ServiceManager.getService("batteryinfo"));
		mPowerProfile = new PowerProfile(context);
	}

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰皬閿熷姭鍒嗘瘮锝忔嫹灏忛敓鑺傞潻鎷峰�閿熶茎绛规嫹閿熸触灏嗘唻鎷烽敓鏂ゆ嫹閿熷壙纰夋嫹
	 * 
	 * @param minPercentOfTotal
	 */
	public void setMinPercentOfTotal(double minPercentOfTotal) {
		this.mMinPercentOfTotal = minPercentOfTotal;
	}

	/**
	 * 閿熸枻鎷峰彇閿熸枻鎷风墶閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
	 * 
	 * @return
	 */
	public double getTotalPower() {
		return mTotalPower;
	}

	/**
	 * 閿熸枻鎷峰彇閿熸枻鎷锋皭閿熺粸鐧告嫹閿熺粸鎲嬫嫹閿燂拷
	 * 
	 * @return
	 */
	public String getStatsPeriod() {
		return Utils.formatElapsedTime(mContext, mStatsPeriod);
	}

	public List<BatterySipper> getBatteryStats() {
		if (mStats == null) {
			mStats = load();
		}

		if (mStats == null) {
			return getAppListCpuTime();
		}

		mMaxPower = 0;
		mTotalPower = 0;
		mWifiPower = 0;
		mBluetoothPower = 0;
		mAppWifiRunning = 0;

		mUsageList.clear();
		mWifiSippers.clear();
		mBluetoothSippers.clear();
		processAppUsage();
		processMiscUsage();

		final List<BatterySipper> list = new ArrayList<BatterySipper>();

		Collections.sort(mUsageList);
		for (BatterySipper sipper : mUsageList) {
			if (sipper.getValue() < MIN_POWER_THRESHOLD)
				continue;
			final double percentOfTotal = ((sipper.getValue() / mTotalPower) * 100);
			sipper.setPercent(percentOfTotal);
			if (percentOfTotal < mMinPercentOfTotal)
				continue;
			list.add(sipper);
		}

		if (list.size() <= 1) {
			return getAppListCpuTime();
		}

		return list;
	}
	
	private long getAppProcessTime(int pid) {
		FileInputStream in = null;
		String ret = null;
		try {
			in = new FileInputStream("/proc/" + pid + "/stat");
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			ret = os.toString();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (ret == null) {
			return 0;
		}
		
		String[] s = ret.split(" ");
		if (s == null || s.length < 17) {
			return 0;
		}
		
		final long utime = string2Long(s[13]);
		final long stime = string2Long(s[14]);
		final long cutime = string2Long(s[15]);
		final long cstime = string2Long(s[16]);
		
		return utime + stime + cutime + cstime;
	}
	
	private long string2Long(String s) {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	private List<BatterySipper> getAppListCpuTime() {
		testType = 2;
		final List<BatterySipper> list = new ArrayList<BatterySipper>();
		
		long totalTime = 0;
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		
		HashMap<String, BatterySipper> templist = new HashMap<String, BatterySipper>();
		for (RunningAppProcessInfo info : runningApps) {
			final long time = getAppProcessTime(info.pid);
			String[] pkgNames = info.pkgList;
			if (pkgNames == null) {
				if (templist.containsKey(info.processName)) {
					BatterySipper sipper = templist.get(info.processName);
					sipper.setValue(sipper.getValue() + time);
				} else {
					templist.put(info.processName, new BatterySipper(mContext, info.processName, time));
				}
				totalTime += time;
			} else {
				for (String pkgName : pkgNames) {
					if (templist.containsKey(pkgName)) {
						BatterySipper sipper = templist.get(pkgName);
						sipper.setValue(sipper.getValue() + time);
					} else {
						templist.put(pkgName, new BatterySipper(mContext, pkgName, time));
					}
					totalTime += time;
				}
			}
		}
		
		if (totalTime == 0) totalTime = 1;
		
		list.addAll(templist.values());
		for (int i = list.size() - 1; i >= 0; i--) {
			BatterySipper sipper = list.get(i);
			double percentOfTotal = sipper.getValue() * 100 / totalTime;
			if (percentOfTotal < mMinPercentOfTotal) {
				list.remove(i);
			} else {
				sipper.setPercent(percentOfTotal);
			}
		}
		
		Collections.sort(list, new Comparator<BatterySipper>() {
			@Override
			public int compare(BatterySipper object1, BatterySipper object2) {
				double d1 = object1.getPercentOfTotal();
				double d2 = object2.getPercentOfTotal();
				if(d1-d2 < 0){
					return 1;
				}else if(d1-d2 > 0){
					return -1;
				}else{
					return 0;
				}
			}
		});
		
		return list;
	}

	private void processMiscUsage() {
		final int which = mStatsType;
		long uSecTime = SystemClock.elapsedRealtime() * 1000;
		final long uSecNow = mStats.computeBatteryRealtime(uSecTime, which);
		final long timeSinceUnplugged = uSecNow;
		if (DEBUG) {
			Log.i(TAG, "Uptime since last unplugged = " + (timeSinceUnplugged / 1000));
		}

		addPhoneUsage(uSecNow);
		addScreenUsage(uSecNow);
		addWiFiUsage(uSecNow);
		addBluetoothUsage(uSecNow);
		addIdleUsage(uSecNow); // Not including cellular idle power
		addRadioUsage(uSecNow);
	}

	private void addPhoneUsage(long uSecNow) {
		long phoneOnTimeMs = mStats.getPhoneOnTime(uSecNow, mStatsType) / 1000;
		double phoneOnPower = mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_ACTIVE) * phoneOnTimeMs / 1000;
		addEntry(DrainType.PHONE, phoneOnTimeMs, phoneOnPower);
	}

	private void addScreenUsage(long uSecNow) {
		double power = 0;
		long screenOnTimeMs = mStats.getScreenOnTime(uSecNow, mStatsType) / 1000;
		power += screenOnTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_SCREEN_ON);
		final double screenFullPower = mPowerProfile.getAveragePower(PowerProfile.POWER_SCREEN_FULL);
		for (int i = 0; i < BatteryStats.NUM_SCREEN_BRIGHTNESS_BINS; i++) {
			double screenBinPower = screenFullPower * (i + 0.5f) / BatteryStats.NUM_SCREEN_BRIGHTNESS_BINS;
			long brightnessTime = mStats.getScreenBrightnessTime(i, uSecNow, mStatsType) / 1000;
			power += screenBinPower * brightnessTime;
			if (DEBUG) {
				Log.i(TAG, "Screen bin power = " + (int) screenBinPower + ", time = " + brightnessTime);
			}
		}
		power /= 1000; // To seconds
		addEntry(DrainType.SCREEN, screenOnTimeMs, power);
	}

	private void addWiFiUsage(long uSecNow) {
		if (!versionValid()) {// less than 2.3.3
			return;
		}

		long onTimeMs = mStats.getWifiOnTime(uSecNow, mStatsType) / 1000;
		long runningTimeMs = mStats.getWifiRunningTime(uSecNow, mStatsType) / 1000;
		if (DEBUG)
			Log.i(TAG, "WIFI runningTime=" + runningTimeMs + " app runningTime=" + mAppWifiRunning);
		runningTimeMs -= mAppWifiRunning;
		if (runningTimeMs < 0)
			runningTimeMs = 0;
		double wifiPower = (onTimeMs * 0 * mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ON) + runningTimeMs
				* mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ON)) / 1000;
		if (DEBUG)
			Log.i(TAG, "WIFI power=" + wifiPower + " from procs=" + mWifiPower);
		BatterySipper bs = addEntry(DrainType.WIFI, runningTimeMs, wifiPower + mWifiPower);
		aggregateSippers(bs, mWifiSippers, "WIFI");
	}

	private void addBluetoothUsage(long uSecNow) {
		long btOnTimeMs = mStats.getBluetoothOnTime(uSecNow, mStatsType) / 1000;
		double btPower = btOnTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_BLUETOOTH_ON) / 1000;
		int btPingCount = mStats.getBluetoothPingCount();
		btPower += (btPingCount * mPowerProfile.getAveragePower(PowerProfile.POWER_BLUETOOTH_AT_CMD)) / 1000;
		BatterySipper bs = addEntry(DrainType.BLUETOOTH, btOnTimeMs, btPower + mBluetoothPower);
		aggregateSippers(bs, mBluetoothSippers, "Bluetooth");
	}

	private void addIdleUsage(long uSecNow) {
		long idleTimeMs = (uSecNow - mStats.getScreenOnTime(uSecNow, mStatsType)) / 1000;
		double idlePower = (idleTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_CPU_IDLE)) / 1000;
		addEntry(DrainType.IDLE, idleTimeMs, idlePower);
	}

	private void addRadioUsage(long uSecNow) {
		double power = 0;
		final int BINS = BatteryStats.NUM_SIGNAL_STRENGTH_BINS;
		long signalTimeMs = 0;
		for (int i = 0; i < BINS; i++) {
			long strengthTimeMs = mStats.getPhoneSignalStrengthTime(i, uSecNow, mStatsType) / 1000;
			power += strengthTimeMs / 1000 * mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_ON, i);
			signalTimeMs += strengthTimeMs;
		}
		long scanningTimeMs = mStats.getPhoneSignalScanningTime(uSecNow, mStatsType) / 1000;
		power += scanningTimeMs / 1000 * mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_SCANNING);
		BatterySipper bs = addEntry(DrainType.CELL, signalTimeMs, power);
		if (signalTimeMs != 0) {
			bs.noCoveragePercent = mStats.getPhoneSignalStrengthTime(0, uSecNow, mStatsType) / 1000 * 100.0 / signalTimeMs;
		}
	}

	private void aggregateSippers(BatterySipper bs, List<BatterySipper> from, String tag) {
		for (int i = 0; i < from.size(); i++) {
			BatterySipper wbs = from.get(i);
			if (DEBUG)
				Log.i(TAG, tag + " adding sipper " + wbs + ": cpu=" + wbs.cpuTime);
			bs.cpuTime += wbs.cpuTime;
			bs.gpsTime += wbs.gpsTime;
			bs.wifiRunningTime += wbs.wifiRunningTime;
			bs.cpuFgTime += wbs.cpuFgTime;
			bs.wakeLockTime += wbs.wakeLockTime;
			bs.tcpBytesReceived += wbs.tcpBytesReceived;
			bs.tcpBytesSent += wbs.tcpBytesSent;
		}
	}

	private BatterySipper addEntry(DrainType drainType, long time, double power) {
		if (power > mMaxPower)
			mMaxPower = power;
		mTotalPower += power;
		BatterySipper bs = new BatterySipper(mContext, drainType, null, new double[] { power });
		bs.usageTime = time;
		mUsageList.add(bs);
		return bs;
	}

	private boolean versionValid() {
		return android.os.Build.VERSION.SDK_INT >= 10;// less than 2.3.3
	}

	private void processAppUsage() {
		SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		final int which = mStatsType;
		final int speedSteps = mPowerProfile.getNumSpeedSteps();
		final double[] powerCpuNormal = new double[speedSteps];
		final long[] cpuSpeedStepTimes = new long[speedSteps];
		for (int p = 0; p < speedSteps; p++) {
			powerCpuNormal[p] = mPowerProfile.getAveragePower(PowerProfile.POWER_CPU_ACTIVE, p);
		}

		final double averageCostPerByte = getAverageDataCost();
		long uSecTime = mStats.computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, which);
		mStatsPeriod = uSecTime;
		SparseArray<? extends Uid> uidStats = mStats.getUidStats();
		final int NU = uidStats.size();
		for (int iu = 0; iu < NU; iu++) {
			Uid u = uidStats.valueAt(iu);
			double power = 0;
			double highestDrain = 0;
			String packageWithHighestDrain = null;
			Map<String, ? extends BatteryStats.Uid.Proc> processStats = u.getProcessStats();
			long cpuTime = 0;
			long cpuFgTime = 0;
			long wakelockTime = 0;
			long gpsTime = 0;
			if (processStats.size() > 0) {
				// 1, Process CPU time
				for (Map.Entry<String, ? extends BatteryStats.Uid.Proc> ent : processStats.entrySet()) {
					if (DEBUG)
						Log.i(TAG, "Process name = " + ent.getKey());

					Uid.Proc ps = ent.getValue();
					final long userTime = ps.getUserTime(which);
					final long systemTime = ps.getSystemTime(which);
					final long foregroundTime = ps.getForegroundTime(which);
					cpuFgTime += foregroundTime * 10; // convert to millis
					final long tmpCpuTime = (userTime + systemTime) * 10; // convert to millis
					int totalTimeAtSpeeds = 0;
					// Get the total first
					for (int step = 0; step < speedSteps; step++) {
						cpuSpeedStepTimes[step] = ps.getTimeAtCpuSpeedStep(step, which);
						totalTimeAtSpeeds += cpuSpeedStepTimes[step];
					}
					if (totalTimeAtSpeeds == 0)
						totalTimeAtSpeeds = 1;
					// Then compute the ratio of time spent at each speed
					double processPower = 0;
					for (int step = 0; step < speedSteps; step++) {
						double ratio = (double) cpuSpeedStepTimes[step] / totalTimeAtSpeeds;
						processPower += ratio * tmpCpuTime * powerCpuNormal[step];
					}
					cpuTime += tmpCpuTime;
					power += processPower;
					if (packageWithHighestDrain == null || packageWithHighestDrain.startsWith("*")) {
						highestDrain = processPower;
						packageWithHighestDrain = ent.getKey();
					} else if (highestDrain < processPower && !ent.getKey().startsWith("*")) {
						highestDrain = processPower;
						packageWithHighestDrain = ent.getKey();
					}
				}
			}
			if (cpuFgTime > cpuTime) {
				if (DEBUG && cpuFgTime > cpuTime + 10000) {
					Log.i(TAG, "WARNING! Cputime is more than 10 seconds behind Foreground time");
				}
				cpuTime = cpuFgTime; // Statistics may not have been gathered yet.
			}
			power /= 1000;

			// 2, Process wake lock usage
			Map<String, ? extends BatteryStats.Uid.Wakelock> wakelockStats = u.getWakelockStats();
			for (Map.Entry<String, ? extends BatteryStats.Uid.Wakelock> wakelockEntry : wakelockStats.entrySet()) {
				Uid.Wakelock wakelock = wakelockEntry.getValue();
				// Only care about partial wake locks since full wake locks are canceled when the user turns the screen off.
				BatteryStats.Timer timer = wakelock.getWakeTime(BatteryStats.WAKE_TYPE_PARTIAL);
				if (timer != null) {
					wakelockTime += timer.getTotalTimeLocked(uSecTime, which);
				}
			}
			wakelockTime /= 1000; // convert to millis
			// Add cost of holding a wake lock
			power += (wakelockTime * mPowerProfile.getAveragePower(PowerProfile.POWER_CPU_ACTIVE)) / 1000;

			// 3, Add cost of data traffic
			long tcpBytesReceived = u.getTcpBytesReceived(mStatsType);
			long tcpBytesSent = u.getTcpBytesSent(mStatsType);
			power += (tcpBytesReceived + tcpBytesSent) * averageCostPerByte;

			// 4, Add cost of keeping WIFI running.
			if (versionValid()) {
				long wifiRunningTimeMs = mStats.getWifiRunningTime(uSecTime, which) / 1000;
				mAppWifiRunning += wifiRunningTimeMs;
				power += (wifiRunningTimeMs * mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ON)) / 1000;
			}

			// 5, Process Sensor usage
			Map<Integer, ? extends BatteryStats.Uid.Sensor> sensorStats = u.getSensorStats();
			for (Map.Entry<Integer, ? extends BatteryStats.Uid.Sensor> sensorEntry : sensorStats.entrySet()) {
				Uid.Sensor sensor = sensorEntry.getValue();
				int sensorType = sensor.getHandle();
				BatteryStats.Timer timer = sensor.getSensorTime();
				long sensorTime = timer.getTotalTimeLocked(uSecTime, which) / 1000;
				double multiplier = 0;
				switch (sensorType) {
				case Uid.Sensor.GPS:
					multiplier = mPowerProfile.getAveragePower(PowerProfile.POWER_GPS_ON);
					gpsTime = sensorTime;
					break;
				default:
					android.hardware.Sensor sensorData = sensorManager.getDefaultSensor(sensorType);
					if (sensorData != null) {
						multiplier = sensorData.getPower();
						if (DEBUG) {
							Log.i(TAG, "Got sensor " + sensorData.getName() + " with power = " + multiplier);
						}
					}
				}
				power += (multiplier * sensorTime) / 1000;
			}

			if (DEBUG)
				Log.i(TAG, "UID " + u.getUid() + ": power=" + power);

			// Add the app to the list if it is consuming power
			if (power != 0) {
				BatterySipper app = new BatterySipper(mContext, DrainType.APP, u, new double[] { power });
				app.cpuTime = cpuTime;
				app.gpsTime = gpsTime;
				// app.wifiRunningTime = wifiRunningTimeMs;
				app.cpuFgTime = cpuFgTime;
				app.wakeLockTime = wakelockTime;
				app.tcpBytesReceived = tcpBytesReceived;
				app.tcpBytesSent = tcpBytesSent;
				if (u.getUid() == 1010) {
					mWifiSippers.add(app);
				} else if (u.getUid() == 2000) {
					mBluetoothSippers.add(app);
				} else {
					mUsageList.add(app);
				}
			}
			if (u.getUid() == 1010) {
				mWifiPower += power;
			} else if (u.getUid() == 2000) {
				mBluetoothPower += power;
			} else {
				if (power > mMaxPower)
					mMaxPower = power;
				mTotalPower += power;
			}

			if (DEBUG)
				Log.i(TAG, "Added power = " + power);
		}
	}

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹姣忛敓琛楄妭纰夋嫹骞抽敓鏂ゆ嫹閿熸枻鎷烽敓锟�	 * 
	 * @return
	 */
	private double getAverageDataCost() {
		final long WIFI_BPS = 1000000; // TODO: Extract average bit rates from system
		final long MOBILE_BPS = 200000; // TODO: Extract average bit rates from system
		final double WIFI_POWER = mPowerProfile.getAveragePower(PowerProfile.POWER_WIFI_ACTIVE) / 3600;
		final double MOBILE_POWER = mPowerProfile.getAveragePower(PowerProfile.POWER_RADIO_ACTIVE) / 3600;

		// 閿熸枻鎷烽敓鏂ゆ嫹閿熻鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
		final long mobileData = mStats.getMobileTcpBytesReceived(mStatsType) + mStats.getMobileTcpBytesSent(mStatsType);
		final long wifiData = mStats.getTotalTcpBytesReceived(mStatsType) + mStats.getTotalTcpBytesSent(mStatsType) - mobileData;
		// 閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹(閿熸枻鎷烽敓鏂ゆ嫹)
		final long radioDataUptimeMs = mStats.getRadioDataUptime() / 1000;
		// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�bps)
		final long mobileBps = radioDataUptimeMs != 0 ? mobileData * 8 * 1000 / radioDataUptimeMs : MOBILE_BPS;

		// 姣忛敓鏂ゆ嫹姣忛敓琛楄妭纰夋嫹閿熸枻鎷烽敓锟�		
		double mobileCostPerByte = MOBILE_POWER / (mobileBps / 8);
		// wifi姣忛敓鏂ゆ嫹姣忛敓琛楄妭纰夋嫹閿熸枻鎷烽敓锟�		
		double wifiCostPerByte = WIFI_POWER / (WIFI_BPS / 8);

		if (wifiData + mobileData != 0) {
			return (mobileCostPerByte * mobileData + wifiCostPerByte * wifiData) / (mobileData + wifiData);
		} else {
			return 0;
		}
	}

	private BatteryStatsImpl load() {
		BatteryStatsImpl mStats = null;
		try {

			byte[] data = mBatteryInfo.getStatistics();
			Parcel parcel = Parcel.obtain();
			parcel.unmarshall(data, 0, data.length);
			parcel.setDataPosition(0);
			mStats = BatteryStatsImpl.CREATOR.createFromParcel(parcel);
			/*if (versionValid()) {
				mStats.distributeWorkLocked(BatteryStats.STATS_SINCE_CHARGED);
			}*/
		} catch (Exception e) {
			Log.e(TAG, "RemoteException:", e);
		} catch (Error e) {
			Log.e(TAG, "Error:", e);
		}
		return mStats;
	}
}
