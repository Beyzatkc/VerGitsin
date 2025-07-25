package com.Beem.vergitsin.BorcAlinanlar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.R;
import java.util.List;

public class AlinanBorcAdapter extends RecyclerView.Adapter<AlinanBorcAdapter.BorcViewHolder> {

    private List<AlinanBorcModel> borcListesi;

    public AlinanBorcAdapter(List<AlinanBorcModel> borcListesi) {
        this.borcListesi = borcListesi;
    }

    @NonNull
    @Override
    public BorcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.borc_item, parent, false);
        return new BorcViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BorcViewHolder holder, int position) {
        AlinanBorcModel borc = borcListesi.get(position);
        holder.tvAciklama.setText(borc.getAciklama());
        holder.tvMiktar.setText(borc.getMiktar());
        holder.tvOdenecekTarih.setText("Ödeme: " + borc.getOdenecekTarih());
        holder.tvZaman.setText("Eklenme: " + borc.getZaman());
        // Eğer ikon dinamik olacaksa burada eklenebilir
    }

    @Override
    public int getItemCount() {
        return borcListesi.size();
    }

    public static class BorcViewHolder extends RecyclerView.ViewHolder {
        TextView tvAciklama, tvMiktar, tvOdenecekTarih, tvZaman;
        ImageView icon;

        public BorcViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAciklama = itemView.findViewById(R.id.tvAciklama);
            tvMiktar = itemView.findViewById(R.id.tvMiktar);
            tvOdenecekTarih = itemView.findViewById(R.id.tvOdenecekTarih);
            tvZaman = itemView.findViewById(R.id.tvZaman);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
