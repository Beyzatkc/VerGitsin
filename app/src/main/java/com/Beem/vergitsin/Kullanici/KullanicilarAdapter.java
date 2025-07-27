package com.Beem.vergitsin.Kullanici;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    public KullanicilarAdapter(ArrayList<Kullanici> kullanicilar,Context contex,OnArkadasEkleListener listener) {
        this.kullanicilar = kullanicilar;
        this.context=contex;
        this.listener=listener;
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
}
