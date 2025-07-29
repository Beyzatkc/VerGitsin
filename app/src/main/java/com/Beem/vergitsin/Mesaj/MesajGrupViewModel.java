package com.Beem.vergitsin.Mesaj;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MesajGrupViewModel extends ViewModel {
    private boolean ilkTetikleme = true;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    MutableLiveData<ArrayList<Mesaj>> _tumMesajlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>> tumMesajlar(){return _tumMesajlar;}

    MutableLiveData<ArrayList<Mesaj>>_eskimesajlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>eskiMesajlar(){return _eskimesajlar;}

    MutableLiveData<Integer>_kackisicevrimici=new MutableLiveData<>();
    LiveData<Integer>kackisicevrimici(){return _kackisicevrimici;}

    MutableLiveData<Boolean>_tamamlandi=new MutableLiveData<>();
    LiveData<Boolean>tamamlandi(){return _tamamlandi;}

    MutableLiveData<Boolean>_tamamlandieskimesajlar=new MutableLiveData<>();
    LiveData<Boolean>tamamlandieski(){return _tamamlandieskimesajlar;}

    MutableLiveData<Mesaj>_eklenenMesaj=new MutableLiveData<>();
    LiveData<Mesaj>eklenen(){return _eklenenMesaj;}
    MutableLiveData<Mesaj>_guncellenenMesaj=new MutableLiveData<>();
    LiveData<Mesaj>guncellenen(){return _guncellenenMesaj;}
    MutableLiveData<Mesaj>_silinenMesaj=new MutableLiveData<>();
    LiveData<Mesaj>silinen(){return _silinenMesaj;}

    MutableLiveData<Mesaj>_tamamlananMesaj=new MutableLiveData<>();
    LiveData<Mesaj>tamamlandimsj(){return _tamamlananMesaj;}

    private ArrayList<Mesaj> geciciEskiMesajListesi;
    public void setGeciciEskiMesajListesi(ArrayList<Mesaj> liste) {
        this.geciciEskiMesajListesi = liste;
    }
    public ArrayList<Mesaj> getGeciciEskiMesajListesi() {
        return geciciEskiMesajListesi;
    }


    public void MesajBorcistekleriDbCek(String aktifSohbetId){
        Query query = db.collection("sohbetler")
                .document(aktifSohbetId)
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.DESCENDING)
                .limit(3);

        query.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", "Mesajlar dinlenirken hata oluştu", error);
                return;
            }
            if (queryDocumentSnapshots != null) {
                if (ilkTetikleme) {
                    ArrayList<Mesaj> tumMesajlar = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        tumMesajlar.add(documentToMesaj(doc));
                    }
                    Collections.sort(tumMesajlar, Comparator.comparingLong(Mesaj::getZaman));
                    _tumMesajlar.setValue(tumMesajlar);
                    ilkTetikleme = false;
                } else {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        Mesaj mesaj = documentToMesaj(dc.getDocument());
                        switch (dc.getType()) {
                            case ADDED:
                                _eklenenMesaj.setValue(mesaj);
                                break;
                            case MODIFIED:
                                _guncellenenMesaj.setValue(mesaj);
                                break;
                            case REMOVED:
                                _silinenMesaj.setValue(mesaj);
                                break;
                        }
                    }
                }
            }
        });
    }
    public void EskiMesajlariYukle(String aktifSohbetId,Long zaman){
        Query query = db.collection("sohbetler")
                .document(aktifSohbetId)
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.DESCENDING)
                .limit(3);

        if (zaman != null) {
            query = query.whereLessThan("isteginAtildigiZaman", zaman);
        }
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Mesaj> eskiMesajlar = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                eskiMesajlar.add(documentToMesaj(doc));
            }

            Collections.reverse(eskiMesajlar); // ters çeviriyoruz (eski->yeni)

           /* ArrayList<Mesaj> mevcutMesajlar = _tumMesajlar.getValue();
            if (mevcutMesajlar == null) mevcutMesajlar = new ArrayList<>();

            mevcutMesajlar.addAll(0, eskiMesajlar); // eski mesajları başa ekle
            */
            _eskimesajlar.setValue(eskiMesajlar);

        });

    }


    public Mesaj documentToMesaj(DocumentSnapshot doc){
        String id=doc.getId();
        String cevap=doc.getString("cevap");
        String atanid = doc.getString("istekatanID");
        String atilanid = doc.getString("istekatılanID");
        String aciklama = doc.getString("aciklama");
        String miktar = doc.getString("miktar");
        Timestamp tarih = doc.getTimestamp("odenecektarih");
        Long mesajAtilmaZamani=doc.getLong("isteginAtildigiZaman");
        Mesaj mesaj=new Mesaj(atanid, atilanid, aciklama, miktar, tarih, mesajAtilmaZamani,false,cevap,id);
        if(cevap!=null){
            mesaj.setCevabiVarMi(true);
        }else{
            mesaj.setCevabiVarMi(false);
        }
        return mesaj;
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
    public void IddenGonderenAdaUlasma(ArrayList<Mesaj>tummesajlar,String tip){
        AtomicInteger sayac = new AtomicInteger(0);
        for(int i=0;i<tummesajlar.size();i++){
            int finalI = i;
            db.collection("users")
                    .document(tummesajlar.get(i).getIstegiAtanId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String adi=documentSnapshot.getString("kullaniciAdi");
                        tummesajlar.get(finalI).setIstekAtanAdi(adi);
                        if (sayac.incrementAndGet() == tummesajlar.size()) {
                            if(tip.equals("mesajlar")) {
                                _tamamlandi.setValue(true);
                            }else if(tip.equals("eskimsjlar")){
                                _tamamlandieskimesajlar.setValue(true);
                            }
                        }
                    }) .addOnFailureListener(e -> {
                        if (sayac.incrementAndGet() == tummesajlar.size()) {
                            _tamamlandi.setValue(false);
                            _tamamlandieskimesajlar.setValue(false);
                        }
                    });
        }

    }
    public void IddenGonderenAdaUlasmaTekKisi(Mesaj mesaj){
            db.collection("users")
                    .document(mesaj.getIstegiAtanId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String adi=documentSnapshot.getString("kullaniciAdi");
                        mesaj.setIstekAtanAdi(adi);
                        _tamamlananMesaj.setValue(mesaj);
                    }) .addOnFailureListener(e -> {
                        _tamamlananMesaj.setValue(null);
                    });

    }

    public void KacKisiCevrimiciGrup(String grupid) {
        db.collection("gruplar")
                .document(grupid)
                .addSnapshotListener((groupSnapshot, error) -> {
                    if (groupSnapshot != null && groupSnapshot.exists()) {
                        ArrayList<String> uyeIDleri = (ArrayList<String>) groupSnapshot.get("uyeler");
                        if (uyeIDleri != null) {
                            Map<String, Boolean> durumMap = new HashMap<>();

                            for (String uyeId : uyeIDleri) {
                                db.collection("users")
                                        .document(uyeId)
                                        .addSnapshotListener((snapshot, err) -> {
                                            if (snapshot != null && snapshot.exists()) {
                                                Boolean online = snapshot.getBoolean("cevrimici");
                                                durumMap.put(uyeId, online != null && online);
                                                int toplamCevrimici = 0;
                                                for (boolean durum : durumMap.values()) {
                                                    if (durum) toplamCevrimici++;
                                                }

                                                _kackisicevrimici.setValue(toplamCevrimici);
                                            }
                                        });
                            }
                        } else {
                            _kackisicevrimici.setValue(0);
                        }
                    }
                });
    }

    public void GelenCevabiKaydetme(String sohbetId,String borcIstekId,String cevap){
        FirebaseFirestore.getInstance()
                .collection("sohbetler")
                .document(sohbetId)
                .collection("borc_istekleri")
                .document(borcIstekId)
                .update("cevap", cevap)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Cevap başarıyla güncellendi.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Cevap güncellenirken hata oluştu: ", e);
                });
    }

    public void BorcIstekleriDb(UyariMesaj uyariMesaj, String istekatan, String istekatilan, String miktar, String aciklama, Timestamp tarih, String ad, String sohbetId, Long zaman){
        uyariMesaj.YuklemeDurum("");
        Map<String, Object> borcData = new HashMap<>();
        borcData.put("istekatanAdi",ad);
        borcData.put("istekatanID", istekatan);
        borcData.put("istekatılanID",istekatilan);
        borcData.put("aciklama", aciklama);
        borcData.put("miktar", miktar);
        borcData.put("odenecektarih", tarih);
        borcData.put("isteginAtildigiZaman",zaman);
        borcData.put("GorulduMu",false);

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
}