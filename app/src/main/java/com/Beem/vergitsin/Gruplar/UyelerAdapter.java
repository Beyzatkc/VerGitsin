package com.Beem.vergitsin.Gruplar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.R;

import java.util.List;

public class UyelerAdapter extends RecyclerView.Adapter<UyelerAdapter.UyeViewHolder> {

    private List<Kullanici> uyeler;
    private ProfilFotoListener listener;
    private String olusturanID;

    public UyelerAdapter(List<Kullanici> uyeler, ProfilFotoListener listener,String olusturanID) {
        this.uyeler = uyeler;
        this.listener = listener;
        this.olusturanID=olusturanID;
    }

    @NonNull
    @Override
    public UyeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uye_item, parent, false);
        return new UyeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UyeViewHolder holder, int position) {
        Kullanici uye = uyeler.get(position);
        holder.textUyeAdi.setText(uye.getKullaniciAdi());
        int resId = holder.itemView.getContext().getResources().getIdentifier(uye.getProfilFoto(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageUyeProfil.setImageResource(resId);

        if(olusturanID.equals(uye.getKullaniciId())){
            holder.imageTaci.setVisibility(View.VISIBLE);
        }else{
            holder.imageTaci.setVisibility(View.GONE);
        }

        holder.imageUyeProfil.setOnClickListener(v->{
            listener.onProfilFotoClick(uye);
        });
    }

    @Override
    public int getItemCount() {
        return uyeler.size();
    }

    public static class UyeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUyeProfil;
        TextView textUyeAdi;
        ImageView imageTaci;

        public UyeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUyeProfil = itemView.findViewById(R.id.imageUyeProfil);
            textUyeAdi = itemView.findViewById(R.id.textUyeAdi);
            imageTaci = itemView.findViewById(R.id.imageTaci);
        }
    }
    public List<Kullanici> getUyeler() {
        return uyeler;
    }
    interface ProfilFotoListener{
        void onProfilFotoClick(Kullanici uye);
    }
}
