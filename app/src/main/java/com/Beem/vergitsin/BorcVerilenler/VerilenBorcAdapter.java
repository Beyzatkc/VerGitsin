package com.Beem.vergitsin.BorcVerilenler;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VerilenBorcAdapter extends RecyclerView.Adapter<VerilenBorcAdapter.BorcViewHolder> {

    private List<VerilenBorcModel> borcListesi;
    private OnHatirlatClickListener hatirlatClickListener;

    public interface OnHatirlatClickListener {
        void onHatirlatClick(VerilenBorcModel borcModel, int position);
        void onProfileGec(String id);
    }

    public VerilenBorcAdapter(List<VerilenBorcModel> borcListesi) {
        this.borcListesi = borcListesi;
    }


    @NonNull
    @Override
    public BorcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.verilen_borc_item, parent, false);
        return new BorcViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BorcViewHolder holder, int position) {
        VerilenBorcModel borc = borcListesi.get(position);

        holder.tvAciklama.setText(borc.getAciklama());
        holder.tvMiktar.setText(borc.getMiktar() + "₺");
        holder.tvOdemeTarihi.setText("Geri Ödeme: " + formatTarih(borc.getOdenecekTarih()));
        holder.tvZaman.setText("Eklenme: " + formatTarih(borc.getTarih()));
        holder.tvIban.setText("IBAN: " + borc.getIban());

        String alinan = "Verilen: "+borc.getVerilenAdi();
        SpannableString sp = new SpannableString(alinan);
        sp.setSpan(new StyleSpan(Typeface.BOLD),alinan.indexOf(borc.getVerilenAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#F05A22")),alinan.indexOf(borc.getVerilenAdi()),alinan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvKimeVerildi.setText(sp);

        if (borc.isOdendiMi()) {
            holder.btnHatirlat.setVisibility(View.GONE);
        }

        holder.btnHatirlat.setOnClickListener(v -> {
            if (hatirlatClickListener != null) {
                hatirlatClickListener.onHatirlatClick(borc, position);
            }
        });
        holder.tvKimeVerildi.setOnClickListener(v->{
            if(hatirlatClickListener!=null){
                hatirlatClickListener.onProfileGec(borc.getKullaniciId());
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
                intent.putExtra("adi","verilenborc");
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
        TextView tvAciklama, tvMiktar, tvOdemeTarihi, tvZaman, tvKimeVerildi, tvIban;
        ImageView icon;
        Button btnHatirlat;

        public BorcViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAciklama = itemView.findViewById(R.id.tvAciklama);
            tvMiktar = itemView.findViewById(R.id.tvMiktar);
            tvOdemeTarihi = itemView.findViewById(R.id.tvOdemeTarihi);
            tvZaman = itemView.findViewById(R.id.tvZaman);
            tvKimeVerildi = itemView.findViewById(R.id.tvKimeVerildi);
            tvIban = itemView.findViewById(R.id.tvIban);
            icon = itemView.findViewById(R.id.icon);
            btnHatirlat = itemView.findViewById(R.id.btnHatirlat);
        }
    }

    private String formatTarih(Timestamp timestamp) {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(timestamp.toDate());
    }

    public void itemEkle(VerilenBorcModel model) {
        borcListesi.add(model);
        notifyItemInserted(borcListesi.size() - 1);
    }

    public void setHatirlatClickListener(OnHatirlatClickListener listener) {
        this.hatirlatClickListener = listener;
    }
}
