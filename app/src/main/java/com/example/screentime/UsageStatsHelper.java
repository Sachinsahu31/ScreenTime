package com.example.screentime;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class UsageStatsHelper {

    private static final int USAGE_STATS_PERMISSION_REQUEST = 1001;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isUsageStatsPermissionGranted(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void requestUsageStatsPermission(Context context) {
        context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR, -1); // You can adjust the time window as needed
        long startTime = calendar.getTimeInMillis();

        return usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, startTime, endTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<String> getTopApps(Context context, int limit) {
        if (!isUsageStatsPermissionGranted(context)) {
            // Request permission if not granted
            requestUsageStatsPermission(context);
            return new ArrayList<>();
        }

        List<UsageStats> usageStatsList = getUsageStatsList(context);

        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            // Sort the usageStatsList by total time in descending order
            usageStatsList.sort(new Comparator<UsageStats>() {
                @Override
                public int compare(UsageStats stats1, UsageStats stats2) {
                    return Long.compare(stats2.getTotalTimeInForeground(), stats1.getTotalTimeInForeground());
                }
            });

            // Get the top apps up to the specified limit
            List<String> topApps = new ArrayList<>();
            for (int i = 0; i < Math.min(limit, usageStatsList.size()); i++) {
                String appName = getAppName(context, usageStatsList.get(i).getPackageName());
                topApps.add(appName);
            }

            return topApps;
        }

        return new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String getAppName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName; // Return the package name if the app name is not found
    }
}
