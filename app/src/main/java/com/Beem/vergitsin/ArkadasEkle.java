package com.Beem.vergitsin;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ArkadasEkle {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void ArkadasEklemeDb(Kullanici kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayUnion(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                });
    }

    public void ArkadasCikarmaDb(Kullanici kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayRemove(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                });

    }

}
