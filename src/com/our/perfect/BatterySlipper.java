package com.our.perfect;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.BatteryStats.Uid;

import com.our.perfect.BatteryInfo.DrainType;

class BatterySipper implements Comparable<BatterySipper> {

	private final Context mContext;
	private final HashMap<String, UidToDetail> mUidCache = new HashMap<String, UidToDetail>();
	private String name;
	private Drawable icon;
	private Uid uidObj;
	private double value;
	private double[] values;
	long usageTime;
	long cpuTime;
	long gpsTime;
	long wifiRunningTime;
	long cpuFgTime;
	long wakeLockTime;
	long tcpBytesReceived;
	long tcpBytesSent;
	private double percent;
	double noCoveragePercent;
	private String defaultPackageName;
	private DrainType drainType;

	static class UidToDetail {
		String name;
		String packageName;
		Drawable icon;
	}

	public BatterySipper(Context context, String pkgName, double time) {
		mContext = context;
		value = time;
		drainType = DrainType.APP;
		getQuickNameIcon(pkgName);
	}

	public BatterySipper(Context context, BatteryInfo.DrainType type, Uid uid, double[] values) {
		mContext = context;
		this.values = values;
		this.drainType = type;
		if (values != null)
			value = values[0];

		uidObj = uid;

		if (uid != null) {
			getQuickNameIconForUid(uid);
		}
	}

	public DrainType getDrainType() {
		return drainType;
	}

	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}

	public double[] getValues() {
		return values;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getPercentOfTotal() {
		return percent;
	}

	@Override
	public int compareTo(BatterySipper other) {
		return (int) (other.getValue() - getValue());
	}

	private void getQuickNameIcon(String pkgName) {
		PackageManager pm = mContext.getPackageManager();
		try {
			ApplicationInfo appInfo = pm.getApplicationInfo(pkgName, 0);
			icon = appInfo.loadIcon(pm);// pm.getApplicationIcon(appInfo);
			name = appInfo.loadLabel(pm).toString();// pm.getApplicationLabel(appInfo).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void getQuickNameIconForUid(Uid uidObj) {
		final int uid = uidObj.getUid();
		final String uidString = Integer.toString(uid);
		if (mUidCache.containsKey(uidString)) {
			UidToDetail utd = mUidCache.get(uidString);
			defaultPackageName = utd.packageName;
			name = utd.name;
			icon = utd.icon;
			return;
		}
		PackageManager pm = mContext.getPackageManager();
		String[] packages = pm.getPackagesForUid(uid);
//		icon = pm.getDefaultActivityIcon();
		if (packages == null) {
			// name = Integer.toString(uid);
			if (uid == 0) {
				drainType = DrainType.KERNEL;
				// name = mContext.getResources().getString(R.string.process_kernel_label);
			} else if ("mediaserver".equals(name)) {
				drainType = DrainType.MEDIASERVER;
				// name = mContext.getResources().getString(R.string.process_mediaserver_label);
			}
			// iconId = R.drawable.ic_power_system;
			// icon = mContext.getResources().getDrawable(iconId);
			return;
		}

		getNameIcon();
	}

	/**
	 * Sets name and icon
	 * 
	 * @param uid
	 *            Uid of the application
	 */
	private void getNameIcon() {
		PackageManager pm = mContext.getPackageManager();
		final int uid = uidObj.getUid();
		final Drawable defaultActivityIcon = pm.getDefaultActivityIcon();
		String[] packages = pm.getPackagesForUid(uid);
		if (packages == null) {
			name = Integer.toString(uid);
			return;
		}

		String[] packageLabels = new String[packages.length];
		System.arraycopy(packages, 0, packageLabels, 0, packages.length);

		// Convert package names to user-facing labels where possible
		for (int i = 0; i < packageLabels.length; i++) {
			// Check if package matches preferred package
			try {
				ApplicationInfo ai = pm.getApplicationInfo(packageLabels[i], 0);
				CharSequence label = ai.loadLabel(pm);
				if (label != null) {
					packageLabels[i] = label.toString();
				}
				if (ai.icon != 0) {
					defaultPackageName = packages[i];
					icon = ai.loadIcon(pm);
					break;
				}
			} catch (NameNotFoundException e) {
			}
		}
		if (icon == null)
			icon = defaultActivityIcon;

		if (packageLabels.length == 1) {
			name = packageLabels[0];
		} else {
			// Look for an official name for this UID.
			for (String pkgName : packages) {
				try {
					final PackageInfo pi = pm.getPackageInfo(pkgName, 0);
					if (pi.sharedUserLabel != 0) {
						final CharSequence nm = pm.getText(pkgName, pi.sharedUserLabel, pi.applicationInfo);
						if (nm != null) {
							name = nm.toString();
							if (pi.applicationInfo.icon != 0) {
								defaultPackageName = pkgName;
								icon = pi.applicationInfo.loadIcon(pm);
							}
							break;
						}
					}
				} catch (PackageManager.NameNotFoundException e) {
				}
			}
		}
		final String uidString = Integer.toString(uidObj.getUid());
		UidToDetail utd = new UidToDetail();
		utd.name = name;
		utd.icon = icon;
		utd.packageName = defaultPackageName;
		mUidCache.put(uidString, utd);
	}
}