package com.Beem.vergitsin.Mesaj;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.KullaniciDurum;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MesajViewModel extends ViewModel {
    private boolean ilkTetikleme = true;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    MutableLiveData<ArrayList<Mesaj>>_tumMesajlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>tumMesajlar(){return _tumMesajlar;}

    MutableLiveData<String>_AliciId=new MutableLiveData<>();
    LiveData<String>AliciID(){return _AliciId;}

    MutableLiveData<KullaniciDurum>_durum=new MutableLiveData<>();
    LiveData<KullaniciDurum>durum(){return _durum;}

    MutableLiveData<Mesaj>_eklenenMesaj=new MutableLiveData<>();
    LiveData<Mesaj>eklenen(){return _eklenenMesaj;}
    MutableLiveData<Mesaj>_guncellenenMesaj=new MutableLiveData<>();
    LiveData<Mesaj>guncellenen(){return _guncellenenMesaj;}
    MutableLiveData<Mesaj>_silinenMesaj=new MutableLiveData<>();
    LiveData<Mesaj>silinen(){return _silinenMesaj;}


    public void MesajBorcistekleriDbCek(String aktifSohbetId) {
        db.collection("sohbetler")
                .document(aktifSohbetId)
                .collection("borc_istekleri")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
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
    public void CevrimiciSongorulmeDb(String bakilanID){
        db.collection("users")
                .document(bakilanID)
                .addSnapshotListener((snapshot, error) -> {
                    if (snapshot != null && snapshot.exists()) {
                        boolean cevrimiciDurumu =false;
                        String sonGorulmeTarihi;
                        Boolean online = snapshot.getBoolean("cevrimici");
                        Timestamp sonGorulme=snapshot.getTimestamp("sonGorulme");
                        if (online != null) {
                            cevrimiciDurumu = online;
                        }
                        if (sonGorulme != null&&online==false) {
                            sonGorulmeTarihi = TimeStampiSaate(sonGorulme);
                        } else {
                            sonGorulmeTarihi = null;
                        }
                        KullaniciDurum kd=new KullaniciDurum(cevrimiciDurumu,sonGorulmeTarihi);
                        _durum.setValue(kd);
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
    public void SohbetSonMesajiBulma(){

    }


    private String TimeStampiSaate(Timestamp gorulme){
        if (gorulme == null) return "";
        Date date = gorulme.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}