package com.Beem.vergitsin.Mesaj;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Profil.DigerProfilFragment;
import com.Beem.vergitsin.Profil.ProfilYonetici;
import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MesajAdapterGrup extends RecyclerView.Adapter<MesajAdapterGrup.ViewHolder>{
    private ArrayList<Mesaj> tumMesajlar;
    private Context context;
    private CevapGeldiGrup listenercvp;
    private MesajSilmeGuncellemeGrup listenersilme;
    private boolean CikilmisMi = false;
    private ProfileGec profilGec;

    interface ProfileGec{
        void profilGec(String kullaniciID);
    }
    public MesajAdapterGrup(ArrayList<Mesaj>tumMesajlar, Context context) {
        this.tumMesajlar=tumMesajlar;
        this.context=context;
    }
    public void setListenercvp(CevapGeldiGrup listener) {
        this.listenercvp = listener;
    }

    public void mesajEkle(Mesaj yeniMesaj) {
        tumMesajlar.add(yeniMesaj);
        notifyItemInserted(tumMesajlar.size() - 1);
    }
    public void eskiMesajlariBasaEkle(ArrayList<Mesaj> mesajlar) {
        tumMesajlar.addAll(0, mesajlar);
        notifyItemRangeInserted(0, mesajlar.size());
    }
    public void guncelleMesajListesi(ArrayList<Mesaj> yeniListe) {
        tumMesajlar.clear();
        tumMesajlar.addAll(yeniListe);
        notifyDataSetChanged();
    }
    public void setListenersil(MesajSilmeGuncellemeGrup listener) {
        this.listenersilme = listener;
    }

    public void mesajGuncelle(Mesaj mesaj){
        for (int i = 0; i < tumMesajlar.size(); i++) {
            if (tumMesajlar.get(i).getMsjID().equals(mesaj.getMsjID())) {
                mesaj.setIstekAtanAdi(tumMesajlar.get(i).istekAtanAdi);
                tumMesajlar.set(i, mesaj);
                notifyDataSetChanged();
                if (i == tumMesajlar.size() - 1 && listenersilme != null) {
                    listenersilme.onSonMesajGuncelleme(mesaj);
                }
                break;
            }
        }
    }
    public void MesajSilme(Mesaj mesaj) {
        for (int i = 0; i < tumMesajlar.size(); i++) {
            if (tumMesajlar.get(i).getMsjID().equals(mesaj.getMsjID())) {
                if (i == tumMesajlar.size() - 1) {
                    if (tumMesajlar.size() > 1) {
                        listenersilme.onSonMesajSilme(tumMesajlar.get(tumMesajlar.size() - 2));
                    } else {
                        listenersilme.onSonMesajSilme(null);
                    }
                }
                tumMesajlar.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_mesaj_grup, parent, false);
        return new ViewHolder(view);
    }
    public String timestampToGunAyYil(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
    public String longToSaatDakika(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date(timeMillis);
        return sdf.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mesaj mesaj=tumMesajlar.get(position);
        String benim= MainActivity.kullanicistatic.getKullaniciId();


        if(CikilmisMi){
            holder.EVETgelen.setEnabled(false);
            holder.HAYIRgelen.setEnabled(false);
            holder.EVETgelen.setAlpha(0.5f);
            holder.HAYIRgelen.setAlpha(0.5f);
        }
        holder.gonderenadigelen.setOnClickListener(v->{
            if(profilGec!=null){
                profilGec.profilGec(mesaj.getIstegiAtanId());
            }
        });

        if(mesaj.getIstegiAtanId().equals(benim)){
            holder.gelenLayout.setVisibility(View.GONE);
            holder.gidenLayout.setVisibility(View.VISIBLE);
            holder.txtMiktarGiden.setText(mesaj.getMiktar()+" TL");
            holder.ibantextgiden.setText(mesaj.getIban());
            holder.gonderenadigiden.setText(MainActivity.kullanicistatic.getKullaniciAdi());
            holder.txtAciklamaGiden.setText(mesaj.getAciklama());
            Timestamp odemeTarihi = mesaj.getOdenecekTarih();
            String tarihStr = timestampToGunAyYil(odemeTarihi);
            holder.txtTarihGiden.setText(tarihStr);

            Long MesajSaati=mesaj.getZaman();
            String zaman=longToSaatDakika(MesajSaati);
            holder.gidenSaat.setText(zaman);
            if(mesaj.isCevabiVarMi()){
                holder.onaygiden.setVisibility(View.VISIBLE);
                holder.onaygiden.setVisibility(View.VISIBLE);
                holder.onay_giden_gonderen_ad.setText(mesaj.getCevapvrnAdi());
                holder.onaygidenicerik.setText(mesaj.getCevapicerigi());
            }else{
                holder.onaygiden.setVisibility(View.GONE);
            }
            if (tumMesajlar.get(tumMesajlar.size() - 1).equals(mesaj)) {
                Map<String, Boolean> gorulmeler = mesaj.getadlar();

                StringBuilder gorulduYazanlar = new StringBuilder();

                if (gorulmeler != null) {
                    for (Map.Entry<String, Boolean> entry : gorulmeler.entrySet()) {
                        String kullaniciAdi = entry.getKey();
                        Boolean gorulduMu = entry.getValue();

                        if (Boolean.TRUE.equals(gorulduMu)) {
                                if (gorulduYazanlar.length() > 0) {
                                    gorulduYazanlar.append(", ");
                                }
                                gorulduYazanlar.append(kullaniciAdi);
                            }
                    }
                }
                if (gorulduYazanlar.length() > 0) {
                    holder.gorulduDurumu.setText("Gördü: " + gorulduYazanlar.toString());
                } else {
                    holder.gorulduDurumu.setText("");
                }
            }else{
                holder.gorulduDurumu.setVisibility(View.GONE);
            }
            holder.ibanCopyIcongiden.setOnClickListener(b -> {
                String iban = mesaj.getIban();
                ClipboardManager clipboardManager = (ClipboardManager) holder.itemView.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("IBAN", iban);
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(holder.itemView.getContext(), "IBAN kopyalandı!", Toast.LENGTH_SHORT).show();
            });
            holder.gidenlayoutsilme.setOnLongClickListener(view->{
                PopupMenu popupMenu=new PopupMenu(view.getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
                if (mesaj.isCevabiVarMi()) {
                    popupMenu.getMenu().findItem(R.id.menu_guncelle).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(item->{
                    if (item.getItemId() == R.id.menu_sil) {
                        listenersilme.onSilmeislemi(mesaj);
                        return true;
                    }else if (item.getItemId() == R.id.menu_guncelle) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setTitle("Mesajı Güncelle");

                        LayoutInflater inflater = LayoutInflater.from(view.getContext());
                        View dialogView = inflater.inflate(R.layout.dialog_mesaj_guncelle, null);

                        EditText editMiktar = dialogView.findViewById(R.id.editMiktar);
                        EditText editAciklama = dialogView.findViewById(R.id.editAciklama);
                        EditText editTarih = dialogView.findViewById(R.id.editTarih);
                        EditText ibanedit=dialogView.findViewById(R.id.ibanEdit);

                        editTarih.setInputType(InputType.TYPE_NULL);
                        editTarih.setFocusable(false);

                        editMiktar.setText(mesaj.getMiktar());
                        editAciklama.setText(mesaj.getAciklama());
                        ibanedit.setText(mesaj.getIban());
                        editTarih.setText(timestampToGunAyYil(mesaj.getOdenecekTarih()));

                        editTarih.setOnClickListener(v -> {
                            final Calendar calendar = Calendar.getInstance();
                            int yil = calendar.get(Calendar.YEAR);
                            int ay = calendar.get(Calendar.MONTH);
                            int gun = calendar.get(Calendar.DAY_OF_MONTH);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    view.getContext(),
                                    (datePicker, secilenYil, secilenAy, secilenGun) -> {
                                        String tarihStr2 = String.format(Locale.getDefault(),
                                                "%02d/%02d/%04d", secilenGun, secilenAy + 1, secilenYil);
                                        editTarih.setText(tarihStr2);
                                    },
                                    yil, ay, gun
                            );

                            // Geçmiş tarihleri engelle
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                            datePickerDialog.show();
                        });

                        builder.setView(dialogView);

                        builder.setPositiveButton("Güncelle", (dialog, which) -> {
                            String yeniMiktar = editMiktar.getText().toString().trim();
                            String yeniAciklama = editAciklama.getText().toString().trim();
                            String yeniiban=ibanedit.getText().toString().trim();
                            String yeniTarihStr = editTarih.getText().toString().trim();

                            if (yeniMiktar.isEmpty() || yeniTarihStr.isEmpty()) {
                                Toast.makeText(view.getContext(), "Miktar ve tarih boş olamaz!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                sdf.setLenient(false);
                                Date girilenTarih = sdf.parse(yeniTarihStr);

                                mesaj.setOdenecekTarih(new Timestamp(girilenTarih));
                                mesaj.setMiktar(yeniMiktar);
                                mesaj.setAciklama(yeniAciklama);
                                mesaj.setIban(yeniiban);
                                mesaj.setOdenecekTarih(new Timestamp(girilenTarih));

                                listenersilme.onMesajGuncelleme(mesaj);
                            } catch (ParseException e) {
                                Toast.makeText(view.getContext(), "Tarih formatı hatalı!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        builder.setNegativeButton("İptal", (dialog, which) -> dialog.dismiss());
                        builder.show();
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
                return true;
            });
        }else{
            holder.gelenLayout.setVisibility(View.VISIBLE);
            holder.gidenLayout.setVisibility(View.GONE);
            holder.gonderenadigelen.setText(mesaj.getIstekAtanAdi());
            holder.txtMiktarGelen.setText(mesaj.getMiktar()+" TL");
            holder.ibantextgelen.setText(mesaj.getIban());
            holder.txtAciklamaGelen.setText(mesaj.getAciklama());
            Timestamp odemeTarihi = mesaj.getOdenecekTarih();
            String tarihStr = timestampToGunAyYil(odemeTarihi);
            holder.txtTarihGelen.setText(tarihStr);

            Long MesajSaati=mesaj.getZaman();
            String zaman=longToSaatDakika(MesajSaati);
            holder.gelenSaat.setText(zaman);
            if(mesaj.isCevabiVarMi()){
                holder.onaygelen.setVisibility(View.VISIBLE);
                holder.onay_gonderen_adgelen.setText(mesaj.getCevapvrnAdi());
                holder.onaygelenicerikgelen.setText(mesaj.getCevapicerigi());
                holder.EVETgelen.setVisibility(View.GONE);
                holder.HAYIRgelen.setVisibility(View.GONE);
                holder.onaylayicigelen.setVisibility(View.VISIBLE);
            }else{
                holder.onaygelen.setVisibility(View.GONE);
                holder.EVETgelen.setVisibility(View.VISIBLE);
                holder.HAYIRgelen.setVisibility(View.VISIBLE);
                holder.onaylayicigelen.setVisibility(View.GONE);
            }
            holder.ibanCopyIcongelen.setOnClickListener(b->{
                String iban=mesaj.getIban();
                ClipboardManager clipboardManager=(ClipboardManager) holder.itemView.getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData=ClipData.newPlainText("IBAN",iban);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(holder.itemView.getContext(), "IBAN kopyalandı!", Toast.LENGTH_SHORT).show();
            });
            holder.EVETgelen.setOnClickListener(b->{
                if (listenercvp != null) {
                    listenercvp.onCevapGeldiGrup(MainActivity.kullanicistatic.getKullaniciId(), mesaj.getMsjID(), MainActivity.kullanicistatic.getKullaniciAdi(),"Borç isteği onaylandı");
                    listenercvp.onEveteBastiGrup(MainActivity.kullanicistatic.getKullaniciId(),mesaj);
                }
                mesaj.setCevabiVarMi(true);
            });

            holder.HAYIRgelen.setOnClickListener(b -> {
                mesaj.setCevabiVarMi(true);
                Context context = holder.itemView.getContext();

                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.hayir_icin_cvp, null);

                ListView listView = dialogView.findViewById(R.id.cevapListesi);
                TextView baslik = dialogView.findViewById(R.id.dialogBaslik);

                String[] secenekler = {
                        "O kadar çıkmaz, cüzdanımın nüfusu sıfır!",
                        "Başka bir miktar dene bakalım, belki bende o var!",
                        "Param yoook, hesabım boş!",
                        "Şu an param yok ama kalbim hep seninle!"
                };
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, secenekler);
                listView.setAdapter(adapter);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .create();

                listView.setOnItemClickListener((parent, view, position2, id) -> {
                    String secilen = secenekler[position2];
                    if (listenercvp != null) {
                        listenercvp.onCevapGeldiGrup(MainActivity.kullanicistatic.getKullaniciId(), mesaj.getMsjID(), MainActivity.kullanicistatic.getKullaniciAdi(),secilen);
                    }
                    dialog.dismiss();
                });
                dialog.show();
            });
        }
    }
    @Override
    public int getItemCount() {
        return tumMesajlar.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout gidenLayout, gelenLayout;
        TextView txtMiktarGiden, txtTarihGiden, txtAciklamaGiden, gidenSaat, gorulduDurumu,gonderenadigiden,ibantextgiden;
        TextView txtMiktarGelen, txtTarihGelen, txtAciklamaGelen, gelenSaat,gonderenadigelen,onaylayicigelen,ibantextgelen;
        TextView onay_gonderen_adgelen,onaygelenicerikgelen,onay_giden_gonderen_ad,onaygidenicerik;
        Button EVETgelen,HAYIRgelen;
        LinearLayout onaygiden,onaygelen,gidenlayoutsilme;
        ImageView ibanCopyIcongiden,ibanCopyIcongelen;

        public ViewHolder(View itemView) {
            super(itemView);

            gidenLayout = itemView.findViewById(R.id.mesajGidenLayout);
            gidenlayoutsilme=itemView.findViewById(R.id.gidenlayoutsilme);
            gelenLayout = itemView.findViewById(R.id.mesajGelenLayout);

            txtMiktarGiden = itemView.findViewById(R.id.txtBorcMiktariGiden);
            gonderenadigiden=itemView.findViewById(R.id.gonderenadigiden);
            txtTarihGiden = itemView.findViewById(R.id.txtOdenecekTarihGiden);
            ibantextgiden=itemView.findViewById(R.id.ibantextgiden);
            ibanCopyIcongiden=itemView.findViewById(R.id.ibanCopyIcongiden);
            txtAciklamaGiden = itemView.findViewById(R.id.txtAciklamaGiden);
            gidenSaat = itemView.findViewById(R.id.mesajGidenSaat);
            gorulduDurumu = itemView.findViewById(R.id.gorulduDurumu);

            txtMiktarGelen = itemView.findViewById(R.id.txtBorcMiktariGelen);
            gonderenadigelen=itemView.findViewById(R.id.gonderenadigelen);
            txtTarihGelen = itemView.findViewById(R.id.txtOdenecekTarihGelen);
            txtAciklamaGelen = itemView.findViewById(R.id.txtAciklamaGelen);
            ibantextgelen=itemView.findViewById(R.id.ibantextgelen);
            ibanCopyIcongelen=itemView.findViewById(R.id.ibanCopyIcongelen);
            gelenSaat = itemView.findViewById(R.id.mesajGelenSaat);
            onaylayicigelen=itemView.findViewById(R.id.onaylayicigelen);

            EVETgelen=itemView.findViewById(R.id.EVETgelen);
            HAYIRgelen=itemView.findViewById(R.id.HAYIRgelen);

            onaygiden=itemView.findViewById(R.id.onaygiden);
            onay_giden_gonderen_ad=itemView.findViewById(R.id.onay_giden_gonderen_ad);
            onaygidenicerik=itemView.findViewById(R.id.onaygidenicerik);
            onaygelen=itemView.findViewById(R.id.onaygelen);
            onay_gonderen_adgelen=itemView.findViewById(R.id.onay_gonderen_adgelen);
            onaygelenicerikgelen=itemView.findViewById(R.id.onaygelenicerikgelen);

        }
    }

    public void setCikilmisMi(boolean cikilmisMi) {
        CikilmisMi = cikilmisMi;
    }

    public boolean isCikilmisMi() {
        return CikilmisMi;
    }

    public void setProfilGec(ProfileGec profilGec) {
        this.profilGec = profilGec;
    }
}
