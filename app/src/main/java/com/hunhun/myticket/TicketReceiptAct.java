package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.hunhun.myticket.RegisterOneAct.USERNAME_KEY;

public class TicketReceiptAct extends AppCompatActivity {

    private LinearLayout btnBack;
    private Button btnRefund;
    private TextView tvTitleTicket, tvLocationTicket,tvAmountTicket, tvDateTicket, tvTimeTicket, tvInfoTicket;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_receipt);

        tvTitleTicket = findViewById(R.id.titleTicket);
        tvLocationTicket = findViewById(R.id.locTicket);
        tvAmountTicket = findViewById(R.id.amountTicket);
        tvDateTicket = findViewById(R.id.dateTicket);
        tvTimeTicket = findViewById(R.id.timeTicket);
        tvInfoTicket = findViewById(R.id.infoTicket);
        btnRefund = findViewById(R.id.btnRefund);
        btnBack = findViewById(R.id.btnBack);

        Bundle bundle = getIntent().getExtras();
        final String ticketId = bundle.getString("ticket_id");

        reference = FirebaseDatabase.getInstance().getReference("Purchase").child(getUsername()).child(ticketId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvTitleTicket.setText(snapshot.child("nama_wisata").getValue(String.class));
                tvLocationTicket.setText(snapshot.child("lokasi").getValue(String.class));
                tvAmountTicket.setText(snapshot.child("jumlah_tiket").getValue(Integer.class) + " Tiket");
                tvTimeTicket.setText(snapshot.child("time_wisata").getValue(String.class));
                tvDateTicket.setText(snapshot.child("date_wisata").getValue(String.class));
                tvInfoTicket.setText(snapshot.child("ketentuan").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME_KEY, null);
    }
}