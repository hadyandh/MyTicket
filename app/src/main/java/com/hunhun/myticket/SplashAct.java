package com.hunhun.myticket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import static com.hunhun.myticket.RegisterOneAct.USERNAME_KEY;

public class SplashAct extends AppCompatActivity {

    private Animation logoAnim, textAnim;
    private ImageView appLogo;
    private TextView appSlogan;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init view
        appLogo = findViewById(R.id.appLogo);
        appSlogan = findViewById(R.id.appSlogan);

        //load animation
        logoAnim = AnimationUtils.loadAnimation(this, R.anim.scale_alpha_down_anim);
        textAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_up_anim);

        //run animation
        appLogo.startAnimation(logoAnim);
        appSlogan.startAnimation(textAnim);

        if (checkUsername() != null){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashAct.this, HomeAct.class));
                    finish();
                }
            }, 1500);
        }
        else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashAct.this, GetStartedAct.class));
                    finish();
                }
            }, 1500);
        }
    }

    public String checkUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME_KEY, null);
    }
}