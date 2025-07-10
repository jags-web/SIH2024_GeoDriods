package com.example.sih;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;
import android.provider.MediaStore;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.view.MenuItem;
import android.content.SharedPreferences;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;
import java.util.List;


public class DailyLogsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView dateTextView;

    // Define hidden layout sections
    private LinearLayout meetingDetails1;
    private LinearLayout meetingDetails2;

    private PieChart pieChart;
    private LineChart lineChart;
    private BarChart barChart;

    // Define an ActivityResultLauncher for requesting permissions
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted, proceed with camera action
                    dispatchTakePictureIntent();
                } else {
                    // Permission is denied, show a message to the user
                    Toast.makeText(this, "Camera permission is required to use this feature.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_logs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Analytics");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        dateTextView = findViewById(R.id.dateTextView);  // Reference to Day/Date TextView

        // Initialize meeting detail sections
        meetingDetails1 = findViewById(R.id.meeting_details);
        meetingDetails2 = findViewById(R.id.site_inspection_details);

        pieChart = findViewById(R.id.pieChart);
        setupPieChart();
        lineChart = findViewById(R.id.lineChart); // Initialize the lineChart variable with the correct ID
        setupLineChart();
//        barChart = findViewById(R.id.barChart);
//        setupBarChart();

        // Set up the navigation icon
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(navigationView);
                }
            });
        }

        // Update with the current date or the saved date
        updateDateOrLoadSavedDate();

        // Set up Date Picker for the Day/Date TextView
        dateTextView.setOnClickListener(v -> openDatePicker());

        // Set up navigation item selection listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Start MainActivity when Home is clicked
                Intent intent = new Intent(DailyLogsActivity.this, MainActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(navigationView);
                return true;
            } else if (id == R.id.nav_capture) {
                // Handle capture item click
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is already granted, proceed with camera action
                    dispatchTakePictureIntent();
                } else {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
                return true;
            } else if (id == R.id.nav_logout) {
                // Handle logout item click
                return true;
            }
            return false;
        });

        // Set up expand/collapse functionality for meeting details
        setupExpandCollapse(findViewById(R.id.down_arrow_1), meetingDetails1);
        setupExpandCollapse(findViewById(R.id.down_arrow_2), meetingDetails2);
    }

    // Method to open DatePickerDialog
    private void openDatePicker() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.show();
    }

    // Handle DatePickerDialog date selection
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String dayString = dayFormat.format(calendar.getTime());
        String dateString = dateFormat.format(calendar.getTime());

        // Display day and date only
        String selectedDate = dayString + " " + dateString;
        dateTextView.setText(selectedDate);

        // Save the new selected date
        saveSelectedDate(selectedDate);
    }

    // Method to save the selected date
    private void saveSelectedDate(String date) {
        SharedPreferences sharedPreferences = getSharedPreferences("DatePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedDate", date);
        editor.apply();
    }

    // Method to check if the saved date is different from the current date
    private void updateDateOrLoadSavedDate() {
        SharedPreferences sharedPreferences = getSharedPreferences("DatePreferences", MODE_PRIVATE);
        String savedDate = sharedPreferences.getString("selectedDate", null);

        // Get current date in the same format
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dayFormat.format(calendar.getTime()) + " " + dateFormat.format(calendar.getTime());

        // Set the dateTextView to saved date if available, otherwise use current date
        dateTextView.setText(savedDate != null ? savedDate : currentDate);
    }

    // Dispatch intent to open camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Handle results from the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle image capture result here if needed
    }

    private void setupExpandCollapse(ImageView arrow, LinearLayout details) {
        arrow.setOnClickListener(v -> {
            if (details.getVisibility() == View.GONE) {
                details.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_up_arrow); // Change to an up arrow icon
            } else {
                details.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_down_arrow); // Change back to a down arrow icon
            }
        });
    }
    private void setupPieChart() {
        // Create a list of PieEntries representing each leave type and count
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(5, "Sick Leave"));  // Example data, replace with actual values
        entries.add(new PieEntry(3, "Casual Leave"));  // Example data, replace with actual values
        entries.add(new PieEntry(7, "Privileged Leave"));  // Example data, replace with actual values

        // Create a PieDataSet from the entries and set its properties
        PieDataSet dataSet = new PieDataSet(entries, "");

        int[] customColors = {
                Color.rgb(167, 167, 255),  // Green for Sick Leave
                Color.rgb(255, 167, 167),  // Orange for Casual Leave
                Color.rgb(167, 255, 167)   // Blue for Privileged Leave
            };

        dataSet.setColors(customColors);
        dataSet.setValueTextColor(Color.BLACK);  // Set value text color
        dataSet.setValueTextSize(16f);  // Set value text size

        // Create PieData from the data set and set it to the PieChart
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();  // Refresh the chart

        // Customize the PieChart appearance
        pieChart.getDescription().setEnabled(false);  // Disable description text
        pieChart.setHoleRadius(40f);  // Set the hole radius in the center
        pieChart.setTransparentCircleRadius(45f);  // Set the transparent circle radius
        pieChart.setCenterText("Leave Distribution");  // Set the center text
        pieChart.setCenterTextSize(14f);  // Set the center text size
        pieChart.setCenterTextColor(Color.BLACK);  // Set the center text color
        pieChart.setDrawEntryLabels(false);  // Disable labels outside slices
        pieChart.animateY(1000);  // Animate the chart vertically for 1 second

        // Customize the legend
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);  // Align legend vertically to the center
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);  // Align legend to the right
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);  // Set legend orientation to vertical
        legend.setDrawInside(false);  // Draw legend outside of the chart
        legend.setTextSize(14f);  // Set text size for legend
        legend.setTextColor(Color.BLACK);  // Set text color for legend
        legend.setXEntrySpace(10f);  // Set space between legend entries horizontally
        legend.setYEntrySpace(5f);  // Set space between legend entries vertically

        // Create a custom label array to include "Leave Types" at the top
        List<LegendEntry> legendEntries = new ArrayList<>();
        legendEntries.add(new LegendEntry("Leave Types", Legend.LegendForm.NONE, Float.NaN, Float.NaN, null, Color.BLACK));
        legendEntries.add(new LegendEntry("Sick Leave", Legend.LegendForm.SQUARE, 10f, 2f, null, dataSet.getColor(0)));
        legendEntries.add(new LegendEntry("Casual Leave", Legend.LegendForm.SQUARE, 10f, 2f, null, dataSet.getColor(1)));
        legendEntries.add(new LegendEntry("Privileged Leave", Legend.LegendForm.SQUARE, 10f, 2f, null, dataSet.getColor(2)));

        // Set the custom labels to the legend
        legend.setCustom(legendEntries);
    }

    private void setupLineChart() {
        // Create a list of entries for your line chart
        List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, 8));  // Monday, 8 hours worked
        entries.add(new Entry(1, 7));  // Tuesday, 7 hours worked
        entries.add(new Entry(2, 9));  // Wednesday, 9 hours worked
        entries.add(new Entry(3, 6));  // Thursday, 6 hours worked
        entries.add(new Entry(4, 8));  // Friday, 8 hours worked
        entries.add(new Entry(5, 5));  // Saturday, 5 hours worked

        // Calculate weekly hours
        int totalWeeklyHours = calculateWeeklyHours(entries);

        // Update the TextView to display the total weekly hours
        TextView weeklyHoursTextView = findViewById(R.id.weeklyHoursTextView);
        weeklyHoursTextView.setText("Total Weekly Hours: " + totalWeeklyHours);

        LineDataSet lineDataSet = new LineDataSet(entries, "Daily Hours Worked");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(lineDataSet);

        // Set data to line chart
        lineChart.setData(lineData);

        // Customize x-axis labels
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);  // Set minimum interval between axis labels
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                switch ((int) value) {
                    case 0: return "Mon";
                    case 1: return "Tue";
                    case 2: return "Wed";
                    case 3: return "Thu";
                    case 4: return "Fri";
                    case 5: return "Sat";
                    case 6: return "Sun";
                    default: return "";
                }
            }
        });

        xAxis.setLabelCount(7, true);  // Set the number of labels to display

        lineChart.invalidate();  // Refresh the chart
    }


    //    private void setupBarChart() {
//        // Sample data: replace with actual values
//        List<BarEntry> barEntries = new ArrayList<>();
//        barEntries.add(new BarEntry(1, new float[]{5, 2, 1}));  // Day 1: 5 On-time, 2 Late, 1 Early
//        barEntries.add(new BarEntry(2, new float[]{4, 1, 0}));  // Day 2: 4 On-time, 1 Late, 0 Early
//        barEntries.add(new BarEntry(3, new float[]{6, 0, 2}));  // Day 3: 6 On-time, 0 Late, 2 Early
//        barEntries.add(new BarEntry(4, new float[]{7, 3, 1}));  // Day 4: 7 On-time, 3 Late, 1 Early
//        // ... add more entries for each day
//
//        // Create a BarDataSet and set its properties
//        BarDataSet barDataSet = new BarDataSet(barEntries, "Attendance Behavior");
//        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);  // Use predefined color template
//        barDataSet.setStackLabels(new String[]{"On-time", "Late", "Early"});  // Label for each stack
//
//        // Create BarData and set it to the BarChart
//        BarData barData = new BarData(barDataSet);
//        barChart.setData(barData);
//        barChart.invalidate();  // Refresh the chart
//
//        // Customize the BarChart appearance
//        barChart.getDescription().setEnabled(false);  // Disable description text
//        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  // X-axis at the bottom
//        barChart.getXAxis().setGranularity(1f);  // One value per increment on the x-axis
//        barChart.getXAxis().setAxisMinimum(1);  // Start at day 1
//        barChart.getAxisLeft().setAxisMinimum(0f);  // Start y-axis at 0
//        barChart.getAxisRight().setEnabled(false);  // Disable right y-axis
//        barChart.setFitBars(true);  // Make bars fit within the chart
//        barChart.setHighlightFullBarEnabled(false);  // Disable highlight across full bar
//        barChart.animateY(1000);  // Animate vertically for 1 second
//    }
private int calculateWeeklyHours(List<Entry> entries) {
    int totalHours = 0;
    for (Entry entry : entries) {
        totalHours += (int) entry.getY();  // Sum the hours worked each day
    }
    return totalHours;
}

}
