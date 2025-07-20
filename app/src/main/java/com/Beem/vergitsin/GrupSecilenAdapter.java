package com.Beem.vergitsin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;

import java.util.ArrayList;

public class GrupSecilenAdapter extends RecyclerView.Adapter<GrupSecilenAdapter.ViewHolder> {
    private ArrayList<Kullanici> kullanicilar;
    private ArrayList<Kullanici> secilenler;
    private Context context;

    public GrupSecilenAdapter(ArrayList<Kullanici> kullanicilar, ArrayList<Kullanici> secilenler, Context context) {
        this.kullanicilar = kullanicilar;
        this.secilenler = secilenler;
        this.context=context;
    }
    @NonNull
    @Override
    public GrupSecilenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_arkadasgrup,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupSecilenAdapter.ViewHolder holder, int position) {
        Kullanici kullanici=kullanicilar.get(position);
        holder.KullaniciAdigrp.setText(kullanici.getKullaniciAdi());
        int resId = context.getResources().getIdentifier(
                kullanici.getProfilFoto(), "drawable", context.getPackageName());
        holder.Profilgrp.setImageResource(resId);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(secilenler.contains(kullanici));// Eğer k zaten secilenler listesinde varsa, bu CheckBox işaretli olur degilse isaretsiz
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!secilenler.contains(kullanici)) secilenler.add(kullanici);
            } else {
                secilenler.remove(kullanici);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kullanicilar.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView KullaniciAdigrp;
        ImageView Profilgrp;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxKullanici);
            KullaniciAdigrp=itemView.findViewById(R.id.KullaniciAdigrp);
            Profilgrp=itemView.findViewById(R.id.Profilgrp);
        }
    }
}
