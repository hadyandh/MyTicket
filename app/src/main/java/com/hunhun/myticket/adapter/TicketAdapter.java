package com.hunhun.myticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hunhun.myticket.R;
import com.hunhun.myticket.TicketReceiptAct;
import com.hunhun.myticket.model.Ticket;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    Context context;
    ArrayList<Ticket> ticketList;

    public TicketAdapter(Context context, ArrayList<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new TicketViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ticket, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        final String ticketId = ticketList.get(position).getId();
        int amountTicket = ticketList.get(position).getJumlah_tiket();

        holder.tvTitle.setText(ticketList.get(position).getNama_wisata());
        holder.tvLocation.setText(ticketList.get(position).getLokasi());
        holder.tvAmount.setText(amountTicket+ " Tiket");

        //will be error if using only int variable when textview.setText
        // holder.tvAmount.setText(amountTicket);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TicketReceiptAct.class);
                intent.putExtra("ticket_id", ticketId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvLocation, tvAmount;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.titleTicket);
            tvLocation = itemView.findViewById(R.id.locTicket);
            tvAmount = itemView.findViewById(R.id.amountTicket);
        }
    }
}
