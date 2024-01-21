package com.example.screentime;
// UsageStatsActivity.java
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UsageStatsActivity extends AppCompatActivity {

    private UsageStatsAdapter usageStatsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_stats);

        Spinner spinnerFilter = findViewById(R.id.spinnerFilter);
        TextView totalTimeTextView = findViewById(R.id.totalTimeTextView);
        ListView usageStatsListView = findViewById(R.id.usageStatsListView);

        List<UsageStats> usageStatsList = getUsageStatsList(this);
        if (usageStatsList != null) {
            usageStatsAdapter = new UsageStatsAdapter(this, R.layout.usage_stats_item, usageStatsList);
            usageStatsListView.setAdapter(usageStatsAdapter);

            // Calculate total time spent
            long totalTimeInMillis = calculateTotalTime(usageStatsList);
            String totalTimeFormatted = formatMillisToHoursMinutes(totalTimeInMillis);

            // Display total time spent
            totalTimeTextView.setText("Total Time Spent: " + totalTimeFormatted);
        } else {
            // Handle the case where there are no usage stats available
            totalTimeTextView.setText("Total Time Spent: N/A");
        }
        // Set up your UsageStatsAdapter and attach it to the ListView

        // Set up the Spinner with sorting options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        // Set a listener for Spinner item selection
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle sorting based on the selected option
                handleSorting(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }
    // Example: You can set up your UsageStatsAdapter and usageStatsListView here
    // usageStatsAdapter = new UsageStatsAdapter(...);
    // usageStatsListView.setAdapter(usageStatsAdapter);
    private void handleSorting(int selectedOption) {
        // Handle sorting based on the selected option
        if (usageStatsAdapter != null) {
            switch (selectedOption) {
                case 0: // Ascending Time
                    usageStatsAdapter.sortList(SortType.ASCENDING_TIME);
                    break;
                case 1: // Descending Time
                    usageStatsAdapter.sortList(SortType.DESCENDING_TIME);
                    break;
                case 2: // Ascending Name
                    usageStatsAdapter.sortList(SortType.ASCENDING_NAME);
                    break;
                case 3: // Descending Name
                    usageStatsAdapter.sortList(SortType.DESCENDING_NAME);
                    break;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStats> getUsageStatsList(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR, -24); // Adjust the time window as needed
        long startTime = calendar.getTimeInMillis();

        return usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
    }

    private long calculateTotalTime(List<UsageStats> usageStatsList) {
        long totalTimeInMillis = 0;

        for (UsageStats usageStats : usageStatsList) {
            totalTimeInMillis += usageStats.getTotalTimeInForeground();
        }

        return totalTimeInMillis;
    }

    private String formatMillisToHoursMinutes(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1);

        return String.format("%02d:%02d", hours, minutes);
    }
}
