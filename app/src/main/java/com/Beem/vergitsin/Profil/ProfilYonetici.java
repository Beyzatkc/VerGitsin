package com.Beem.vergitsin.Profil;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.SharedPreferencesK;
import com.Beem.vergitsin.MainActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfilYonetici {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Kullanici kullanici;
    private Runnable profilTamamdir;
    private Runnable profilBilgileriGuncelle;
    private ArrayList<String> kullanicininEnellilerListesi;

   public ProfilYonetici(Kullanici kullanici){
       this.kullanici = kullanici;
   }

    public void ProfilDoldur(Runnable profilTamamdir, Runnable profilBilgileriGuncelle){
        this.profilTamamdir = profilTamamdir;
        this.profilBilgileriGuncelle = profilBilgileriGuncelle;
        KullaniciNesnesiDoldur();
    }

    private void KullaniciNesnesiDoldur(){
        db.collection("users")
                .document(kullanici.getKullaniciId())
                .get()
                .addOnSuccessListener(dokumantasyonlar->{
                    if (dokumantasyonlar.exists()) {
                        Dokumantasyon(dokumantasyonlar);
                    }
                });

    }

    private void Dokumantasyon(DocumentSnapshot dokuman){
        String name = dokuman.contains("kullaniciAdi") ? dokuman.getString("kullaniciAdi") : "İsim yok";
        String email = dokuman.contains("email") ? dokuman.getString("email") : "Email yok";
        String bio = dokuman.contains("Bio") ? dokuman.getString("Bio") : "";

        String profilFoto = dokuman.contains("ProfilFoto") ? dokuman.getString("ProfilFoto") : "user";

        int grupSayisi = dokuman.contains("GrupSayisi") && dokuman.getLong("GrupSayisi") != null
                ? dokuman.getLong("GrupSayisi").intValue() : 0;

        int arkSayisi = dokuman.contains("arkadaslar") && dokuman.get("arkadaslar") != null
                ? ((ArrayList<String>) dokuman.get("arkadaslar")).size() : 0;

        int borcSayisi = dokuman.contains("BorcSayisi") && dokuman.getLong("BorcSayisi") != null
                ? dokuman.getLong("BorcSayisi").intValue() : 0;

        kullanici.setKullaniciAdi(name);
        kullanici.setEmail(email);
        kullanici.setBio(bio);
        kullanici.setProfilFoto(profilFoto);
        kullanici.setGrupSayisi(grupSayisi);
        kullanici.setArkSayisi(arkSayisi);
        kullanici.setBorcSayisi(borcSayisi);
        profilTamamdir.run();
        profilBilgileriGuncelle.run();
    }
    public void ProfilDuzenle(String newUsername, String newBio, String newFoto){
        kullanici.setKullaniciAdi(newUsername);
        kullanici.setBio(newBio);
        kullanici.setProfilFoto(newFoto);
        profilTamamdir.run();
        Map<String, Object> yeniVeriler = new HashMap<>();
        yeniVeriler.put("kullaniciAdi", newUsername);
        yeniVeriler.put("Bio", newBio);
        yeniVeriler.put("ProfilFoto", newFoto);
        db.collection("users")
                .document(kullanici.getKullaniciId())
                .set(yeniVeriler, SetOptions.merge())
                .addOnSuccessListener(basarili ->{
                    profilBilgileriGuncelle.run();
                });
    }

    public Kullanici getKullanici(){
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici){
        this.kullanici = kullanici;
    }

    public void CikisYap(Context context){
        SharedPreferencesK yerelKayit = new SharedPreferencesK(context);
        yerelKayit.cikisYap();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void Engelle(Runnable engellendi, Runnable engelkalkti){
       if(kullanici.isEngelliMi()){
           db.collection("users")
                   .document(MainActivity.kullanicistatic.getKullaniciId())
                   .update("engelliler", FieldValue.arrayRemove(kullanici.getKullaniciId()))
                   .addOnSuccessListener(basarili->{
                       kullanici.setEngelliMi(false);
                       engelkalkti.run();
                   });
       }
       else{
           db.collection("users")
                   .document(MainActivity.kullanicistatic.getKullaniciId())
                   .update("engelliler", FieldValue.arrayUnion(kullanici.getKullaniciId()))
                   .addOnSuccessListener(basarili->{
                       kullanici.setEngelliMi(true);
                       kullanici.setArkdasMi(false);
                       db.collection("users")
                               .document(MainActivity.kullanicistatic.getKullaniciId())
                               .update("arkadaslar", FieldValue.arrayRemove(kullanici.getKullaniciId()));
                       engellendi.run();
                   });
       }
    }

    public void KarsiTarafEngelKontrol(Runnable islemTamamdir){
       if(kullanici.isKarsiTarafEngellediMi()){
           islemTamamdir.run();
           return;
       }
       db.collection("users")
               .document(kullanici.getKullaniciId())
               .get()
               .addOnSuccessListener(dokuman->{
                   kullanicininEnellilerListesi = dokuman.contains("engelliler") && dokuman.get("engelliler") != null ? (ArrayList<String>) dokuman.get("engelliler") : new ArrayList<>();
                   kullanici.setKarsiTarafEngellediMi(kullanicininEnellilerListesi.contains(MainActivity.kullanicistatic.getKullaniciId()));
                   islemTamamdir.run();
               });

    }

}
