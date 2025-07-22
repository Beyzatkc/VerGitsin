package com.Beem.vergitsin.Sohbet;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Mesaj.Mesaj;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class SohbetViewModel extends ViewModel {
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    MutableLiveData<ArrayList<Sohbet>>_sohbetler=new MutableLiveData<>();
    LiveData<ArrayList<Sohbet>>sohbetler(){return _sohbetler;}

    public void SohbetleriCek(){
        db.collection("sohbetler")
                .whereArrayContains("katilimcilar", MainActivity.kullanicistatic.getKullaniciId())
                .orderBy("sonMsjSaati", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Hata oluştu", e);
                        return;
                    }
                    ArrayList<Sohbet> liste = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String sohbetId = doc.getString("sohbetId");
                        String kullaniciAdi = doc.getString("kullaniciAdi");
                        String ppfoto = doc.getString("ppfoto");
                        String sonMesaj = doc.getString("sonMesaj");
                        Long sonMsjSaati = doc.getLong("sonMsjSaati");
                        ArrayList<String> katilimcilar = (ArrayList<String>) doc.get("katilimcilar");
                        Sohbet sohbet =new Sohbet(sohbetId,kullaniciAdi,sonMsjSaati,ppfoto,sonMesaj,katilimcilar);
                        liste.add(sohbet);
                    }
                    _sohbetler.setValue(liste);
                });
    }
}