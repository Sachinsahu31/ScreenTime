package com.example.screentime;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionRequestActivity extends AppCompatActivity {

    private static final int USAGE_STATS_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);

        TextView textTitle = findViewById(R.id.textTitle);
        Button btnGrantPermission = findViewById(R.id.btnGrantPermission);
        Button btnCheckPermission = findViewById(R.id.btnCheckPermission); // Add this line

        btnGrantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUsageStatsPermission();
            }
        });

        // Add the following code to check permission when the new button is clicked
        btnCheckPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndProceed();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestUsageStatsPermission() {
        if (checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, navigate to UsageStatsActivity
            startActivity(new Intent(this, UsageStatsActivity.class));
            finish(); // Optional: Finish PermissionRequestActivity if needed
        } else {
            // Request permission
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, USAGE_STATS_PERMISSION_REQUEST);
        }
        Log.d("PermissionRequestActivity", "Checking and requesting permission");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAndProceed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted, navigate to UsageStatsActivity
                startActivity(new Intent(this, UsageStatsActivity.class));
                finish(); // Optional: Finish PermissionRequestActivity if needed
            } else {
                // Permission not granted, handle accordingly
                // You might show a message or take other actions
                startActivity(new Intent(this, UsageStatsActivity.class));
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("PermissionRequestActivity", "Permission result received");
        // Check if the requestCode matches the one used for requesting usage stats permission
        if (requestCode == USAGE_STATS_PERMISSION_REQUEST) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, navigate to UsageStatsActivity
                startActivity(new Intent(this, UsageStatsActivity.class));
                finish(); // Optional: Finish PermissionRequestActivity if needed
            } else {
                // Permission denied, handle accordingly
                // You might show a message or take other actions
                startActivity(new Intent(this, UsageStatsActivity.class));
                finish();
            }
        }
    }
}
