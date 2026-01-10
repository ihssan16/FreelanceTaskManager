package com.teamname.freelancetaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthManager.isLoggedIn(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.editEmail);
        EditText password = findViewById(R.id.editPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()) {
                email.setError("Required");
                return;
            }

            AuthManager.login(this, email.getText().toString());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        findViewById(R.id.txtRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}

