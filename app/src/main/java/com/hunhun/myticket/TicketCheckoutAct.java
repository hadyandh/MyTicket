package com.hunhun.myticket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hunhun.myticket.model.Ticket;

import java.util.Random;

import es.dmoral.toasty.Toasty;

import static com.hunhun.myticket.RegisterOneAct.USERNAME_KEY;

public class TicketCheckoutAct extends AppCompatActivity {

    private LinearLayout btnBack;
    private Button btnPay, btnPlus, btnMin;
    private TextView tvMyBalance, tvTotal, tvAmountTicket, tvTitleTicket, tvLocationTicket, tvInfoTicket;
    private ImageView warningBalance;

    private Integer amountTicket = 1;
    private Integer randomNumber = new Random().nextInt();
    private Integer myBalance, totalPrice, priceTicket;
    private String ticketName, locationTicket, infoTicket, dateTicket, timeTicket;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_checkout);

        Bundle bundle = getIntent().getExtras();
        ticketName = bundle.getString("ticket_name");

        reference = FirebaseDatabase.getInstance().getReference();

        tvAmountTicket = findViewById(R.id.txtAmountTicket);
        tvMyBalance = findViewById(R.id.txtCurrentBalance);
        tvTotal = findViewById(R.id.txtTotal);
        tvLocationTicket = findViewById(R.id.locTicket);
        tvTitleTicket = findViewById(R.id.titleTicket);
        tvInfoTicket = findViewById(R.id.infoTicket);
        warningBalance = findViewById(R.id.warning_balance);
        btnPlus = findViewById(R.id.btnPlus);
        btnMin = findViewById(R.id.btnMin);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);

        loadDataFirebase();
        tvAmountTicket.setText(String.valueOf(amountTicket));

        warningBalance.setVisibility(View.GONE);

        //set default hide button mines
        btnMin.animate().alpha(0).setDuration(100).start();
        btnMin.setEnabled(false);

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountTicket+=1;
                tvAmountTicket.setText(String.valueOf(amountTicket));

                totalPrice = priceTicket * amountTicket;
                tvTotal.setText("US$ " + totalPrice);

                //show and enable button min when amount of ticket is more than one
                if (amountTicket > 1) {
                    btnMin.animate().alpha(1).setDuration(100).start();
                    btnMin.setEnabled(true);
                }

                if (totalPrice > myBalance){
                    btnPay.animate().translationY(200).alpha(0).setDuration(300).start();
                    btnPay.setEnabled(false);
                    warningBalance.setVisibility(View.VISIBLE);
                    tvMyBalance.setTextColor(Color.RED);
                }
            }
        });

        btnMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountTicket-=1;
                tvAmountTicket.setText(String.valueOf(amountTicket));

                totalPrice = priceTicket * amountTicket;
                tvTotal.setText("US$ " + totalPrice);

                //hide and disable button min when amount of ticket is less than two
                if (amountTicket < 2) {
                    btnMin.animate().alpha(0).setDuration(100).start();
                    btnMin.setEnabled(false);
                }

                if (totalPrice <= myBalance){
                    btnPay.animate().translationY(0).alpha(1).setDuration(300).start();
                    btnPay.setEnabled(true);
                    warningBalance.setVisibility(View.GONE);
                    tvMyBalance.setTextColor(getResources().getColor(R.color.bluePrimary));
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeDataFirebase();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadDataFirebase(){
        //load data wisata
        reference.child("Wisata").child(ticketName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                priceTicket = snapshot.child("harga_tiket").getValue(Integer.class);
                locationTicket = snapshot.child("lokasi").getValue(String.class);
                infoTicket = snapshot.child("ketentuan").getValue(String.class);
                dateTicket = snapshot.child("date_wisata").getValue(String.class);
                timeTicket = snapshot.child("time_wisata").getValue(String.class);

                totalPrice = priceTicket * amountTicket;

                tvTitleTicket.setText(ticketName);
                tvLocationTicket.setText(locationTicket);
                tvInfoTicket.setText(infoTicket);
                tvTotal.setText("US$ " + totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //load data user
        reference.child("Users").child(getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myBalance = snapshot.child("user_balance").getValue(Integer.class);
                tvMyBalance.setText("US$ " + myBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void storeDataFirebase(){
        Ticket purchase = new Ticket(ticketName, locationTicket, infoTicket, dateTicket, timeTicket, priceTicket, amountTicket, totalPrice);
        reference.child("Purchase").child(getUsername()).child(ticketName + randomNumber).setValue(purchase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update user balance after buy ticket
                        int updateUserBalance = myBalance - totalPrice;
                        reference.child("Users").child(getUsername()).child("user_balance").setValue(updateUserBalance).isSuccessful();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(TicketCheckoutAct.this, SuccessBuyTicketAct.class));
                            finish();
                        } else {
                            Toasty.error(TicketCheckoutAct.this, "Sorry, there's something wrong. Please try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(TicketCheckoutAct.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public String getUsername(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USERNAME_FILE), MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME_KEY, null);
    }
}