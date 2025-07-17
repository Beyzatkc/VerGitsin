package com.Beem.vergitsin.Profil;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ProfilYonetici {

    private static ProfilYonetici yonetici;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Kullanici kullanici;
    private Runnable profilTamamdir;

    public static ProfilYonetici getYonetici(){
        if(yonetici==null){
            yonetici = new ProfilYonetici();
        }
        return yonetici;
    }

    public void ProfilDoldur(Runnable profilTamamdir){
        this.profilTamamdir = profilTamamdir;
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

        String profilFoto = dokuman.contains("ProfilFoto") ? dokuman.getString("ProfilFoto") : "profil_adam";

        int grupSayisi = dokuman.contains("GrupSayisi") && dokuman.getLong("GrupSayisi") != null
                ? dokuman.getLong("GrupSayisi").intValue() : 0;

        int arkSayisi = dokuman.contains("ArkSayisi") && dokuman.getLong("ArkSayisi") != null
                ? dokuman.getLong("ArkSayisi").intValue() : 0;

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
                .set(yeniVeriler, SetOptions.merge());
    }

    public Kullanici getKullanici(){
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici){
        this.kullanici = kullanici;
    }

}
