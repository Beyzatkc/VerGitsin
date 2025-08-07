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

public class GrupAdapter extends RecyclerView.Adapter<GrupAdapter.ViewHolder> {
    private int secilenPozisyon = -1;

    private ArrayList<Grup>gruplar;
    private Context context;

    public GrupAdapter(ArrayList<Grup> arkadaslar, Context contex) {
        this.gruplar = arkadaslar;
        this.context=contex;
    }
    public Grup getSecilenGrup() {
        if (secilenPozisyon != -1 && secilenPozisyon < gruplar.size()) {
            return gruplar.get(secilenPozisyon);
        }
        return null;
    }

    @NonNull
    @Override
    public GrupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_grup,parent,false);
        return new GrupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrupAdapter.ViewHolder holder, int position) {
        Grup grup=gruplar.get(position);

        holder.grupAdi.setText(grup.getGrupAdi());

        boolean seciliMi = position == secilenPozisyon;

        holder.secimIkonu.setImageResource(
                seciliMi ? R.drawable.baseline_radio_button_checked_24 : R.drawable.baseline_radio_button_unchecked_24
        );

        holder.secimIkonu.setOnClickListener(v -> {
            int oncekiPozisyon = secilenPozisyon;

            // Seçiliyse, kaldır. Değilse, seç.
            if (position == secilenPozisyon) {
                secilenPozisyon = -1; // seçim kaldırıldı
                notifyItemChanged(oncekiPozisyon);
            } else {
                secilenPozisyon = position;
                notifyItemChanged(oncekiPozisyon); // önceki seçimi kaldır
                notifyItemChanged(secilenPozisyon); // yeni seçimi göster
            }
        });
    }

    @Override
    public int getItemCount() {
        return gruplar.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView grupAdi;
        private ImageView Profilgrupfoto;
        private ImageView secimIkonu;

        public ViewHolder(View itemView) {
            super(itemView);
            grupAdi=itemView.findViewById(R.id.grupAdi);
            Profilgrupfoto=itemView.findViewById(R.id.Profilgrupfoto);
            secimIkonu=itemView.findViewById(R.id.secimIkonu);
        }
    }
}
