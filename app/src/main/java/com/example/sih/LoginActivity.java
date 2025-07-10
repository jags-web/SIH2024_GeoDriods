package com.example.sih;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout employeeButton;
    private LinearLayout hrButton;
    private Button loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize the views
        employeeButton = findViewById(R.id.employee_button);
        hrButton = findViewById(R.id.hr_button);
        loginButton = findViewById(R.id.login_button);
        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);

        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check which button is selected and handle login
                if (hrButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.button_selected).getConstantState())) {
                    // Navigate to HR Activity
                    Intent intent = new Intent(LoginActivity.this, HrActivity.class); // Replace HrActivity with the actual HR activity class name
                    startActivity(intent);
                } else if (employeeButton.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.button_selected).getConstantState())) {
                    // Navigate to Employee Activity (if you have it)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Replace EmployeeActivity with the actual Employee activity class name
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Please select an account type", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for HR button
        hrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Highlight HR button
                hrButton.setBackgroundResource(R.drawable.button_selected);
                employeeButton.setBackgroundResource(R.drawable.button_unselected);
            }
        });

        // Set click listener for Employee button
        employeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Highlight Employee button
                employeeButton.setBackgroundResource(R.drawable.button_selected);
                hrButton.setBackgroundResource(R.drawable.button_unselected);
            }
        });
    }
}
