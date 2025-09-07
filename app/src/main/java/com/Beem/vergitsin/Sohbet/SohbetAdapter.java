
package com.Beem.vergitsin.Sohbet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Mesaj.Mesaj;
import com.Beem.vergitsin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class SohbetAdapter extends RecyclerView.Adapter<SohbetAdapter.ViewHolder>{
    public interface OnSohbetClickListener {
        void onSohbetClicked(Sohbet sohbet);
        void onSohbetSilindi(Sohbet sohbet);
    }
    private ArrayList<Sohbet>sohbetler;
    private Context context;
    private OnSohbetClickListener listener;

    public SohbetAdapter(ArrayList<Sohbet> sohbetler, Context context,OnSohbetClickListener listener) {
        this.sohbetler = sohbetler;
        this.context = context;
        this.listener=listener;
    }
    public void sohbetEkle(Sohbet yeniSohbet) {
        for (Sohbet s : sohbetler) {
            if (s.getSohbetID().equals(yeniSohbet.getSohbetID())) {
                return;
            }
        }
        sohbetler.add(0, yeniSohbet);
        notifyItemInserted(0);
    }

    public void SohbetGuncelle(Sohbet guncelSohbet) {
       /* for (int i = 0; i < sohbetler.size(); i++) {
            if (sohbetler.get(i).getSohbetID().equals(guncelSohbet.getSohbetID())) {
                sohbetler.get(i).setSonMesaj(guncelSohbet.getSonMesaj());
                sohbetler.get(i).setSonmsjsaati(guncelSohbet.getSonmsjsaati());
                notifyItemChanged(i);
                break;
            }
        }*/
        for (int i = 0; i < sohbetler.size(); i++) {
            if (sohbetler.get(i).getSohbetID().equals(guncelSohbet.getSohbetID())) {

                // Mevcut sohbeti al ve güncelle
                Sohbet mevcut = sohbetler.remove(i);
                mevcut.setSonMesaj(guncelSohbet.getSonMesaj());
                mevcut.setSonmsjsaati(guncelSohbet.getSonmsjsaati());

                // Başa ekle
                sohbetler.add(0, mevcut);

                // Adapter'a bildir
                notifyItemMoved(i, 0);
                notifyItemChanged(0);
                break;
            }
        }
    }
    public void GorulmeyenSayisi(Sohbet sohbet) {
        for (int i = 0; i < sohbetler.size(); i++) {
            if (sohbetler.get(i).equals(sohbet)) {
                notifyItemChanged(i);
                break;
            }
        }
    }
    public void sohbetiSil(Sohbet sohbet) {
            for (int i = 0; i < sohbetler.size(); i++) {
                if (sohbetler.get(i).getSohbetID().equals(sohbet.getSohbetID())) {
                    sohbetler.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
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
        if(sohbet.getSonMesaj()!=null){
            holder.son_mesaj.setText(sohbet.getSonMesaj());
        }
        if(sohbet.getGorulmemisMesajSayisi()!=0){
            holder.gorulmeyen_sayi.setVisibility(View.VISIBLE);
            holder.yazi.setVisibility(View.VISIBLE);
            if(sohbet.getGorulmemisMesajSayisi()>99){
                holder.gorulmeyen_sayi.setText("99+");
            }
            else{
                holder.gorulmeyen_sayi.setText(String.valueOf(sohbet.getGorulmemisMesajSayisi()));
            }
        }else{
            holder.gorulmeyen_sayi.setVisibility(View.GONE);
            holder.yazi.setVisibility(View.GONE);
        }

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
        if(sohbet.getSonmsjsaati()!=null) {
            holder.mesaj_saat.setVisibility(View.VISIBLE);
            String zaman=longToSaatDakika(sohbet.getSonmsjsaati());
            holder.mesaj_saat.setText(zaman);
        }else{
            holder.mesaj_saat.setVisibility(View.GONE);
        }
        holder.sohbet_kutu.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSohbetClicked(sohbet);
            }
        });
        holder.sohbet_kutu.setOnLongClickListener(b->{
            PopupMenu popupMenu=new PopupMenu(b.getContext(),b);
            popupMenu.getMenu().add("Sil");
            popupMenu.setOnMenuItemClickListener(item->{
                if(item.getTitle().equals("Sil")){
                    listener.onSohbetSilindi(sohbet);
                    int a = sohbetler.indexOf(sohbet);
                    sohbetler.remove(sohbet);
                    notifyItemRemoved(a);
                    return true;
                }
                return false;
            });
            popupMenu.show();
            return true;
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
       private ConstraintLayout sohbet_kutu;
       private TextView gorulmeyen_sayi;
       private TextView yazi;

        public ViewHolder(View itemView) {
            super(itemView);
            kisi_adi=itemView.findViewById(R.id.kisi_adi);
            son_mesaj=itemView.findViewById(R.id.son_mesaj);
            mesaj_saat=itemView.findViewById(R.id.mesaj_saat);
            kisi_fotosu=itemView.findViewById(R.id.kisi_fotosu);
            sohbet_kutu=itemView.findViewById(R.id.sohbet_kutu);
            gorulmeyen_sayi=itemView.findViewById(R.id.gorulmeyen_sayi);
            yazi=itemView.findViewById(R.id.yazi);
        }
    }
}
