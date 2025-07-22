package com.Beem.vergitsin.Arkadaslar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Profil.DigerProfilFragment;
import com.Beem.vergitsin.R;

import java.io.Serializable;
import java.util.ArrayList;


public class ArkadaslarAdapter extends RecyclerView.Adapter<ArkadaslarAdapter.ViewHolder> {
    public interface OnArkadasEkleListener {
        void onArkadasEkleTiklandi(Kullanici kullanici);
        void onArkadasCıkarTiklandi(Kullanici kullanici);
    }

    private ArrayList<Arkadas> arkadaslar;
    private Context context;
    private OnArkadasEkleListener listener;
    private FragmentManager manager;

    public ArkadaslarAdapter(ArrayList<Arkadas> arkadaslar,Context contex,OnArkadasEkleListener listener,FragmentManager manager) {
        this.arkadaslar = arkadaslar;
        this.context=contex;
        this.listener=listener;
        this.manager=manager;
    }

    @NonNull
    @Override
    public ArkadaslarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_kullanici, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArkadaslarAdapter.ViewHolder holder, int position) {
        Arkadas arkadas = arkadaslar.get(position);
        holder.itemView.setOnClickListener(v->{
            TiklananArkadasProfileGec(arkadas);
        });
        if(arkadas.isEngelliMi()){
            holder.ekle.setVisibility(View.GONE);
            holder.eklendi.setVisibility(View.GONE);
        }
        else{
            if(arkadas.isArkdasMi()){
                holder.ekle.setVisibility(View.GONE);
                holder.eklendi.setVisibility(View.VISIBLE);
            }
            else{
                holder.ekle.setVisibility(View.VISIBLE);
                holder.eklendi.setVisibility(View.GONE);
            }
        }
        System.out.println(arkadas.getKullaniciAdi()+"----");
        holder.textViewKullaniciAdi.setText(arkadas.getKullaniciAdi());
        int resId = context.getResources().getIdentifier(arkadas.getProfilFoto(), "drawable", context.getPackageName());
        holder.imageViewProfil.setImageResource(resId);
        holder.ekle.setOnClickListener(b->{
            if (listener != null) {
                holder.ekle.setVisibility(View.GONE);
                holder.eklendi.setVisibility(View.VISIBLE);
                arkadas.setArkdasMi(true);
                listener.onArkadasEkleTiklandi(arkadas);
            }
        });
        holder.eklendi.setOnClickListener(b->{
            if (listener != null) {
                holder.eklendi.setVisibility(View.GONE);
                holder.ekle.setVisibility(View.VISIBLE);
                arkadas.setArkdasMi(false);
                listener.onArkadasCıkarTiklandi(arkadas);
            }
        });
    }
    @Override
    public int getItemCount() {
        return arkadaslar.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKullaniciAdi;
        ImageView imageViewProfil;
        ImageView ekle;
        ImageView eklendi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewKullaniciAdi = itemView.findViewById(R.id.textViewKullaniciAdi);
            imageViewProfil=itemView.findViewById(R.id.imageViewProfil);
            ekle=itemView.findViewById(R.id.ekle);
            eklendi=itemView.findViewById(R.id.eklendi);
        }
    }

    private void TiklananArkadasProfileGec(Arkadas arkadas){
        if(arkadas!=null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("kullanici", arkadas);
            Fragment arkFragment = new DigerProfilFragment();
            arkFragment.setArguments(bundle);

            manager.beginTransaction()
                    .replace(R.id.konteynir, arkFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public ArrayList<Arkadas> getKullanicilar() {
        System.out.println("**");
        return arkadaslar;
    }
}
