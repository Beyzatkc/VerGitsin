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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;

public class YoneticiSecAdapter extends RecyclerView.Adapter<YoneticiSecAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Kullanici> uyeler;
    private int sonSecili = 0;

    public YoneticiSecAdapter(Context context, ArrayList<Kullanici> uyeler) {
        this.context = context;
        this.uyeler = uyeler;
    }

    @NonNull
    @Override
    public YoneticiSecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grup_ark_cikart_item, parent, false);
        return new YoneticiSecAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YoneticiSecAdapter.ViewHolder holder, int position) {
        Kullanici kullanici = uyeler.get(position);
        holder.KullaniciAdicikart.setText(kullanici.getKullaniciAdi());
        int resId = context.getResources().getIdentifier(kullanici.getProfilFoto(), "drawable", context.getPackageName());
        holder.Profilcikart.setImageResource(resId);
        if(sonSecili != position){
            int mevcutRenk = holder.ProfilCardView.getCardBackgroundColor().getDefaultColor();
            int hedefRenk = context.getColor(R.color.neutral_grey);
            animasyonBaslat(holder.ProfilCardView, holder.KullaniciAdicikart, mevcutRenk, hedefRenk);
        }
        holder.ProfilCikartLayout.setOnClickListener(v->{
            sonSecili = position;
            animasyonBaslat(holder.ProfilCardView, holder.KullaniciAdicikart, context.getResources().getColor(R.color.neutral_grey), context.getResources().getColor(R.color.accent_green));
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return uyeler.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Profilcikart;
        TextView KullaniciAdicikart;
        CardView ProfilCardView;
        LinearLayout ProfilCikartLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Profilcikart = itemView.findViewById(R.id.Profilcikart);
            KullaniciAdicikart = itemView.findViewById(R.id.KullaniciAdicikart);
            ProfilCardView = itemView.findViewById(R.id.ProfilcikartCard);
            ProfilCikartLayout = itemView.findViewById(R.id.ProfilcikartLayout);

        }
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


    public String getSonSecili() {
        return uyeler.get(sonSecili).getKullaniciId();
    }
}
