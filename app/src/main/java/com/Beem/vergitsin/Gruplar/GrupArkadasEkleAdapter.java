package com.Beem.vergitsin.Gruplar;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.R;

import java.util.ArrayList;
import java.util.List;

public class GrupArkadasEkleAdapter extends RecyclerView.Adapter<GrupArkadasEkleAdapter.KullaniciViewHolder> {

    private List<Kullanici> kullaniciListesi;
    private Context context;
    private List<String> uyeler;
    private ArrayList<String> yeniUyeler = new ArrayList<>();
    private ArrayList<Kullanici> yeniArklar = new ArrayList<>();

    public GrupArkadasEkleAdapter(List<Kullanici> kullaniciListesi, Context context, Grup uyeler) {
        this.kullaniciListesi = kullaniciListesi;
        this.context = context;
        this.uyeler = uyeler.getUyeler();
    }

    @NonNull
    @Override
    public KullaniciViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grup_ark_ekle_item, parent, false);
        return new KullaniciViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KullaniciViewHolder holder, int position) {
        Kullanici model = kullaniciListesi.get(position);
        holder.kullaniciAdi.setText(model.getKullaniciAdi());
        holder.seciliMi = uyeler.contains(model.getKullaniciId());
        int resourceId = context.getResources().getIdentifier(model.getProfilFoto(), "drawable", context.getPackageName());
        holder.profilResmi.setImageResource(resourceId);
        if(uyeler.contains(model.getKullaniciId())){
            holder.ProfilekleCard.setCardBackgroundColor(context.getColor(R.color.accent_green));
            return;
        }
        holder.ProfilekleLayout.setOnClickListener(v->{
            int mevcutRenk = holder.ProfilekleCard.getCardBackgroundColor().getDefaultColor();
            if(holder.seciliMi) {
                int hedefRenk = context.getResources().getColor(R.color.neutral_grey);
                yeniUyeler.remove(model.getKullaniciId());
                yeniArklar.remove(model);
                animasyonBaslat(holder.ProfilekleCard, holder.kullaniciAdi, mevcutRenk, hedefRenk);
                holder.seciliMi = false;
            }
            else {
                int hedefRenk = context.getResources().getColor(R.color.accent_green);
                yeniUyeler.add(model.getKullaniciId());
                yeniArklar.add(model);
                animasyonBaslat(holder.ProfilekleCard, holder.kullaniciAdi, mevcutRenk, hedefRenk);
                holder.seciliMi = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return kullaniciListesi.size();
    }

    public static class KullaniciViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ProfilekleLayout;
        CardView ProfilekleCard;
        ImageView profilResmi;
        TextView kullaniciAdi;
        boolean seciliMi;

        public KullaniciViewHolder(@NonNull View itemView) {
            super(itemView);
            profilResmi = itemView.findViewById(R.id.Profilark);
            kullaniciAdi = itemView.findViewById(R.id.KullaniciAdiark);
            ProfilekleLayout = itemView.findViewById(R.id.ProfilekleLayout);
            ProfilekleCard = itemView.findViewById(R.id.ProfilekleCard);
            seciliMi = false;
        }
    }
    public ArrayList<String> getYeniUyeler() {
        return yeniUyeler;
    }

    public ArrayList<Kullanici> getYeniArklar() {
        return yeniArklar;
    }


    private void animasyonBaslat(CardView cardView, TextView textView, int baslangicRenk, int hedefRenk) {
        // Background için animasyon
        ValueAnimator bgAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), baslangicRenk, hedefRenk);
        bgAnimator.setDuration(400);
        bgAnimator.addUpdateListener(animation -> {
            int animatedColor = (int) animation.getAnimatedValue();
            cardView.setCardBackgroundColor(animatedColor);
        });

        // Text rengi için animasyon
        ValueAnimator textAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), baslangicRenk, hedefRenk);
        textAnimator.setDuration(400);
        textAnimator.addUpdateListener(animation -> {
            int animatedColor = (int) animation.getAnimatedValue();
            textView.setTextColor(animatedColor);
        });

        bgAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        textAnimator.setInterpolator(new AccelerateDecelerateInterpolator());


        // Animasyonları başlat
        bgAnimator.start();
        textAnimator.start();
    }
}

