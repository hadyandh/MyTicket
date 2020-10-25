package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hunhun.myticket.adapter.TicketAdapter;
import com.hunhun.myticket.model.Ticket;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.hunhun.myticket.RegisterOneAct.USERNAME_KEY;

public class MyProfileAct extends AppCompatActivity {

    private Button btnEdit, btnBack, btnSignOut;
    private TextView tvName, tvBio;
    private ImageView photoUser;

    private DatabaseReference userReference, ticketReference;

    private RecyclerView rvticket;
    private ArrayList<Ticket> list;
    private TicketAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        btnEdit = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        btnSignOut = findViewById(R.id.btnSignOut);
        tvName = findViewById(R.id.tv_name);
        tvBio = findViewById(R.id.tv_bio);
        photoUser = findViewById(R.id.photo_user);

        rvticket = findViewById(R.id.rv_ticket);
        rvticket.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Ticket>();

        ticketReference = FirebaseDatabase.getInstance().getReference("Purchase").child(getUsername());
        ticketReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    Ticket ticket = new Ticket();

                    ticket.setNama_wisata(dataSnapshot.child("nama_wisata").getValue(String.class));
                    ticket.setLokasi(dataSnapshot.child("lokasi").getValue(String.class));
                    ticket.setJumlah_tiket(dataSnapshot.child("jumlah_tiket").getValue(Integer.class));
                    ticket.setId(key);

                    list.add(ticket);
                }
                adapter = new TicketAdapter(MyProfileAct.this, list);
                rvticket.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference = FirebaseDatabase.getInstance().getReference("Users").child(getUsername());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String url = snapshot.child("url_photo").getValue(String.class);
                tvName.setText(snapshot.child("name").getValue(String.class));
                tvBio.setText(snapshot.child("bio").getValue(String.class));

                Picasso.get().load(url).centerCrop().fit().into(photoUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileAct.this, EditProfileAct.class));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete all data in pref
                SharedPreferences preferences = getSharedPreferences(getString(R.string.USERNAME_FILE),MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit().clear();
                editor.apply();

                Intent intent = new Intent(MyProfileAct.this, SignInAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Clear all activity
                startActivity(intent);
                finish();
            }
        });
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME_KEY, null);
    }
}