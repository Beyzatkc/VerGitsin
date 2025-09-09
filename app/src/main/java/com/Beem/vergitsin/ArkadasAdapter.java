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
    private ArrayList<Kullanici> gosterilecekArkadaslar;
    private Context context;

    public ArkadasAdapter(ArrayList<Kullanici> arkadaslar,ArrayList<Kullanici> gosterilecekArkadaslar, Context contex) {
        this.arkadaslar = arkadaslar;
        this.gosterilecekArkadaslar=gosterilecekArkadaslar;
        this.context=contex;
    }
    public Kullanici getSecilenKullanici() {
        if (secilenPozisyon != -1 && secilenPozisyon < gosterilecekArkadaslar.size()) {
            return gosterilecekArkadaslar.get(secilenPozisyon);
        }
        return null;
    }
    public void filtrele(String metin) {
        gosterilecekArkadaslar.clear();
        if (metin.isEmpty()) {
            gosterilecekArkadaslar.addAll(arkadaslar);
        } else {
            for (Kullanici k : arkadaslar) {
                if (k.getKullaniciAdi().toLowerCase().contains(metin.toLowerCase())) {
                    gosterilecekArkadaslar.add(k);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArkadasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_arkadas,parent,false);
        return new ArkadasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArkadasAdapter.ViewHolder holder, int position) {
        //Kullanici kullanici=arkadaslar.get(position);
        Kullanici kullanici = gosterilecekArkadaslar.get(position);

        holder.KullaniciAdiark.setText(kullanici.getKullaniciAdi());
        int resId = context.getResources().getIdentifier(
                kullanici.getProfilFoto(), "drawable", context.getPackageName());
        holder.Profilark.setImageResource(resId);

        boolean seciliMi = position == secilenPozisyon;

        holder.secimIkonu.setImageResource(
                seciliMi ? R.drawable.baseline_radio_button_checked_24 : R.drawable.baseline_radio_button_unchecked_24
        );

        holder.secimIkonu.setOnClickListener(v -> {
            int oncekiPozisyon = secilenPozisyon;

            // Seçiliyse, kaldır. Değilse, seç.
            if (position == secilenPozisyon) {
                secilenPozisyon = -1;
                notifyItemChanged(oncekiPozisyon);
            } else {
                secilenPozisyon = position;
                notifyItemChanged(oncekiPozisyon);
                notifyItemChanged(secilenPozisyon);
            }
        });

    }

    @Override
    public int getItemCount() {
        return gosterilecekArkadaslar.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView KullaniciAdiark;
        private ImageView Profilark;
        private ImageView secimIkonu;

        public ViewHolder(View itemView) {
            super(itemView);
            KullaniciAdiark=itemView.findViewById(R.id.KullaniciAdiark);
            Profilark=itemView.findViewById(R.id.Profilark);
            secimIkonu=itemView.findViewById(R.id.secimIkonu);
        }
    }
}
