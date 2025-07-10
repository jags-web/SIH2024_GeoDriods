package com.example.sih;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class LeaveRequestActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private EditText editTextFullName;
    private EditText editTextEmail;
    private Spinner spinnerDepartment;
    private EditText editTextManagerName;
    private EditText editTextFirstDay;
    private EditText editTextLastDay;
    private EditText editTextNotes;
    private Spinner spinnerLeaveReason;
    private Button buttonSubmit;
    private TextView textViewRequests; // Declare TextView for requests

    private ArrayList<LeaveRequest> requestsList = new ArrayList<>(); // List to store requests
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Leave Request");
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up the navigation drawer
        setupDrawer(toolbar);

        // Initialize views
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        editTextManagerName = findViewById(R.id.editTextManagerName);
        editTextFirstDay = findViewById(R.id.editTextFirstDay);
        editTextLastDay = findViewById(R.id.editTextLastDay);
        editTextNotes = findViewById(R.id.editTextNotes);
        spinnerLeaveReason = findViewById(R.id.spinnerLeaveReason);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textViewRequests = findViewById(R.id.textViewRequests); // Initialize TextView

        // Set up the spinner with leave reasons
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.leave_reasons, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLeaveReason.setAdapter(adapter);

        // Set up the spinner with department options
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.departments, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Set up date selection on EditTexts
        editTextFirstDay.setOnClickListener(v -> showDatePickerDialog(editTextFirstDay));
        editTextLastDay.setOnClickListener(v -> showDatePickerDialog(editTextLastDay));

        // Set up the submit button
        buttonSubmit.setOnClickListener(v -> submitLeaveRequest());
    }

    private void setupDrawer(Toolbar toolbar) {
        // Set up drawer toggle
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        }

        // Handle navigation item clicks using if-else
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(LeaveRequestActivity.this, MainActivity.class));
                } else if (item.getItemId() == R.id.nav_logout) {
                    Toast.makeText(LeaveRequestActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    finish(); // Optionally finish the activity
                } else {
                    return false;
                }
                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
    }

    private void showDatePickerDialog(EditText editText) {
        // Initialize calendar instance
        calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format month to be 1-based
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void submitLeaveRequest() {
        String fullName = editTextFullName.getText().toString();
        String email = editTextEmail.getText().toString();
        String department = spinnerDepartment.getSelectedItem().toString();
        String managerName = editTextManagerName.getText().toString();
        String leaveReason = spinnerLeaveReason.getSelectedItem().toString();
        String firstDay = editTextFirstDay.getText().toString();
        String lastDay = editTextLastDay.getText().toString();
        String notes = editTextNotes.getText().toString();

        // Create a new LeaveRequest object
        LeaveRequest leaveRequest = new LeaveRequest(fullName, email, department, managerName, leaveReason, firstDay, lastDay, notes);

        // Add request to the list
        requestsList.add(leaveRequest);

        // Display requests
        displayRequests();

        // Show confirmation message
        Toast.makeText(this, "Leave Request Submitted", Toast.LENGTH_SHORT).show();
    }

    private void displayRequests() {
        StringBuilder requestsText = new StringBuilder();

        for (LeaveRequest request : requestsList) {
            requestsText.append("Leave Request Submitted:\n\n");

            // Create a SpannableStringBuilder to apply different styles
            SpannableStringBuilder styledText = new SpannableStringBuilder();

            // Function to append and style text
            addStyledText(styledText, "Full Name: ", request.getFullName(), 1.25f, 1.1f); // 18sp and 16sp
            addStyledText(styledText, "Email: ", request.getEmail(), 1.25f, 1.1f);
            addStyledText(styledText, "Department: ", request.getDepartment(), 1.25f, 1.1f);
            addStyledText(styledText, "Manager/Supervisor: ", request.getManagerName(), 1.25f, 1.1f);
            addStyledText(styledText, "Reason for Leave: ", request.getLeaveReason(), 1.25f, 1.1f);
            addStyledText(styledText, "From: ", request.getFirstDay(), 1.25f, 1.1f);
            addStyledText(styledText, "To: ", request.getLastDay(), 1.25f, 1.1f);
            addStyledText(styledText, "Notes: ", request.getNotes(), 1.25f, 1.1f);
            addStyledText(styledText, "Status: ", request.getStatus(), 1.25f, 1.1f);

            // Append the styled text to the TextView
            textViewRequests.append(styledText);
        }
    }

    private void addStyledText(SpannableStringBuilder builder, String label, String value, float labelSizeMultiplier, float valueSizeMultiplier) {
        int start = builder.length();
        builder.append(label);
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(labelSizeMultiplier), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Set label size

        builder.append(value).append("\n");
        int valueStart = builder.length() - value.length() - 1; // Adjust for the appended newline
        builder.setSpan(new RelativeSizeSpan(valueSizeMultiplier), valueStart, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Set value size
    }



}
