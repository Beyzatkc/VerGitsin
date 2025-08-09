package com.Beem.vergitsin.BorcAlinanlar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Alarm.AlarmReceiver;
import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlinanBorcAdapter extends RecyclerView.Adapter<AlinanBorcAdapter.BorcViewHolder> {

    private List<AlinanBorcModel> borcListesi;
    private OnBorcOdeClickListener borcOdeClickListener;

    public interface OnBorcOdeClickListener {
        void onBorcOdeClick(AlinanBorcModel borcModel, int position);
    }

    public AlinanBorcAdapter(List<AlinanBorcModel> borcListesi) {
        this.borcListesi = borcListesi;
    }

    @NonNull
    @Override
    public BorcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.borc_item, parent, false);
        return new BorcViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BorcViewHolder holder, int position) {
        AlinanBorcModel borc = borcListesi.get(position);

        holder.tvAciklama.setText(borc.getAciklama());
        holder.tvMiktar.setText(borc.getMiktar()+"₺");
        holder.tvOdenecekTarih.setText("Ödeme: " + formatTarih(borc.getOdenecekTarih()));
        holder.tvZaman.setText("Eklenme: " + formatTarih(borc.getTarih()));
        holder.tvIban.setText("IBAN: " + borc.getIban());

        String alinan = "Alınan: "+borc.getAlinanAdi();
        SpannableString sp = new SpannableString(alinan);
        sp.setSpan(new StyleSpan(Typeface.BOLD),alinan.indexOf(borc.getAlinanAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#F05A22")),alinan.indexOf(borc.getAlinanAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvKimdenAlindi.setText(sp);

        if(borc.isOdendiMi()){
            holder.btnBorcuOde.setVisibility(View.GONE);
        }

        holder.btnBorcuOde.setOnClickListener(v -> {
            if (borcOdeClickListener != null) {
                borcOdeClickListener.onBorcOdeClick(borc, position);
            }
        });
        holder.icon.setOnClickListener(v->{
            Context context=v.getContext();
            LayoutInflater inflater=LayoutInflater.from(context);
            View dialogview=inflater.inflate(R.layout.alarmicin,null);

            Button evet=dialogview.findViewById(R.id.evtbuton);
            Button hayir= dialogview.findViewById(R.id.hyrbuton);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogview);
            AlertDialog dialog = builder.create();

            evet.setOnClickListener(e -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        context.startActivity(intent);
                        Toast.makeText(context, "Lütfen alarm iznini açın.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }
                }
                Timestamp zaman = borc.getOdenecekTarih();
                Date date = zaman.toDate();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                calendar.set(Calendar.HOUR_OF_DAY, 18);
                calendar.set(Calendar.MINUTE, 20);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long hedefZaman = calendar.getTimeInMillis();
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("adi","alinanborc");
                intent.putExtra("miktar",borc.getMiktar());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, hedefZaman, pendingIntent);

                Toast.makeText(context, "Hatırlatıcı kuruldu", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            hayir.setOnClickListener(h -> {
                dialog.dismiss();
            });

            dialog.show();

        });
    }

    @Override
    public int getItemCount() {
        return borcListesi.size();
    }

    public static class BorcViewHolder extends RecyclerView.ViewHolder {
        TextView tvAciklama, tvMiktar, tvOdenecekTarih, tvZaman, tvKimdenAlindi, tvIban;
        ImageView icon;
        Button btnBorcuOde;

        public BorcViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAciklama = itemView.findViewById(R.id.tvAciklama);
            tvMiktar = itemView.findViewById(R.id.tvMiktar);
            tvOdenecekTarih = itemView.findViewById(R.id.tvOdenecekTarih);
            tvZaman = itemView.findViewById(R.id.tvZaman);
            icon = itemView.findViewById(R.id.icon);
            btnBorcuOde = itemView.findViewById(R.id.btnBorcuOde);
            tvKimdenAlindi = itemView.findViewById(R.id.tvKimdenAlindi);
            tvIban = itemView.findViewById(R.id.tvIban);
        }
    }


    private String formatTarih(Timestamp timestamp) {
        return new java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
                .format(timestamp.toDate());
    }

    public void ItemEkle(AlinanBorcModel model){
        borcListesi.add(model);
        notifyItemInserted(borcListesi.size()-1);
    }

    public void setBorcOdeClickListener(OnBorcOdeClickListener borcOdeClickListener) {
        this.borcOdeClickListener = borcOdeClickListener;
    }
}
