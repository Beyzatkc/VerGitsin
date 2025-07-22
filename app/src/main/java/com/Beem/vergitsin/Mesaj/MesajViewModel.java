package com.Beem.vergitsin.Mesaj;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.MainActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MesajViewModel extends ViewModel {
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    MutableLiveData<ArrayList<Mesaj>>_tumMesajlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>tumMesajlar(){return _tumMesajlar;}

    MutableLiveData<String>_Adi=new MutableLiveData<>();
    LiveData<String>Adi(){return _Adi;}
    MutableLiveData<String>_Profil=new MutableLiveData<>();
    LiveData<String>Profil(){return _Profil;}

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

}