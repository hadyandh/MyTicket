package com.hunhun.myticket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import es.dmoral.toasty.Toasty;

public class RegisterOneAct extends AppCompatActivity {

    private LinearLayout btnBack;
    private Button btnContinue;
    private EditText edtUsername, edtPassword, edtEmail;

    final static String USERNAME_KEY = "username";
    final static String PASS_KEY = "password";
    final static String EMAIL_KEY = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);

        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        btnBack = findViewById(R.id.btnBack);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String email = edtEmail.getText().toString();

                if (TextUtils.isEmpty(username)){
                    Toasty.error(RegisterOneAct.this, "Please, enter your username", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)) {
                    Toasty.error(RegisterOneAct.this, "Please, enter your password", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(email)) {
                    Toasty.error(RegisterOneAct.this, "Please, enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sp = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(USERNAME_KEY, username);
                    editor.putString(PASS_KEY, password);
                    editor.putString(EMAIL_KEY, email);
                    editor.apply();

                    startActivity(new Intent(RegisterOneAct.this, RegisterTwoAct.class));
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}