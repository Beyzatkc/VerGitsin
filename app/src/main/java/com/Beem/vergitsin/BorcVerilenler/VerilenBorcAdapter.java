package com.Beem.vergitsin.BorcVerilenler;

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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VerilenBorcAdapter extends RecyclerView.Adapter<VerilenBorcAdapter.BorcViewHolder> {

    private List<VerilenBorcModel> borcListesi;
    private OnHatirlatClickListener hatirlatClickListener;

    public interface OnHatirlatClickListener {
        void onHatirlatClick(VerilenBorcModel borcModel, int position);
        void onProfileGec(String id);
    }

    public VerilenBorcAdapter(List<VerilenBorcModel> borcListesi) {
        this.borcListesi = borcListesi;
    }

    @NonNull
    @Override
    public BorcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.verilen_borc_item, parent, false);
        return new BorcViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BorcViewHolder holder, int position) {
        VerilenBorcModel borc = borcListesi.get(position);

        holder.tvAciklama.setText(borc.getAciklama());
        holder.tvMiktar.setText(borc.getMiktar() + "₺");
        holder.tvOdemeTarihi.setText("Geri Ödeme: " + formatTarih(borc.getOdenecekTarih()));
        holder.tvZaman.setText("Eklenme: " + formatTarih(borc.getTarih()));
        holder.tvIban.setText("IBAN: " + borc.getIban());

        String alinan = "Verilen: "+borc.getVerilenAdi();
        SpannableString sp = new SpannableString(alinan);
        sp.setSpan(new StyleSpan(Typeface.BOLD),alinan.indexOf(borc.getVerilenAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#F05A22")),alinan.indexOf(borc.getVerilenAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvKimeVerildi.setText(sp);

        if (borc.isOdendiMi()) {
            holder.btnHatirlat.setVisibility(View.GONE);
        }

        holder.btnHatirlat.setOnClickListener(v -> {
            if (hatirlatClickListener != null) {
                hatirlatClickListener.onHatirlatClick(borc, position);
            }
        });
        holder.tvKimeVerildi.setOnClickListener(v->{
            if(hatirlatClickListener!=null){
                hatirlatClickListener.onProfileGec(borc.getKullaniciId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return borcListesi.size();
    }

    public static class BorcViewHolder extends RecyclerView.ViewHolder {
        TextView tvAciklama, tvMiktar, tvOdemeTarihi, tvZaman, tvKimeVerildi, tvIban;
        ImageView icon;
        Button btnHatirlat;

        public BorcViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAciklama = itemView.findViewById(R.id.tvAciklama);
            tvMiktar = itemView.findViewById(R.id.tvMiktar);
            tvOdemeTarihi = itemView.findViewById(R.id.tvOdemeTarihi);
            tvZaman = itemView.findViewById(R.id.tvZaman);
            tvKimeVerildi = itemView.findViewById(R.id.tvKimeVerildi);
            tvIban = itemView.findViewById(R.id.tvIban);
            icon = itemView.findViewById(R.id.icon);
            btnHatirlat = itemView.findViewById(R.id.btnHatirlat);
        }
    }

    private String formatTarih(Timestamp timestamp) {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(timestamp.toDate());
    }

    public void itemEkle(VerilenBorcModel model) {
        borcListesi.add(model);
        notifyItemInserted(borcListesi.size() - 1);
    }

    public void setHatirlatClickListener(OnHatirlatClickListener listener) {
        this.hatirlatClickListener = listener;
    }
}
