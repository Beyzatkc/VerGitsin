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

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.R;

import java.util.ArrayList;
import java.util.List;

public class GrupArkadasCikarAdapter extends RecyclerView.Adapter<GrupArkadasCikarAdapter.KullaniciViewHolder> {

    private List<Kullanici> kullaniciListesi;
    private Context context;
    private ArrayList<String> secilenCikacaklar = new ArrayList<>();
    private ArrayList<Kullanici> secilenKullanicilar = new ArrayList<>();

    public GrupArkadasCikarAdapter(List<Kullanici> kullaniciListesi, Context context) {
        this.kullaniciListesi = kullaniciListesi;
        this.context = context;
    }

    @NonNull
    @Override
    public KullaniciViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grup_ark_cikart_item, parent, false);
        return new KullaniciViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KullaniciViewHolder holder, int position) {
        Kullanici model = kullaniciListesi.get(position);
        holder.kullaniciAdi.setText(model.getKullaniciAdi());
        int resourceId = context.getResources().getIdentifier(model.getProfilFoto(), "drawable", context.getPackageName());
        holder.profilResmi.setImageResource(resourceId);

        holder.ProfilCikartLayout.setOnClickListener(v->{
            int mevcutRenk = holder.ProfilCardView.getCardBackgroundColor().getDefaultColor();

            if(holder.seciliMi){
                secilenCikacaklar.remove(model.getKullaniciId());
                secilenKullanicilar.remove(model);
                holder.seciliMi = false;
                int hedefRenk = context.getResources().getColor(R.color.neutral_grey);
                animasyonBaslat(holder.ProfilCardView, holder.kullaniciAdi, mevcutRenk, hedefRenk);
            }
            else {
                secilenCikacaklar.add(model.getKullaniciId());
                secilenKullanicilar.add(model);
                holder.seciliMi = true;
                int hedefRenk = context.getResources().getColor(R.color.accent_red);
                animasyonBaslat(holder.ProfilCardView, holder.kullaniciAdi, mevcutRenk, hedefRenk);
            }
        });

    }

    @Override
    public int getItemCount() {
        return kullaniciListesi.size();
    }

    public static class KullaniciViewHolder extends RecyclerView.ViewHolder {
        ImageView profilResmi;
        TextView kullaniciAdi;
        CardView ProfilCardView;
        LinearLayout ProfilCikartLayout;
        boolean seciliMi = false;

        public KullaniciViewHolder(@NonNull View itemView) {
            super(itemView);
            profilResmi = itemView.findViewById(R.id.Profilcikart);
            kullaniciAdi = itemView.findViewById(R.id.KullaniciAdicikart);
            ProfilCardView = itemView.findViewById(R.id.ProfilcikartCard);
            ProfilCikartLayout = itemView.findViewById(R.id.ProfilcikartLayout);
        }
    }

    public ArrayList<String> getSecilenCikacaklar() {
        return secilenCikacaklar;
    }

    public ArrayList<Kullanici> getSecilenKullanicilar() {
        return secilenKullanicilar;
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
