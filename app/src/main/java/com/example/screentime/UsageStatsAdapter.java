package com.example.screentime;
// UsageStatsAdapter.java
import android.app.usage.UsageStats;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.pm.PackageManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.content.pm.ApplicationInfo;

public class UsageStatsAdapter extends ArrayAdapter<UsageStats> {

    private List<UsageStats> usageStatsList;
    public UsageStatsAdapter(Context context, int resource, List<UsageStats> objects) {
        super(context, resource, objects);
        this.usageStatsList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            itemView = inflater.inflate(R.layout.usage_stats_item, parent, false);
        }

        UsageStats usageStats = getItem(position);
        if (usageStats != null) {
            TextView appNameTextView = itemView.findViewById(R.id.appNameTextView);
            TextView usageTimeTextView = itemView.findViewById(R.id.usageTimeTextView);

            appNameTextView.setText(getAppNameFromPackage(getContext(), usageStats.getPackageName()));
            usageTimeTextView.setText(formatMillisToHoursMinutes(usageStats.getTotalTimeInForeground()));
        }

        return itemView;
    }

    // Utility method to get the app name from the package name
    private String getAppNameFromPackage(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return packageName; // Return the package name if app name not found
        }
    }

    // Utility method to format milliseconds to hours and minutes
    private String formatMillisToHoursMinutes(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1);

        return String.format("%02d:%02d", hours, minutes);
    }

    //Sort the list based on different criteria
    //Sort the list based on different criteria
    public void sortList(int sortType) {
        switch (sortType) {
            case SortType.ASCENDING_TIME:
                Collections.sort(usageStatsList, new TimeComparator());
                break;
            case SortType.DESCENDING_TIME:
                Collections.sort(usageStatsList, Collections.reverseOrder(new TimeComparator()));
                break;
            case SortType.ASCENDING_NAME:
                Collections.sort(usageStatsList, new NameComparator());
                break;
            case SortType.DESCENDING_NAME:
                Collections.sort(usageStatsList, Collections.reverseOrder(new NameComparator()));
                break;
        }
        notifyDataSetChanged();
    }


    public static class TimeComparator implements Comparator<UsageStats> {
        @Override
        public int compare(UsageStats o1, UsageStats o2) {
            return Long.compare(o1.getTotalTimeInForeground(), o2.getTotalTimeInForeground());
        }
    }

    public class NameComparator implements Comparator<UsageStats> {
        @Override
        public int compare(UsageStats o1, UsageStats o2) {
            String appName1 = getAppNameFromPackage(o1.getPackageName());
            String appName2 = getAppNameFromPackage(o2.getPackageName());
            return appName1.compareToIgnoreCase(appName2);
        }

        private String getAppNameFromPackage(String packageName) {
            PackageManager packageManager = getContext().getPackageManager();
            try {
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                return packageManager.getApplicationLabel(appInfo).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return packageName; // Return the package name if app name not found
            }
        }
    }

}

