package com.Beem.vergitsin.Mesaj;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MesajViewModel extends ViewModel {
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    MutableLiveData<ArrayList<Mesaj>>_tumMesajlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>tumMesajlar(){return _tumMesajlar;}

    MutableLiveData<String>_Adi=new MutableLiveData<>();
    LiveData<String>Adi(){return _Adi;}
    MutableLiveData<String>_Profil=new MutableLiveData<>();
    LiveData<String>Profil(){return _Profil;}

    MutableLiveData<String>_AliciId=new MutableLiveData<>();
    LiveData<String>AliciID(){return _AliciId;}

    public void MesajBorcistekleriDbCek (String aktifSohbetId){
            ArrayList<Mesaj> tumMesajlar = new ArrayList<>();
            db.collection("borc_istekleri")
                    .whereEqualTo("sohbetId", aktifSohbetId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            tumMesajlar.add(documentToMesaj(doc));
                        }
                        Collections.sort(tumMesajlar, Comparator.comparingLong(Mesaj::getZaman));

                        _tumMesajlar.setValue(tumMesajlar);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Mesajlar çekilirken hata oluştu", e);
                    });
    }
        public Mesaj documentToMesaj(DocumentSnapshot doc){
            String atanid = doc.getString("istekatanID");
            String atilanid = doc.getString("istekatılanID");
            String aciklama = doc.getString("aciklama");
            String miktar = doc.getString("miktar");
            Timestamp tarih = doc.getTimestamp("tarih");
            Long mesajAtilmaZamani=doc.getLong("isteginAtildigiZaman");

            return new Mesaj(atanid, atilanid, aciklama, miktar, tarih, mesajAtilmaZamani,false);
    }
    public void IDdenAdaUlasma(String ID){
        db.collection("users")
                .document(ID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String Adi=documentSnapshot.getString("kullaniciAdi");
                    String pp=documentSnapshot.getString("ProfilFoto");
                    _Adi.setValue(Adi);
                    _Profil.setValue(pp);
                }) .addOnFailureListener(e -> {
                    Log.e("Firestore", "Kullanıcı verisi çekilirken hata", e);
                });
    }
    public void BorcIstekleriDb(UyariMesaj uyariMesaj,String istekatan, String istekatilan, String miktar, String aciklama, Timestamp tarih, String ad, String sohbetId, Long zaman){
        uyariMesaj.YuklemeDurum("");
        Map<String, Object> borcData = new HashMap<>();
        borcData.put("istekatanAdi",ad);
        borcData.put("istekatanID", istekatan);
        borcData.put("istekatılanID",istekatilan);
        borcData.put("aciklama", aciklama);
        borcData.put("miktar", miktar);
        borcData.put("odenecektarih", tarih);
        borcData.put("isteginAtildigiZaman",zaman);

        db.collection("sohbetler")
                .document(sohbetId)
                .collection("borc_istekleri")
                .add(borcData)
                .addOnSuccessListener(aVoid -> {
                    uyariMesaj.BasariliDurum("",1000);
                    Log.d("Firestore", "Borç isteği başarıyla kaydedildi: " );
                })
                .addOnFailureListener(e -> {
                    uyariMesaj.BasarisizDurum("",1000);
                    Log.e("Firestore", "Borç isteği kaydedilirken hata: ", e);
                });
    }
    public void SohbetIDsindenAliciya(String sohbetID){
        db.collection("sohbetler").document(sohbetID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> katilimcilar = (ArrayList<String>) documentSnapshot.get("katilimcilar");

                        if (katilimcilar != null) {
                            for (String kisiId : katilimcilar) {
                                if (!kisiId.equals(MainActivity.kullanicistatic.getKullaniciId())) {
                                   _AliciId.setValue(kisiId);
                                    return;
                                }
                            }
                        }
                    } else {
                        _AliciId.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Sohbet bulunamadı veya hata oluştu", e);
                    _AliciId.setValue(null);
                });

    }

}