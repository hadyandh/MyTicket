package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class SignInAct extends AppCompatActivity {

    private TextView btnNewAccount;
    private Button btnSignIn;
    private EditText edtUsername, edtPassword;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnNewAccount = findViewById(R.id.btnNewAccount);
        btnSignIn = findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginValidation();
            }
        });

        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInAct.this, RegisterOneAct.class));
            }
        });
    }

    private void loginValidation() {
        final String username = edtUsername.getText().toString();
        final String password = edtPassword.getText().toString();

        //check field edit text
        if (TextUtils.isEmpty(username)){
            Toasty.error(SignInAct.this, "Please, enter your username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toasty.error(SignInAct.this, "Please, enter your password", Toast.LENGTH_SHORT).show();
        }
        else {

            btnSignInLoading(true);

            reference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){

                        String passwordFromFirebase = snapshot.child("password").getValue(String.class);

                        if (password.equals(passwordFromFirebase)){
                            //simpan username pada local
                            SharedPreferences sp = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(getString(R.string.USERNAME_KEY), username);
                            editor.apply();

                            //move activity
                            Intent intent = new Intent(SignInAct.this, HomeAct.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Clear all activity
                            startActivity(intent);
                            finish();

                        } else {
                            Toasty.error(SignInAct.this, "Sorry wrong password, please try again", Toast.LENGTH_SHORT).show();
                            btnSignInLoading(false);
                        }
                    } else {
                        Toasty.error(SignInAct.this, "Username not found", Toast.LENGTH_SHORT).show();
                        btnSignInLoading(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void btnSignInLoading(Boolean loading){
        if (loading){
            btnSignIn.setEnabled(false);
            btnSignIn.setText("Loading...");
        } else {
            btnSignIn.setEnabled(true);
            btnSignIn.setText("SIGN IN");
        }
    }
}