/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.our.perfect;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.BatteryManager;

/**
 * Contains utility functions for formatting elapsed time and consumed bytes
 */
public class Utils {
	private static final int SECONDS_PER_MINUTE = 60;
	private static final int SECONDS_PER_HOUR = 60 * 60;
	private static final int SECONDS_PER_DAY = 24 * 60 * 60;

	/**
	 * Returns elapsed time for the given millis, in the following format: 2d 5h 40m 29s
	 * 
	 * @param context
	 *            the application context
	 * @param millis
	 *            the elapsed time in milli seconds
	 * @return the formatted elapsed time
	 */
	public static String formatElapsedTime(Context context, double millis) {
		StringBuilder sb = new StringBuilder();
		int seconds = (int) Math.floor(millis / 1000);

		int days = 0, hours = 0, minutes = 0;
		if (seconds > SECONDS_PER_DAY) {
			days = seconds / SECONDS_PER_DAY;
			seconds -= days * SECONDS_PER_DAY;
		}
		if (seconds > SECONDS_PER_HOUR) {
			hours = seconds / SECONDS_PER_HOUR;
			seconds -= hours * SECONDS_PER_HOUR;
		}
		if (seconds > SECONDS_PER_MINUTE) {
			minutes = seconds / SECONDS_PER_MINUTE;
			seconds -= minutes * SECONDS_PER_MINUTE;
		}
		if (days > 0) {
			sb.append(context.getString(R.string.battery_history_days, days, hours, minutes, seconds));
		} else if (hours > 0) {
			sb.append(context.getString(R.string.battery_history_hours, hours, minutes, seconds));
		} else if (minutes > 0) {
			sb.append(context.getString(R.string.battery_history_minutes, minutes, seconds));
		} else {
			sb.append(context.getString(R.string.battery_history_seconds, seconds));
		}
		return sb.toString();
	}

	/**
	 * Formats data size in KB, MB, from the given bytes.
	 * 
	 * @param context
	 *            the application context
	 * @param bytes
	 *            data size in bytes
	 * @return the formatted size such as 4.52 MB or 245 KB or 332 bytes
	 */
	public static String formatBytes(Context context, double bytes) {
		// TODO: I18N
		if (bytes > 1000 * 1000) {
			return String.format("%.2f MB", ((int) (bytes / 1000)) / 1000f);
		} else if (bytes > 1024) {
			return String.format("%.2f KB", ((int) (bytes / 10)) / 100f);
		} else {
			return String.format("%d bytes", (int) bytes);
		}
	}

	public static String getBatteryPercentage(Intent batteryChangedIntent) {
		int level = batteryChangedIntent.getIntExtra("level", 0);
		int scale = batteryChangedIntent.getIntExtra("scale", 100);
		return String.valueOf(level * 100 / scale) + "%";
	}

	public static String getBatteryStatus(Resources res, Intent batteryChangedIntent) {
		final Intent intent = batteryChangedIntent;

		int plugType = intent.getIntExtra("plugged", 0);
		int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
		String statusString;
		if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
			statusString = res.getString(R.string.battery_info_status_charging);
			if (plugType > 0) {
				statusString = statusString
						+ " "
						+ res.getString((plugType == BatteryManager.BATTERY_PLUGGED_AC) ? R.string.battery_info_status_charging_ac
								: R.string.battery_info_status_charging_usb);
			}
		} else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
			statusString = res.getString(R.string.battery_info_status_discharging);
		} else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
			statusString = res.getString(R.string.battery_info_status_not_charging);
		} else if (status == BatteryManager.BATTERY_STATUS_FULL) {
			statusString = res.getString(R.string.battery_info_status_full);
		} else {
			statusString = res.getString(R.string.battery_info_status_unknown);
		}

		return statusString;
	}
}
