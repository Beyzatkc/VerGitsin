package com.Beem.vergitsin;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ArkadasEkle {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ArkadasEkle ekle;
    public static ArkadasEkle getEkle(){
        if(ekle==null){
            ekle = new ArkadasEkle();
        }
        return ekle;
    }

    public void ArkadasEklemeDb(Kullanici kullanici){
        if(kullanici.isKarsiTarafEngellediMi()){
            return;
        } else if (kullanici.isEngelliMi()) {
            return;
        }
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        DocumentReference arkadasDocRef = db.collection("users").document(kullanici.getKullaniciId());
        kendiDocRef
                .get()
                .addOnSuccessListener(dokuman->{
                    ArrayList<String> engelliler = dokuman.contains("engelliler") && dokuman.get("engelliler") != null ? (ArrayList<String>) dokuman.get("engelliler") : new ArrayList<>();
                    if(engelliler.contains(kullanici.getKullaniciId())){
                        kullanici.setEngelliMi(true);
                        return;
                    }
                    arkadasDocRef
                            .get()
                            .addOnSuccessListener(dokumantasyon->{
                                ArrayList<String> engelledikleri = dokumantasyon.contains("engelliler") && dokumantasyon.get("engelliler") != null ? (ArrayList<String>) dokumantasyon.get("engelliler") : new ArrayList<>();
                                if(engelledikleri.contains(MainActivity.kullanicistatic.getKullaniciId())){
                                    kullanici.setEngelliMi(true);
                                    return;
                                }
                                kendiDocRef.update("arkadaslar", FieldValue.arrayUnion(kullanici.getKullaniciId()))
                                        .addOnSuccessListener(fonks->{
                                           kullanici.setArkdasMi(true);
                                        });
                            });
                });
    }

    public void ArkadasCikarmaDb(Kullanici kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayRemove(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                    kullanici.setArkdasMi(false);
                });

    }

}
