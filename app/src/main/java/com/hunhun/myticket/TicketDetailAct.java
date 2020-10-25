package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TicketDetailAct extends AppCompatActivity {

    private LinearLayout btnBack;
    private Button btnBuy;
    private TextView titleTicket, locationTicket, photoSpotTicket, wifiTicket, festTicket, descTicket;
    private ImageView thumbnailTicket;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        Bundle bundle = getIntent().getExtras();
        final String ticketName = bundle.getString("ticket_name");

        btnBuy = findViewById(R.id.btnBuy);
        btnBack = findViewById(R.id.btnBack);

        titleTicket = findViewById(R.id.titleTicket);
        photoSpotTicket = findViewById(R.id.photoSpotTicket);
        wifiTicket = findViewById(R.id.wifiTicket);
        locationTicket = findViewById(R.id.locTicket);
        festTicket = findViewById(R.id.festTicket);
        descTicket = findViewById(R.id.descTicket);
        thumbnailTicket = findViewById(R.id.thumbnailTicket);

        reference = FirebaseDatabase.getInstance().getReference("Wisata").child(ticketName);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String urlThumbnail = snapshot.child("url_thumbnail").getValue(String.class);

                titleTicket.setText(snapshot.child("nama_wisata").getValue(String.class));
                locationTicket.setText(snapshot.child("lokasi").getValue(String.class));
                wifiTicket.setText(snapshot.child("is_wifi").getValue(String.class));
                festTicket.setText(snapshot.child("is_festival").getValue(String.class));
                photoSpotTicket.setText(snapshot.child("is_photo_spot").getValue(String.class));
                descTicket.setText(snapshot.child("short_desc").getValue(String.class));

                Picasso.get().load(urlThumbnail).centerCrop().fit().into(thumbnailTicket);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketDetailAct.this, TicketCheckoutAct.class);
                intent.putExtra("ticket_name", ticketName);
                startActivity(intent);
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