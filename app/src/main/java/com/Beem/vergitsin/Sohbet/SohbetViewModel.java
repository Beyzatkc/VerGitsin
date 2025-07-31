package com.Beem.vergitsin.Sohbet;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

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
import com.google.firebase.firestore.DocumentSnapshot;
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
                                    String tur = doc.getString("tur");
                                    String sohbetId = doc.getString("sohbetId");
                                    String kullaniciAdi = doc.getString("kullaniciAdi");
                                    String ppfoto = doc.getString("ppfoto");
                                    String sonMesaj = doc.getString("sonMesaj");
                                    Long sonMsjSaati = doc.getLong("sonMsjSaati");
                                    ArrayList<String> katilimcilar = (ArrayList<String>) doc.get("katilimcilar");
                                    Sohbet sohbet = new Sohbet(sohbetId, kullaniciAdi, sonMsjSaati, ppfoto, sonMesaj, katilimcilar, tur);
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
        ListenerRegistration listenerRegistration = db.collection("sohbetler")
                .document(sohbet.getSohbetID())
                .collection("borc_istekleri")
                .whereEqualTo("GorulduMu", false)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Hata oluştu", e);
                        return;
                    }
                    if(sohbet.getSohbeteGirildiMi()){
                        return;
                    }
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            String atanId = doc.getString("istekatanID");
                            if (!atanId.equals(MainActivity.kullanicistatic.getKullaniciId())) {
                                int sayi=sohbet.getGorulmemisMesajSayisi();
                                sayi++;
                                sohbet.setGorulmemisMesajSayisi(sayi);
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
        String kendiId = MainActivity.kullanicistatic.getKullaniciId();

        ListenerRegistration listenerRegistration = db.collection("sohbetler")
                .document(sohbet.getSohbetID())
                .collection("borc_istekleri")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Hata oluştu", e);
                        return;
                    }
                    if(sohbet.getSohbeteGirildiMi()){
                        return;
                    }
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            System.out.println(doc.getId());
                            Map<String, Boolean> gorulmeler = (Map<String, Boolean>) doc.get("gorulmeler");
                            String atanid=doc.getString("istekatanID");
                            boolean gorulmedi = (gorulmeler == null) &&!atanid.equals(kendiId) || (!Boolean.TRUE.equals(gorulmeler.get(kendiId)) && !atanid.equals(kendiId));
                            if (gorulmedi) {
                                int sayi=sohbet.getGorulmemisMesajSayisi();
                                sayi++;
                                sohbet.setGorulmemisMesajSayisi(sayi);
                            }
                        }
                        _gorulmeyenMesajSayilariGrup.setValue(sohbet);
                    }
                });
        listenerMapGrup.put(sohbet.getSohbetID(), listenerRegistration);
    }
}