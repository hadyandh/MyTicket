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

public class SuccessRegisterAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_register);

        TextView txtHeading1 = findViewById(R.id.txtHeading1);
        TextView txtHeading2 = findViewById(R.id.txtHeading2);
        ImageView imgSuccessReg = findViewById(R.id.imgSuccessReg);
        Button btnExplore = findViewById(R.id.btnExplore);

        //load animation
        Animation scaleDownAnim = AnimationUtils.loadAnimation(this, R.anim.scale_alpha_down_anim);
        Animation upAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_up_anim);
        Animation downAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_down_anim);

        //run animation
        imgSuccessReg.startAnimation(scaleDownAnim);
        txtHeading1.startAnimation(downAnim);
        txtHeading2.startAnimation(downAnim);
        btnExplore.startAnimation(upAnim);

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuccessRegisterAct.this, HomeAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Clear all activity
                startActivity(intent);
                finish();
            }
        });
    }
}