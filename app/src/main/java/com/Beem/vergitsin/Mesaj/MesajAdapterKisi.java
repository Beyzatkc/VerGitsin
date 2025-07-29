package com.Beem.vergitsin.Mesaj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MesajAdapterKisi extends RecyclerView.Adapter<MesajAdapterKisi.ViewHolder>{
    private ArrayList<Mesaj> tumMesajlar;
    private Context context;
    private CevapGeldi listenercvp;

    public MesajAdapterKisi(ArrayList<Mesaj>tumMesajlar, Context context) {
        this.tumMesajlar=tumMesajlar;
        this.context=context;
    }
    public void mesajEkle(Mesaj yeniMesaj) {
        tumMesajlar.add(yeniMesaj);
        notifyItemInserted(tumMesajlar.size() - 1);
    }

    public void mesajGuncelle(Mesaj mesaj){
        for (int i = 0; i < tumMesajlar.size(); i++) {
            if (tumMesajlar.get(i).getMsjID().equals(mesaj.getMsjID())) {
                tumMesajlar.set(i, mesaj);
                notifyDataSetChanged();
                break;
            }
        }
    }
    public void eskiMesajlariBasaEkle(ArrayList<Mesaj> mesajlar) {
        tumMesajlar.addAll(0, mesajlar);
        notifyItemRangeInserted(0, mesajlar.size());
    }

    public void setListenercvp(CevapGeldi listener) {
        this.listenercvp = listener;
    }

    public void guncelleMesajListesi(ArrayList<Mesaj> yeniListe) {
            tumMesajlar.clear();
            tumMesajlar.addAll(yeniListe);
            notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_mesaj_kisi, parent, false);
        return new ViewHolder(view);
    }
    public String timestampToGunAyYil(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
    public String longToSaatDakika(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(timeMillis);
        return sdf.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Mesaj mesaj=tumMesajlar.get(position);
       String benim= MainActivity.kullanicistatic.getKullaniciId();

       if(mesaj.getIstegiAtanId().equals(benim)){
           holder.gelenLayout.setVisibility(View.GONE);
           holder.gidenLayout.setVisibility(View.VISIBLE);
           holder.txtMiktarGiden.setText(mesaj.getMiktar());
           holder.txtAciklamaGiden.setText(mesaj.getAciklama());
           Timestamp odemeTarihi = mesaj.getOdenecekTarih();
           String tarihStr = timestampToGunAyYil(odemeTarihi);
           holder.txtTarihGiden.setText(tarihStr);

           Long MesajSaati=mesaj.getZaman();
           String zaman=longToSaatDakika(MesajSaati);
           holder.gidenSaat.setText(zaman);
           if(mesaj.isCevabiVarMi()){
               holder.onaygiden.setVisibility(View.VISIBLE);
               holder.onaygiden.setText(mesaj.getCevap());
           }else{
               holder.onaygiden.setVisibility(View.GONE);
           }
           if(tumMesajlar.get(tumMesajlar.size()-1).equals(mesaj)&&mesaj.isGoruldu()){
               holder.gorulduDurumu.setVisibility(View.VISIBLE);
           }else{
               holder.gorulduDurumu.setVisibility(View.GONE);
           }
       }else{
           holder.gelenLayout.setVisibility(View.VISIBLE);
           holder.gidenLayout.setVisibility(View.GONE);
           holder.txtMiktarGelen.setText(mesaj.getMiktar());
           holder.txtAciklamaGelen.setText(mesaj.getAciklama());
           Timestamp odemeTarihi = mesaj.getOdenecekTarih();
           String tarihStr = timestampToGunAyYil(odemeTarihi);
           holder.txtTarihGelen.setText(tarihStr);

           Long MesajSaati=mesaj.getZaman();
           String zaman=longToSaatDakika(MesajSaati);
           holder.gelenSaat.setText(zaman);
           if(mesaj.isCevabiVarMi()){
               holder.onaygelen.setVisibility(View.VISIBLE);
               holder.onaygelen.setText(mesaj.getCevap());
           }else{
               holder.onaygelen.setVisibility(View.GONE);
           }

           holder.EVETgelen.setOnClickListener(b->{
               if (listenercvp != null) {
                   listenercvp.onCevapGeldi("Borç isteği onaylandı!", mesaj.getMsjID());
               }
               mesaj.setCevabiVarMi(true);
               holder.onaygelen.setVisibility(View.VISIBLE);
               holder.onaygelen.setText("Borç isteği onaylandı!");
           });
          }
       }
    @Override
    public int getItemCount() {
        return tumMesajlar.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout gidenLayout, gelenLayout;
        TextView txtMiktarGiden, txtTarihGiden, txtAciklamaGiden, gidenSaat, gorulduDurumu;
        TextView txtMiktarGelen, txtTarihGelen, txtAciklamaGelen, gelenSaat;
        TextView onaygiden,onaygelen;
        Button EVETgelen,HAYIRgelen;

        public ViewHolder(View itemView) {
            super(itemView);

            gidenLayout = itemView.findViewById(R.id.mesajGidenLayout);
            gelenLayout = itemView.findViewById(R.id.mesajGelenLayout);

            txtMiktarGiden = itemView.findViewById(R.id.txtBorcMiktariGiden);
            txtTarihGiden = itemView.findViewById(R.id.txtOdenecekTarihGiden);
            txtAciklamaGiden = itemView.findViewById(R.id.txtAciklamaGiden);
            gidenSaat = itemView.findViewById(R.id.mesajGidenSaat);
            gorulduDurumu = itemView.findViewById(R.id.gorulduDurumu);

            txtMiktarGelen = itemView.findViewById(R.id.txtBorcMiktariGelen);
            txtTarihGelen = itemView.findViewById(R.id.txtOdenecekTarihGelen);
            txtAciklamaGelen = itemView.findViewById(R.id.txtAciklamaGelen);
            gelenSaat = itemView.findViewById(R.id.mesajGelenSaat);

            EVETgelen=itemView.findViewById(R.id.EVETgelen);
            HAYIRgelen=itemView.findViewById(R.id.HAYIRgelen);

            onaygiden=itemView.findViewById(R.id.onaygiden);
            onaygelen=itemView.findViewById(R.id.onaygelen);
        }
    }

}
