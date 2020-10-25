package com.hunhun.myticket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GetStartedAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        //init view
        ImageView appEmblem = findViewById(R.id.appEmblem);
        TextView appText = findViewById(R.id.appText);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnNewAccount = findViewById(R.id.btnNewAccount);

        //load animation
        Animation upAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_up_anim);
        Animation downAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_down_anim);

        //run animation
        appEmblem.startAnimation(downAnim);
        appText.startAnimation(downAnim);

        btnSignIn.startAnimation(upAnim);
        btnNewAccount.startAnimation(upAnim);

        //event
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GetStartedAct.this, SignInAct.class));
            }
        });

        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GetStartedAct.this, RegisterOneAct.class));
            }
        });
    }
}