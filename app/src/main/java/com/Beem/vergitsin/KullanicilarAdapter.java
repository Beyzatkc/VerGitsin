package com.Beem.vergitsin;

import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;

import java.util.ArrayList;


public class KullanicilarAdapter extends RecyclerView.Adapter<KullanicilarAdapter.ViewHolder> {
    public interface OnArkadasEkleListener {
        void onArkadasEkleTiklandi(Kullanici kullanici);
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
                listener.onArkadasEkleTiklandi(kullanici);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKullaniciAdi;
        ImageView imageViewProfil;
        ImageView ekle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewKullaniciAdi = itemView.findViewById(R.id.textViewKullaniciAdi);
            imageViewProfil=itemView.findViewById(R.id.imageViewProfil);
            ekle=itemView.findViewById(R.id.ekle);
        }
    }
}
