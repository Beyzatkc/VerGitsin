package com.Beem.vergitsin.Gruplar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GruplarAdapter extends RecyclerView.Adapter<GruplarAdapter.GruplarViewHolder> {

    private Context context;
    private List<Grup> grupList;
    private DahaFazlaListener dahaFazlaListener;

    public GruplarAdapter(Context context, List<Grup> grupList, DahaFazlaListener dahaFazlaListener) {
        this.context = context;
        this.grupList = grupList;
        this.dahaFazlaListener = dahaFazlaListener;
    }

    @NonNull
    @Override
    public GruplarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grup_item, parent, false);
        return new GruplarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GruplarViewHolder holder, int position) {
        Grup grup = grupList.get(position);
        holder.textGrupAdi.setText(grup.getGrupAdi());
        holder.textUyeSayisi.setText("Üyeler: "+grup.getUyeler().size());
        holder.textTarih.setText(formatTarih(grup.getOlusturmaTarihi()));
        int resId = context.getResources().getIdentifier(grup.getGrupFoto(), "drawable", context.getPackageName());
        holder.imageGrupIcon.setImageResource(resId);

        holder.imageMore.setOnClickListener(v->{
            dahaFazlaListener.dahaFazla(grup);
        });
    }

    @Override
    public int getItemCount() {
        return grupList.size();
    }


    public static class GruplarViewHolder extends RecyclerView.ViewHolder {
        ImageView imageGrupIcon, imageMore;
        TextView textGrupAdi, textUyeSayisi, textTarih;

        public GruplarViewHolder(@NonNull View itemView) {
            super(itemView);
            imageGrupIcon = itemView.findViewById(R.id.imageGrupIcon);
            imageMore = itemView.findViewById(R.id.imageMore);
            textGrupAdi = itemView.findViewById(R.id.textGrupAdi);
            textUyeSayisi = itemView.findViewById(R.id.textUyeSayisi);
            textTarih = itemView.findViewById(R.id.textTarih);
        }

    }

    private String formatTarih(Timestamp tarih) {
        Date date = tarih.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = formatter.format(date);
        return "Oluşturuldu: "+formattedDate;
    }

    public List<Grup> getGrupList() {
        return grupList;
    }

    interface DahaFazlaListener{
        void dahaFazla(Grup grup);
    }
}
