package com.Beem.vergitsin.Sohbet;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.Kullanici.KullaniciDurum;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Mesaj.Mesaj;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

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

}