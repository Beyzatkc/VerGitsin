package com.Beem.vergitsin.BorcAlinanlar;

import com.Beem.vergitsin.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class BorcAlinanlarYonetici {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void TumAlinanBorclariCek(){
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .collection("AlinanBorclar")
                .get()
                .addOnSuccessListener(dokumanlar->{

                });
    }
}
