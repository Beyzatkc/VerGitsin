package com.Beem.vergitsin.BorcAlinanlar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;

import java.util.List;

public class AlinanBorcAdapter extends RecyclerView.Adapter<AlinanBorcAdapter.BorcViewHolder> {

    private List<AlinanBorcModel> borcListesi;
    private OnBorcOdeClickListener borcOdeClickListener;

    public interface OnBorcOdeClickListener {
        void onBorcOdeClick(AlinanBorcModel borcModel, int position);
    }

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
        holder.tvMiktar.setText(borc.getMiktar()+"₺");
        holder.tvOdenecekTarih.setText("Ödeme: " + formatTarih(borc.getOdenecekTarih()));
        holder.tvZaman.setText("Eklenme: " + formatTarih(borc.getTarih()));
        holder.tvIban.setText("IBAN: " + borc.getIban());

        String alinan = "Alınan: "+borc.getAlinanAdi();
        SpannableString sp = new SpannableString(alinan);
        sp.setSpan(new StyleSpan(Typeface.BOLD),alinan.indexOf(borc.getAlinanAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#F05A22")),alinan.indexOf(borc.getAlinanAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvKimdenAlindi.setText(sp);

        if(borc.isOdendiMi()){
            holder.btnBorcuOde.setVisibility(View.GONE);
        }

        holder.btnBorcuOde.setOnClickListener(v -> {
            if (borcOdeClickListener != null) {
                borcOdeClickListener.onBorcOdeClick(borc, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return borcListesi.size();
    }

    public static class BorcViewHolder extends RecyclerView.ViewHolder {
        TextView tvAciklama, tvMiktar, tvOdenecekTarih, tvZaman, tvKimdenAlindi, tvIban;
        ImageView icon;
        Button btnBorcuOde;

        public BorcViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAciklama = itemView.findViewById(R.id.tvAciklama);
            tvMiktar = itemView.findViewById(R.id.tvMiktar);
            tvOdenecekTarih = itemView.findViewById(R.id.tvOdenecekTarih);
            tvZaman = itemView.findViewById(R.id.tvZaman);
            icon = itemView.findViewById(R.id.icon);
            btnBorcuOde = itemView.findViewById(R.id.btnBorcuOde);
            tvKimdenAlindi = itemView.findViewById(R.id.tvKimdenAlindi);
            tvIban = itemView.findViewById(R.id.tvIban);
        }
    }


    private String formatTarih(Timestamp timestamp) {
        return new java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
                .format(timestamp.toDate());
    }

    public void ItemEkle(AlinanBorcModel model){
        borcListesi.add(model);
        notifyItemInserted(borcListesi.size()-1);
    }

    public void setBorcOdeClickListener(OnBorcOdeClickListener borcOdeClickListener) {
        this.borcOdeClickListener = borcOdeClickListener;
    }
}
