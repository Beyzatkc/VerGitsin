package com.Beem.vergitsin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;

import java.util.ArrayList;

public class ArkadasAdapter extends RecyclerView.Adapter<ArkadasAdapter.ViewHolder> {
    private int secilenPozisyon = -1;

    private ArrayList<Kullanici>arkadaslar;
    private Context context;

    public ArkadasAdapter(ArrayList<Kullanici> arkadaslar, Context contex) {
        this.arkadaslar = arkadaslar;
        this.context=contex;
    }
    public Kullanici getSecilenKullanici() {
        if (secilenPozisyon != -1 && secilenPozisyon < arkadaslar.size()) {
            return arkadaslar.get(secilenPozisyon);
        }
        return null;
    }

    @NonNull
    @Override
    public ArkadasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_arkadas,parent,false);
        return new ArkadasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArkadasAdapter.ViewHolder holder, int position) {
        Kullanici kullanici=arkadaslar.get(position);
        holder.KullaniciAdiark.setText(kullanici.getKullaniciAdi());
        int resId = context.getResources().getIdentifier(
                kullanici.getProfilFoto(), "drawable", context.getPackageName());
        holder.Profilark.setImageResource(resId);

        holder.radioButton.setOnCheckedChangeListener(null);
        holder.radioButton.setChecked(position == secilenPozisyon);

        holder.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                secilenPozisyon = position;
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            secilenPozisyon = position;
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return arkadaslar.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView KullaniciAdiark;
        private ImageView Profilark;
        private RadioButton radioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            KullaniciAdiark=itemView.findViewById(R.id.KullaniciAdiark);
            Profilark=itemView.findViewById(R.id.Profilark);
            radioButton=itemView.findViewById(R.id.radioButton);
        }
    }
}
