package com.Beem.vergitsin;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MesajSohbetOrtakView extends ViewModel {
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private  MutableLiveData<Map<String, Pair<String, Long>>> sonMesajlar = new MutableLiveData<>(new HashMap<>());


    public void setSonMesaj(String sohbetId, String mesaj, Long saat) {
        Map<String, Pair<String, Long>> eskiMap = sonMesajlar.getValue();
        Map<String, Pair<String, Long>> yeniMap = new HashMap<>();
        if (eskiMap != null) {
            yeniMap.putAll(eskiMap);
        }
        yeniMap.put(sohbetId, new Pair<>(mesaj, saat));
        sonMesajlar.setValue(yeniMap);
    }

    public LiveData<Map<String, Pair<String, Long>>> getTumSonMesajlar() {
        return sonMesajlar;
    }
    public void sonMsjDbKaydi(String sohbetId, String yeniSonMesaj, Long yeniSonMsjSaati) {
        DocumentReference docRef = db.collection("sohbetler").document(sohbetId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String mevcutMesaj = documentSnapshot.getString("sonMesaj");
                Long mevcutSaat = documentSnapshot.getLong("sonMsjSaati");

                if (!Objects.equals(mevcutMesaj, yeniSonMesaj) || !Objects.equals(mevcutSaat, yeniSonMsjSaati)) {
                    Map<String, Object> guncelleme = new HashMap<>();
                    guncelleme.put("sonMesaj", yeniSonMesaj);
                    guncelleme.put("sonMsjSaati", yeniSonMsjSaati);

                    docRef.update(guncelleme)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Son mesaj ve saat güncellendi"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Güncelleme hatası", e));
                } else {
                    Log.d("Firestore", "Son mesajda değişiklik yok, güncellenmedi");
                }
            } else {
                Log.w("Firestore", "Belge bulunamadı: " + sohbetId);
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Belge okunamadı", e));
    }

}
