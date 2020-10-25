package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.hunhun.myticket.RegisterOneAct.USERNAME_KEY;

public class HomeAct extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference reference;

    private LinearLayout btnTicketPisa, btnTicketMonas, btnTicketSphinx, btnTicketPagoda, btnTicketTorri, btnTicketCandi;
    private ImageView photoProfile;
    private TextView tvName, tvBio, tvBalance;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(getUsername());

        tvName = findViewById(R.id.tv_name);
        tvBio = findViewById(R.id.tv_bio);
        tvBalance = findViewById(R.id.tv_balance);
        photoProfile = findViewById(R.id.btnProfile);

        btnTicketPisa = findViewById(R.id.btnTicketPisa);
        btnTicketMonas = findViewById(R.id.btnTicketMonas);
        btnTicketSphinx = findViewById(R.id.btnTicketSphinx);
        btnTicketPagoda = findViewById(R.id.btnTicketPagoda);
        btnTicketTorri = findViewById(R.id.btnTicketTorri);
        btnTicketCandi = findViewById(R.id.btnTicketCandi);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = snapshot.child("url_photo").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);
                String bio = snapshot.child("bio").getValue(String.class);
                int balance = snapshot.child("user_balance").getValue(Integer.class);

                tvName.setText(name);
                tvBio.setText(bio);
                tvBalance.setText("US$ " + balance);
                Picasso.get().load(url).centerCrop().fit().into(photoProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        photoProfile.setOnClickListener(this);
        btnTicketCandi.setOnClickListener(this);
        btnTicketMonas.setOnClickListener(this);
        btnTicketPagoda.setOnClickListener(this);
        btnTicketTorri.setOnClickListener(this);
        btnTicketPisa.setOnClickListener(this);
        btnTicketSphinx.setOnClickListener(this);
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME_KEY, null);
        return username;
    }

    private void intentTicketDetail(String ticket){
        Intent intent = new Intent(HomeAct.this, TicketDetailAct.class);
        intent.putExtra("ticket_name", ticket);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnProfile:
                startActivity(new Intent(HomeAct.this, MyProfileAct.class));
                break;
            case R.id.btnTicketPisa:
                intentTicketDetail("Pisa");
                break;
            case R.id.btnTicketMonas:
                intentTicketDetail("Monas");
                break;
            case R.id.btnTicketSphinx:
                intentTicketDetail("Sphinx");
                break;
            case R.id.btnTicketTorri:
                intentTicketDetail("Torri");
                break;
            case R.id.btnTicketCandi:
                intentTicketDetail("Candi");
                break;
            case R.id.btnTicketPagoda:
                intentTicketDetail("Pagoda");
                break;
        }
    }
}