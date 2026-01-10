package com.teamname.freelancetaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText email = findViewById(R.id.editEmail);
        EditText password = findViewById(R.id.editPassword);
        EditText confirm = findViewById(R.id.editConfirm);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            if (!password.getText().toString().equals(confirm.getText().toString())) {
                confirm.setError("Passwords do not match");
                return;
            }

            AuthManager.login(this, email.getText().toString());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
