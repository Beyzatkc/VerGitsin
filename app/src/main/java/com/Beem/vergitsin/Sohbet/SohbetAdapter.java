package com.Beem.vergitsin.Sohbet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Mesaj.MesajFragment;
import com.Beem.vergitsin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class SohbetAdapter extends RecyclerView.Adapter<SohbetAdapter.ViewHolder> {
    private ArrayList<Sohbet>sohbetler;
    private Context context;
    private Fragment fragment;

    public SohbetAdapter(Fragment anaFragment,ArrayList<Sohbet> sohbetler, Context context) {
        this.fragment=anaFragment;
        this.sohbetler = sohbetler;
        this.context = context;
    }
    @NonNull
    @Override
    public SohbetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_sohbet,parent,false);
        return new SohbetAdapter.ViewHolder(view);
    }
    public String longToSaatDakika(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(timeMillis);
        return sdf.format(date);
    }
    @Override
    public void onBindViewHolder(@NonNull SohbetAdapter.ViewHolder holder, int position) {
        Sohbet sohbet=sohbetler.get(position);
        holder.kisi_adi.setText(sohbet.getKullaniciAdi());
        if(sohbet.getPpfoto()!=null) {
            int resId = context.getResources().getIdentifier(
                    sohbet.getPpfoto(), "drawable", context.getPackageName());
            holder.kisi_fotosu.setImageResource(resId);
        }else {
            int resId = context.getResources().getIdentifier(
                    "user", "drawable", context.getPackageName());
            holder.kisi_fotosu.setImageResource(resId);
        }
        holder.son_mesaj.setText(sohbet.getSonMesaj());
        if(sohbet.getSonmsjsaati()!=null) {
            holder.mesaj_saat.setVisibility(View.VISIBLE);
            String zaman=longToSaatDakika(sohbet.getSonmsjsaati());
            holder.mesaj_saat.setText(zaman);
        }else{
            holder.mesaj_saat.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("kaynak", "SohbetAdapter");
            bundle.putString("sohbetId", sohbet.getSohbetID());

            Fragment mesajFragment = new MesajFragment();
            mesajFragment.setArguments(bundle);
            fragment.getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.konteynir, mesajFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
    @Override
    public int getItemCount() {
        return sohbetler.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
       private TextView kisi_adi;
       private TextView son_mesaj;
       private TextView mesaj_saat;
       private ImageView kisi_fotosu;

        public ViewHolder(View itemView) {
            super(itemView);
            kisi_adi=itemView.findViewById(R.id.kisi_adi);
            son_mesaj=itemView.findViewById(R.id.son_mesaj);
            mesaj_saat=itemView.findViewById(R.id.mesaj_saat);
            kisi_fotosu=itemView.findViewById(R.id.kisi_fotosu);
        }
    }
}
