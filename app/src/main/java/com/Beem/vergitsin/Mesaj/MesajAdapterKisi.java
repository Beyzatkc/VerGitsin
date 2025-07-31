package com.Beem.vergitsin.Mesaj;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MesajAdapterKisi extends RecyclerView.Adapter<MesajAdapterKisi.ViewHolder>{
    private ArrayList<Mesaj> tumMesajlar;
    private Context context;
    private CevapGeldi listenercvp;
    private MesajSilmeGuncellemeKisi listenersilme;

    public MesajAdapterKisi(ArrayList<Mesaj>tumMesajlar, Context context) {
        this.tumMesajlar=tumMesajlar;
        this.context=context;
    }
    public void mesajEkle(Mesaj yeniMesaj) {
        tumMesajlar.add(yeniMesaj);
        notifyItemInserted(tumMesajlar.size() - 1);
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
    public void eskiMesajlariBasaEkle(ArrayList<Mesaj> mesajlar) {
        tumMesajlar.addAll(0, mesajlar);
        notifyItemRangeInserted(0, mesajlar.size());
    }

    public void setListenercvp(CevapGeldi listener) {
        this.listenercvp = listener;
    }

    public void setListenersil(MesajSilmeGuncellemeKisi listener) {
        this.listenersilme = listener;
    }

    public void guncelleMesajListesi(ArrayList<Mesaj> yeniListe) {
            tumMesajlar.clear();
            tumMesajlar.addAll(yeniListe);
            notifyDataSetChanged();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.herbi_mesaj_kisi, parent, false);
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

       if(mesaj.getIstegiAtanId().equals(benim)){
           holder.gelenLayout.setVisibility(View.GONE);
           holder.gidenLayout.setVisibility(View.VISIBLE);
           holder.txtMiktarGiden.setText(mesaj.getMiktar());
           holder.txtAciklamaGiden.setText(mesaj.getAciklama());
           Timestamp odemeTarihi = mesaj.getOdenecekTarih();
           String tarihStr = timestampToGunAyYil(odemeTarihi);
           holder.txtTarihGiden.setText(tarihStr);

           Long MesajSaati=mesaj.getZaman();
           String zaman=longToSaatDakika(MesajSaati);
           holder.gidenSaat.setText(zaman);
           if(mesaj.isCevabiVarMi()){
               holder.onaygiden.setVisibility(View.VISIBLE);
               holder.onaygidenicerik.setText(mesaj.getCevapicerigi());
           }else{
               holder.onaygiden.setVisibility(View.GONE);
           }
           if(tumMesajlar.get(tumMesajlar.size()-1).equals(mesaj)&&mesaj.isGoruldu()){
               holder.gorulduDurumu.setVisibility(View.VISIBLE);
           }else{
               holder.gorulduDurumu.setVisibility(View.GONE);
           }
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

                       editMiktar.setText(mesaj.getMiktar());
                       editAciklama.setText(mesaj.getAciklama());
                       editTarih.setText(timestampToGunAyYil(mesaj.getOdenecekTarih())); // kendi fonksiyonunla

                       builder.setView(dialogView);

                       builder.setPositiveButton("Güncelle", (dialog, which) -> {
                           String yeniMiktar = editMiktar.getText().toString().trim();
                           String yeniAciklama = editAciklama.getText().toString().trim();
                           String yeniTarihStr = editTarih.getText().toString().trim();

                           Pattern tarihDeseni = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
                           Matcher matcher = tarihDeseni.matcher(yeniTarihStr);

                           if (!matcher.matches()) {
                               Toast.makeText(view.getContext(), "Tarih formatı geçersiz! (gg/aa/yyyy)", Toast.LENGTH_SHORT).show();
                               return;
                           }

                           try {
                               SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                               sdf.setLenient(false); // 32/01/2025 gibi hatalı tarihleri de reddeder

                               Date girilenTarih = sdf.parse(yeniTarihStr);
                               Date bugun = new Date(); // şimdiki zaman

                               if (girilenTarih.before(sdf.parse(sdf.format(bugun)))) {
                                   Toast.makeText(view.getContext(), "Geçmiş bir tarih seçilemez!", Toast.LENGTH_SHORT).show();
                                   return;
                               }

                               mesaj.setOdenecekTarih(new Timestamp(girilenTarih));

                           } catch (ParseException e) {
                               Toast.makeText(view.getContext(), "Tarih çözümleme hatası!", Toast.LENGTH_SHORT).show();
                               e.printStackTrace();
                               return;
                           }
                           if (!yeniMiktar.isEmpty() && !yeniTarihStr.isEmpty()) {
                               mesaj.setMiktar(yeniMiktar);
                               mesaj.setAciklama(yeniAciklama);
                               try {
                                   SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                   Date date = sdf.parse(yeniTarihStr);
                                   mesaj.setOdenecekTarih(new Timestamp(date));
                               } catch (ParseException e) {
                                   e.printStackTrace();
                               }
                               listenersilme.onMesajGuncelleme(mesaj);
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
       }else {
           holder.gelenLayout.setVisibility(View.VISIBLE);
           holder.gidenLayout.setVisibility(View.GONE);
           holder.txtMiktarGelen.setText(mesaj.getMiktar());
           holder.txtAciklamaGelen.setText(mesaj.getAciklama());
           Timestamp odemeTarihi = mesaj.getOdenecekTarih();
           String tarihStr = timestampToGunAyYil(odemeTarihi);
           holder.txtTarihGelen.setText(tarihStr);

           Long MesajSaati = mesaj.getZaman();
           String zaman = longToSaatDakika(MesajSaati);
           holder.gelenSaat.setText(zaman);
           if (mesaj.isCevabiVarMi()) {
               holder.onaygelen.setVisibility(View.VISIBLE);
               holder.onaygelenicerikgelen.setText(mesaj.getCevapicerigi());
               holder.EVETgelen.setVisibility(View.GONE);
               holder.HAYIRgelen.setVisibility(View.GONE);
               holder.onaylayicigelen.setVisibility(View.VISIBLE);
           } else {
               holder.onaygelen.setVisibility(View.GONE);
               holder.EVETgelen.setVisibility(View.VISIBLE);
               holder.HAYIRgelen.setVisibility(View.VISIBLE);
               holder.onaylayicigelen.setVisibility(View.GONE);
           }
           holder.EVETgelen.setOnClickListener(b -> {
               if (listenercvp != null) {
                   listenercvp.onCevapGeldi(MainActivity.kullanicistatic.getKullaniciId(), mesaj.getMsjID(), MainActivity.kullanicistatic.getKullaniciAdi(),"Borç isteği onaylandı");
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
                       listenercvp.onCevapGeldi(MainActivity.kullanicistatic.getKullaniciId(), mesaj.getMsjID(), MainActivity.kullanicistatic.getKullaniciAdi(),secilen);
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
        LinearLayout gidenLayout, gelenLayout,gidenlayoutsilme;
        TextView txtMiktarGiden, txtTarihGiden, txtAciklamaGiden, gidenSaat, gorulduDurumu;
        TextView txtMiktarGelen, txtTarihGelen, txtAciklamaGelen, gelenSaat,onaylayicigelen;
        TextView onaygelenicerikgelen,onaygidenicerik;
        Button EVETgelen,HAYIRgelen;
        LinearLayout onaygiden,onaygelen;

        public ViewHolder(View itemView) {
            super(itemView);
            gidenlayoutsilme=itemView.findViewById(R.id.gidenlayoutsilme);
            gidenLayout = itemView.findViewById(R.id.mesajGidenLayout);
            gelenLayout = itemView.findViewById(R.id.mesajGelenLayout);

            txtMiktarGiden = itemView.findViewById(R.id.txtBorcMiktariGiden);
            txtTarihGiden = itemView.findViewById(R.id.txtOdenecekTarihGiden);
            txtAciklamaGiden = itemView.findViewById(R.id.txtAciklamaGiden);
            gidenSaat = itemView.findViewById(R.id.mesajGidenSaat);
            gorulduDurumu = itemView.findViewById(R.id.gorulduDurumu);

            txtMiktarGelen = itemView.findViewById(R.id.txtBorcMiktariGelen);
            txtTarihGelen = itemView.findViewById(R.id.txtOdenecekTarihGelen);
            txtAciklamaGelen = itemView.findViewById(R.id.txtAciklamaGelen);
            gelenSaat = itemView.findViewById(R.id.mesajGelenSaat);
            onaylayicigelen=itemView.findViewById(R.id.onaylayicigelen);

            EVETgelen=itemView.findViewById(R.id.EVETgelen);
            HAYIRgelen=itemView.findViewById(R.id.HAYIRgelen);

            onaygiden=itemView.findViewById(R.id.onaygiden);
            onaygidenicerik=itemView.findViewById(R.id.onaygidenicerik);

            onaygelen=itemView.findViewById(R.id.onaygelen);
            onaygelenicerikgelen=itemView.findViewById(R.id.onaygelenicerikgelen);
        }
    }

}
