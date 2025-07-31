package com.Beem.vergitsin.Arkadaslar;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ArkadaslarYonetici {

    private ArkadaslarAdapter adapter;
    private ArkadaslarListenir listenir;
    private ArrayList<String> engelliler;

    public ArkadaslarYonetici(ArkadaslarAdapter adapter, ArkadaslarListenir listenir){
        this.adapter = adapter;
        this.listenir = listenir;
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void ArkadaslarimGetir(){
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .get()
                .addOnSuccessListener(dokumantasyonlar->{
                    if (dokumantasyonlar.exists()) {
                        ArrayList<String> arkadaslarListesi = (ArrayList<String>) dokumantasyonlar.get("arkadaslar");
                        if (arkadaslarListesi != null) {
                            listenir.onArkadaslarSayisi(arkadaslarListesi.size());
                            for (String arkadasId : arkadaslarListesi) {
                                db.collection("users")
                                        .document(arkadasId)
                                        .get()
                                        .addOnSuccessListener(dokumantasyon->{
                                            Dokumantasyon(dokumantasyon,true,false);
                                            adapter.notifyDataSetChanged();
                                        });
                            }
                        }
                    }
                });
    }

    private void Dokumantasyon(DocumentSnapshot dokuman, boolean arkadasmi,boolean engelliMi){
        Arkadas arkadas = new Arkadas();
        arkadas.setArkdasMi(arkadasmi);
        arkadas.setEngelliMi(engelliMi);

        String ID = dokuman.getId();
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

        arkadas.setKullaniciId(ID);
        arkadas.setKullaniciAdi(name);
        arkadas.setEmail(email);
        arkadas.setBio(bio);
        arkadas.setProfilFoto(profilFoto);
        arkadas.setGrupSayisi(grupSayisi);
        arkadas.setArkSayisi(arkSayisi);
        arkadas.setBorcSayisi(borcSayisi);
        System.out.println(arkadas.getKullaniciAdi());
        adapter.getKullanicilar().add(arkadas);
    }


    public void DigerKullanicininArkadaslari(Kullanici arkadas){
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .get()
                .addOnSuccessListener(dokuman->{
                    engelliler = dokuman.contains("engelliler") && dokuman.get("engelliler") != null ? (ArrayList<String>) dokuman.get("engelliler") : new ArrayList<>();
                    db.collection("users")
                            .document(arkadas.getKullaniciId())
                            .get()
                            .addOnSuccessListener(dokum->{
                                if (dokum.exists()) {
                                    ArrayList<String> arkadaslarListesi = (ArrayList<String>) dokum.get("arkadaslar");
                                    if (arkadaslarListesi != null) {
                                        db.collection("users")
                                                .document(MainActivity.kullanicistatic.getKullaniciId())
                                                .get()
                                                .addOnSuccessListener(dokumantasyonlar->{
                                                    if (dokumantasyonlar.exists()) {
                                                        ArrayList<String> arkadaslarimListesi = (ArrayList<String>) dokumantasyonlar.get("arkadaslar");
                                                        if (arkadaslarimListesi != null) {
                                                            for (String arkadasId : arkadaslarListesi) {
                                                                boolean ortakMi = arkadaslarimListesi.contains(arkadasId);
                                                                boolean engelliMi = engelliler.contains(arkadasId);
                                                                db.collection("users")
                                                                        .document(arkadasId)
                                                                        .get()
                                                                        .addOnSuccessListener(dokumanK->{
                                                                            Dokumantasyon(dokumanK,ortakMi,engelliMi);
                                                                            adapter.notifyDataSetChanged();
                                                                        });
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                });
    }
}
