
package com.Beem.vergitsin.Sohbet;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.KullaniciDurum;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Mesaj.Mesaj;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SohbetViewModel extends ViewModel {
    private boolean ilkTetikleme = true;
    private String bos;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    MutableLiveData<ArrayList<Sohbet>>_sohbetler=new MutableLiveData<>();
    LiveData<ArrayList<Sohbet>>sohbetler(){return _sohbetler;}


    MutableLiveData<Sohbet>_eklenenSohbet=new MutableLiveData<>();
    LiveData<Sohbet>eklenenSohbet(){return _eklenenSohbet;}
    MutableLiveData<Sohbet>_guncellenenSohbet=new MutableLiveData<>();
    LiveData<Sohbet>guncellenenSohbet(){return _guncellenenSohbet;}
    MutableLiveData<Sohbet>_silinenSohbet=new MutableLiveData<>();
    LiveData<Sohbet>silinenSohbet(){return _silinenSohbet;}
    private Map<String, ListenerRegistration> listenerMap = new HashMap<>();
    private Map<String, ListenerRegistration> listenerMapGrup = new HashMap<>();

    private  MutableLiveData<Sohbet> _gorulmeyenMesajSayilari = new MutableLiveData<>();
    public LiveData<Sohbet> getGorulmeyenMesajSayilari() {
        return _gorulmeyenMesajSayilari;
    }

    private  MutableLiveData<Sohbet> _gorulmeyenMesajSayilariGrup = new MutableLiveData<>();
    public LiveData<Sohbet> getGorulmeyenMesajSayilariGrup() {
        return _gorulmeyenMesajSayilariGrup;
    }


    public void SohbetleriCek() {
        db.collection("sohbetler")
                .whereArrayContains("katilimcilar", MainActivity.kullanicistatic.getKullaniciId())
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Mesajlar dinlenirken hata oluştu", error);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        if(ilkTetikleme) {
                            ArrayList<Sohbet> tumSohbetler = new ArrayList<>();
                            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                                DocumentSnapshot doc = change.getDocument();
                                ArrayList<Map<String, Object>> gizleyenler = (ArrayList<Map<String, Object>>) doc.get("gizleyenler");
                                if (gizleyenler != null) {
                                    boolean gizleniyor = false;
                                    for (Map<String, Object> obj : gizleyenler) {
                                        if (MainActivity.kullanicistatic.getKullaniciId().equals(obj.get("id"))) {
                                            gizleniyor = true;
                                            break;
                                        }
                                    }
                                    if (gizleniyor) {
                                        continue;
                                    }
                                }
                                String tur = doc.getString("tur");
                                String sohbetId = doc.getString("sohbetId");
                                String kullaniciAdi = doc.getString("kullaniciAdi");
                                String ppfoto = doc.getString("ppfoto");
                                String sonMesaj = doc.getString("sonMesaj");
                                Long sonMsjSaati = doc.getLong("sonMsjSaati");
                                ArrayList<String> katilimcilar = (ArrayList<String>) doc.get("katilimcilar");
                                ArrayList<Map<String, Object>> acilmaZamanlari = (ArrayList<Map<String, Object>>) doc.get("acilmaZamanlari");
                                Sohbet sohbet = new Sohbet(sohbetId, kullaniciAdi, sonMsjSaati, ppfoto, sonMesaj, katilimcilar, tur);
                                Long kendiAcilmaZamani = null;
                                if (acilmaZamanlari != null) {
                                    for (Map<String, Object> acilma : acilmaZamanlari) {
                                        String id = (String) acilma.get("id");
                                        if (MainActivity.kullanicistatic.getKullaniciId().equals(id)) {
                                            Object zamanObj = acilma.get("acilmaZamani");
                                            if (zamanObj instanceof Number) {
                                                kendiAcilmaZamani = ((Number) zamanObj).longValue(); // Long türüne dönüştür
                                            }
                                            sohbet.setAcilmazamani(kendiAcilmaZamani);
                                        }
                                    }
                                }
                                if(tur.equals("kisi")) {
                                    GorulmeyenMesajSayisi(sohbet);
                                }else{
                                    GorulmeyenMesajSayisiGrup(sohbet);
                                }
                                tumSohbetler.add(sohbet);
                            }
                            Collections.sort(tumSohbetler, Comparator.comparingLong(Sohbet::getSonmsjsaati));
                            _sohbetler.setValue(tumSohbetler);
                            ilkTetikleme = false;
                        } else {
                            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                                DocumentSnapshot doc = change.getDocument();
                                ArrayList<Map<String, Object>> gizleyenler = (ArrayList<Map<String, Object>>) doc.get("gizleyenler");
                                if (gizleyenler != null) {
                                    boolean gizleniyor = false;
                                    for (Map<String, Object> obj : gizleyenler) {
                                        if (MainActivity.kullanicistatic.getKullaniciId().equals(obj.get("id"))) {
                                            gizleniyor = true;
                                            break;
                                        }
                                    }
                                    if (gizleniyor) {
                                        continue;
                                    }
                                }

                                String tur = doc.getString("tur");
                                String sohbetId = doc.getString("sohbetId");
                                String kullaniciAdi = doc.getString("kullaniciAdi");
                                String ppfoto = doc.getString("ppfoto");
                                String sonMesaj = doc.getString("sonMesaj");
                                Long sonMsjSaati = doc.getLong("sonMsjSaati");
                                ArrayList<String> katilimcilar = (ArrayList<String>) doc.get("katilimcilar");

                                Sohbet yeniSohbet = new Sohbet(sohbetId, kullaniciAdi, sonMsjSaati, ppfoto, sonMesaj, katilimcilar, tur);
                                switch (change.getType()) {
                                    case ADDED:
                                        if(tur.equals("kisi")){
                                            GorulmeyenMesajSayisi(yeniSohbet);
                                        }else{
                                            GorulmeyenMesajSayisiGrup(yeniSohbet);
                                        }
                                        _eklenenSohbet.setValue(yeniSohbet);
                                        break;
                                    case MODIFIED:
                                        _guncellenenSohbet.setValue(yeniSohbet);
                                        break;
                                    case REMOVED:
                                        _silinenSohbet.setValue(yeniSohbet);
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    public void GorulmeyenMesajSayisi(Sohbet sohbet) {
        if(listenerMap.containsKey(sohbet.getSohbetID())){
            return;
        }
        final boolean[] gizlemeKaldirildiMi = {false};
        ListenerRegistration listenerRegistration = db.collection("sohbetler")
                .document(sohbet.getSohbetID())
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.DESCENDING)
                .limit(101)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Hata oluştu", e);
                        return;
                    }
                    if(sohbet.getSohbeteGirildiMi()){
                        return;
                    }
                    if (snapshots != null) {
                        Long gizlenmeZamani = null;
                        ArrayList<Map<String, Object>> gizleyenler = sohbet.getGizleyenler();
                        if (gizleyenler != null) {
                            for (Map<String, Object> obj : gizleyenler) {
                                if (MainActivity.kullanicistatic.getKullaniciId().equals(obj.get("id"))) {
                                    Object zamanObj = obj.get("gizlenmeZamani");
                                    if (zamanObj instanceof Number) {
                                        gizlenmeZamani = ((Number) zamanObj).longValue();
                                    }
                                    break;
                                }
                            }
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            DocumentSnapshot doc = dc.getDocument();
                            System.out.println(doc.getId());
                            boolean gorulduMu = doc.contains("GorulduMu") ? (boolean) doc.get("GorulduMu") : false;
                            if(gorulduMu) continue;
                            if(dc.getType()!=DocumentChange.Type.ADDED) continue;
                            String atanId = doc.getString("istekatanID");
                            Long mesajZamani = doc.getLong("isteginAtildigiZaman");

                            if (gizlenmeZamani != null && mesajZamani <= gizlenmeZamani) {
                                continue;
                            }
                            if (!atanId.equals(MainActivity.kullanicistatic.getKullaniciId())) {
                                int sayi=sohbet.getGorulmemisMesajSayisi();
                                sayi++;
                                sohbet.setGorulmemisMesajSayisi(sayi);
                                if (!gizlemeKaldirildiMi[0]) {
                                    gizlemeKaldirildiMi[0] = true;
                                    Map<String, Object> silinecekObj = new HashMap<>();
                                    silinecekObj.put("id", MainActivity.kullanicistatic.getKullaniciId());
                                    silinecekObj.put("gizlenmeZamani", gizlenmeZamani);

                                    db.collection("sohbetler")
                                            .document(sohbet.getSohbetID())
                                            .update("gizleyenler", FieldValue.arrayRemove(silinecekObj))
                                            .addOnSuccessListener(aVoid ->{
                                                Map<String, Object> yeniObj = new HashMap<>();
                                                yeniObj.put("id", MainActivity.kullanicistatic.getKullaniciId());
                                                yeniObj.put("acilmaZamani", System.currentTimeMillis());

                                                db.collection("sohbetler")
                                                        .document(sohbet.getSohbetID())
                                                        .update("acilmaZamanlari", FieldValue.arrayUnion(yeniObj));
                                            })
                                            .addOnFailureListener(err -> Log.e("Gizleme", "Gizleme kaldırılamadı", err));
                                }
                            }
                        }
                        _gorulmeyenMesajSayilari.setValue(sohbet);
                    }
                });
        listenerMap.put(sohbet.getSohbetID(), listenerRegistration);
    }
    public void GorulmeyenMesajSayisiGrup(Sohbet sohbet) {
        if(listenerMapGrup.containsKey(sohbet.getSohbetID())){
            return;
        }
        final boolean[] gizlemeKaldirildiMi = {false};
        String kendiId = MainActivity.kullanicistatic.getKullaniciId();

        ListenerRegistration listenerRegistration = db.collection("sohbetler")
                .document(sohbet.getSohbetID())
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.DESCENDING)
                .limit(101)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Hata oluştu", e);
                        return;
                    }
                    if(sohbet.getSohbeteGirildiMi()){
                        return;
                    }
                    if (snapshots != null) {
                        Long gizlenmeZamani = null;
                        ArrayList<Map<String, Object>> gizleyenler = sohbet.getGizleyenler();
                        if (gizleyenler != null) {
                            for (Map<String, Object> obj : gizleyenler) {
                                if (kendiId.equals(obj.get("id"))) {
                                    Object zamanObj = obj.get("gizlenmeZamani");
                                    if (zamanObj instanceof Number) {
                                        gizlenmeZamani = ((Number) zamanObj).longValue();
                                    }
                                    break;
                                }
                            }
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            DocumentSnapshot doc = dc.getDocument();
                            if(dc.getType()!=DocumentChange.Type.ADDED) continue;
                            Map<String, Boolean> gorulmeler = (Map<String, Boolean>) doc.get("gorulmeler");
                            if(gorulmeler==null){
                                gorulmeler=new HashMap<>();
                            }
                            String atanid=doc.getString("istekatanID");
                            Long mesajZamani = doc.getLong("isteginAtildigiZaman");

                            if (gizlenmeZamani != null && mesajZamani <= gizlenmeZamani) {
                                continue;
                            }
                            boolean gorulmedi = (gorulmeler == null) &&!atanid.equals(kendiId) || (!Boolean.TRUE.equals(gorulmeler.get(kendiId)) && !atanid.equals(kendiId));
                            if (gorulmedi) {
                                int sayi=sohbet.getGorulmemisMesajSayisi();
                                sayi++;
                                sohbet.setGorulmemisMesajSayisi(sayi);
                                if (!gizlemeKaldirildiMi[0]) {
                                    gizlemeKaldirildiMi[0] = true;
                                    Map<String, Object> silinecekObj = new HashMap<>();
                                    silinecekObj.put("id", MainActivity.kullanicistatic.getKullaniciId());
                                    silinecekObj.put("gizlenmeZamani", gizlenmeZamani);

                                    db.collection("sohbetler")
                                            .document(sohbet.getSohbetID())
                                            .update("gizleyenler", FieldValue.arrayRemove(silinecekObj))
                                            .addOnSuccessListener(aVoid -> Log.d("Gizleme", "Gizleme kaldırıldı"))
                                            .addOnFailureListener(err -> Log.e("Gizleme", "Gizleme kaldırılamadı", err));
                                }
                            }
                        }
                        _gorulmeyenMesajSayilariGrup.setValue(sohbet);
                    }
                });
        listenerMapGrup.put(sohbet.getSohbetID(), listenerRegistration);
    }
    public void SohbetSilme(Sohbet sohbet) {
        DocumentReference sohbetRef = db.collection("sohbetler").document(sohbet.getSohbetID());

        Map<String, Object> gizleyenObjesi = new HashMap<>();
        gizleyenObjesi.put("id", MainActivity.kullanicistatic.getKullaniciId());
        gizleyenObjesi.put("gizlenmeZamani", System.currentTimeMillis());

        sohbetRef.update("gizleyenler", FieldValue.arrayUnion(gizleyenObjesi))
                .addOnSuccessListener(aVoid ->{
                    if (sohbet.getGizleyenler() == null) {
                        sohbet.setGizleyenler(new ArrayList<>());
                    }
                    sohbet.getGizleyenler().add(gizleyenObjesi);
                })
                .addOnFailureListener(e -> Log.e("SohbetSilme", "Gizleme hatası: ", e));
    }


}