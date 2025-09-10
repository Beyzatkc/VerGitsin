package com.Beem.vergitsin.BorcVerilenler;

import android.widget.Toast;

import com.Beem.vergitsin.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BorcVerilenlerYonetici {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private VerilenBorcAdapter adapter;

    public BorcVerilenlerYonetici(VerilenBorcAdapter adapter) {
        this.adapter = adapter;
    }

    public void TumVerilenBorclariCek() {
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .collection("verilenler")
                .orderBy("odemeTarihi", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(dokumanlar -> {
                    for (DocumentSnapshot document : dokumanlar) {
                        dokumanVerisiniIsle(document);
                    }
                });
    }

    private void dokumanVerisiniIsle(DocumentSnapshot document) {
        if (document != null && document.exists()) {
            String aciklama = document.contains("aciklama") && document.get("aciklama") != null
                    ? document.getString("aciklama")
                    : "Açıklama yok";

            String kullaniciId = document.contains("kullaniciId") && document.get("kullaniciId") != null
                    ? document.getString("kullaniciId")
                    : "Kullanıcı ID yok";

            String miktar = document.contains("miktar") && document.get("miktar") != null
                    ? document.getString("miktar")
                    : "0";

            Timestamp odemeTarihi = document.contains("odemeTarihi") && document.get("odemeTarihi") != null
                    ? document.getTimestamp("odemeTarihi")
                    : null;

            Timestamp tarih = document.contains("tarih") && document.get("tarih") != null
                    ? document.getTimestamp("tarih")
                    : null;

            String iban = document.contains("iban") && document.get("iban") != null
                    ? document.getString("iban")
                    : "IBAN yok";

            boolean odendiMi = document.contains("odendiMi") && document.get("odendiMi") != null
                    ? document.getBoolean("odendiMi")
                    : false;

            VerilenBorcModel model = new VerilenBorcModel(kullaniciId, aciklama, miktar, odemeTarihi, tarih, odendiMi, iban);
            model.setID(document.getId());
            KullaniciAdiUlas(model);
        }
    }

    private void KullaniciAdiUlas(VerilenBorcModel model) {
        db.collection("users")
                .document(model.getKullaniciId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String kullaniciAdi = documentSnapshot.getString("kullaniciAdi");
                        model.setVerilenAdi(kullaniciAdi);
                        adapter.itemEkle(model);
                    }
                });
    }

    public void BorcuOde(VerilenBorcModel borcModel, int pozisyon) {
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .collection("verilenler")
                .document(borcModel.getID())
                .update("odendiMi", true)
                .addOnSuccessListener(s -> {
                    borcModel.setOdendiMi(true);
                    adapter.notifyItemChanged(pozisyon);
                });

        db.collection("users")
                .document(borcModel.getKullaniciId())
                .collection("alinanlar")
                .document(borcModel.getID())
                .update("odendiMi", true)
                .addOnSuccessListener(s -> {
                    borcModel.setOdendiMi(true);
                    adapter.notifyItemChanged(pozisyon);
                });
    }



}
