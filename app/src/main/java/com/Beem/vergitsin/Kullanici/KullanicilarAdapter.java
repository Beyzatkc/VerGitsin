package com.Beem.vergitsin.Kullanici;
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

import com.Beem.vergitsin.Arkadaslar.Arkadas;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Profil.DigerProfilFragment;
import com.Beem.vergitsin.R;

import java.util.ArrayList;


public class KullanicilarAdapter extends RecyclerView.Adapter<KullanicilarAdapter.ViewHolder> {
    public interface OnArkadasEkleListener {
        void onArkadasEkleTiklandi(Kullanici kullanici);
        void onArkadasCıkarTiklandi(Kullanici kullanici);
    }

    private ArrayList<Kullanici> kullanicilar;
    private Context context;
    private OnArkadasEkleListener listener;
    private FragmentManager manager;
    private Runnable DialogKapat;

    public KullanicilarAdapter(ArrayList<Kullanici> kullanicilar,Context contex,OnArkadasEkleListener listener,FragmentManager manager, Runnable DialogKapat) {
        this.kullanicilar = kullanicilar;
        this.context=contex;
        this.listener=listener;
        this.manager=manager;
        this.DialogKapat=DialogKapat;
    }

    @NonNull
    @Override
    public KullanicilarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_kullanici, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KullanicilarAdapter.ViewHolder holder, int position) {
        Kullanici kullanici = kullanicilar.get(position);
        holder.textViewKullaniciAdi.setText(kullanici.getKullaniciAdi());
        int resId = context.getResources().getIdentifier(
                kullanici.getProfilFoto(), "drawable", context.getPackageName());
        holder.imageViewProfil.setImageResource(resId);
        holder.ekle.setOnClickListener(b->{
            if (listener != null) {
                holder.ekle.setVisibility(View.GONE);
                holder.eklendi.setVisibility(View.VISIBLE);
                kullanici.setArkdasMi(true);
                listener.onArkadasEkleTiklandi(kullanici);
            }
        });
        holder.eklendi.setOnClickListener(b->{
            if (listener != null) {
                holder.eklendi.setVisibility(View.GONE);
                holder.ekle.setVisibility(View.VISIBLE);
                listener.onArkadasCıkarTiklandi(kullanici);
            }
        });
        holder.itemView.setOnClickListener(v->{
            TiklananArkadasProfileGec(kullanici);
        });
    }
    @Override
    public int getItemCount() {
        return kullanicilar.size();
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

    private void TiklananArkadasProfileGec(Kullanici arkadas){
        if(arkadas!=null) {
            Bundle bundle = new Bundle();
            Arkadas ark = new Arkadas(arkadas);
            ark.setArkdasMi(arkadas.isArkdasMi());
            bundle.putSerializable("kullanici", ark);
            Fragment arkFragment = new DigerProfilFragment();
            arkFragment.setArguments(bundle);
            DialogKapat.run();
            manager.beginTransaction()
                    .replace(R.id.konteynir, arkFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
