package com.xpbiomed.xppda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_id);
        passwordEditText = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authenticateUser()) {
                    // Authentication successful
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", usernameEditText.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    // Authentication failed
                    Toast.makeText(LoginActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean authenticateUser() {
        // Read user credentials from user.txt in the raw folder
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.user);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(",");
                String savedUsername = credentials[0].trim();
                String savedPassword = credentials[1].trim();

                String enteredUsername = usernameEditText.getText().toString().trim();
                String enteredPassword = passwordEditText.getText().toString().trim();

                if (savedUsername.equals(enteredUsername) && savedPassword.equals(enteredPassword)) {
                    return true; // Authentication successful
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Authentication failed
    }
}
