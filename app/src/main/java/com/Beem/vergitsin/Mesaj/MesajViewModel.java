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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MesajViewModel extends ViewModel {
    private boolean ilkTetikleme = true;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    MutableLiveData<ArrayList<Mesaj>>_tumMesajlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>tumMesajlar(){return _tumMesajlar;}

    MutableLiveData<ArrayList<Mesaj>>_eskimesajlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>eskiMesajlar(){return _eskimesajlar;}

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

    MutableLiveData<ArrayList<Mesaj>>_adlargeldi=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>adlargeldi(){return _adlargeldi;}
    MutableLiveData<ArrayList<Mesaj>>_adlargeldieskimsjlar=new MutableLiveData<>();
    LiveData<ArrayList<Mesaj>>adlargeldieskimsjlar(){return _adlargeldieskimsjlar;}

    MutableLiveData<Mesaj>_tamamlananMesaj=new MutableLiveData<>();
    LiveData<Mesaj>tamamlandimsj(){return _tamamlananMesaj;}

    private ListenerRegistration borcIstekleriListener;


    public void MesajBorcistekleriDbCek(String aktifSohbetId,Long gizlemeZamani){
        Query query = db.collection("sohbetler")
                .document(aktifSohbetId)
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.DESCENDING)
                .limit(3);

        borcIstekleriListener = query.addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", "Mesajlar dinlenirken hata oluştu", error);
                return;
            }
            if (queryDocumentSnapshots != null) {
                if (ilkTetikleme) {
                    ArrayList<Mesaj> tumMesajlar = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Mesaj mesaj=documentToMesaj(doc);
                        if (gizlemeZamani!=null){
                            if(mesaj.getZaman()> gizlemeZamani){
                                tumMesajlar.add(mesaj);
                            }
                        }
                        else{
                            tumMesajlar.add(mesaj);
                        }
                        GorulmeKontrolEtVeGuncelle(mesaj, aktifSohbetId, () -> {});
                    }
                    Collections.sort(tumMesajlar, Comparator.comparingLong(Mesaj::getZaman));

                    _tumMesajlar.setValue(tumMesajlar);
                    ilkTetikleme = false;
                } else {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        Mesaj mesaj = documentToMesaj(dc.getDocument());
                        switch (dc.getType()) {
                            case ADDED:
                                System.out.println("ADDED");
                                GorulmeKontrolEtVeGuncelle(mesaj, aktifSohbetId, () -> {
                                    _eklenenMesaj.setValue(mesaj);
                                });
                                break;
                            case MODIFIED:
                                System.out.println("MODIFIED");
                                GorulmeKontrolEtVeGuncelle(mesaj, aktifSohbetId,() -> {
                                    _guncellenenMesaj.setValue(mesaj);
                                });
                                break;
                            case REMOVED:
                                System.out.println("REMOVED");
                                _silinenMesaj.setValue(mesaj);
                                break;
                        }
                    }
                }
            }
        });
    }
    public void DinleyiciKaldir(){
        if (borcIstekleriListener != null) {
            borcIstekleriListener.remove();
            borcIstekleriListener = null;
        }
        ilkTetikleme = true;
    }
    public void GorulmeKontrolEtVeGuncelle(Mesaj mesaj, String aktifSohbetId, Runnable onComplete) {
        System.out.println("GorulmeKontrolEtVeGuncelle");
        String kendiId = MainActivity.kullanicistatic.getKullaniciId();
        if (!mesaj.getIstegiAtanId().equals(kendiId) && !mesaj.isGoruldu()) {
            mesaj.setGoruldu(true);
            db.collection("sohbetler")
                    .document(aktifSohbetId)
                    .collection("borc_istekleri")
                    .document(mesaj.getMsjID())
                    .update("GorulduMu", true)
                    .addOnSuccessListener(aVoid -> {
                        if (onComplete != null) onComplete.run();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Gorulme durumu güncellenemedi", e);
                        if (onComplete != null) onComplete.run(); // Hata olsa da devam et
                    });
        } else {
            if (onComplete != null) onComplete.run();
        }
    }


    public void EskiMesajlariYukle(String aktifSohbetId,Long zaman, Long gizlemeZamani){
        System.out.println("EskiMesajlariYukle");
        Query query = db.collection("sohbetler")
                .document(aktifSohbetId)
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.DESCENDING)
                .limit(3);

        if (zaman != null) {
            query = query.whereLessThan("isteginAtildigiZaman", zaman);
        }
        if (gizlemeZamani != null) {
            query = query.whereGreaterThan("isteginAtildigiZaman", gizlemeZamani);
        }
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Mesaj> eskiMesajlar = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                Mesaj mesaj = documentToMesaj(doc);
                eskiMesajlar.add(mesaj);
                GorulmeKontrolEtVeGuncelle(mesaj, aktifSohbetId, () -> {
                });
            }
            Collections.reverse(eskiMesajlar); // ters çeviriyoruz (eski->yeni)
            _eskimesajlar.setValue(eskiMesajlar);
        });
    }

    public Mesaj documentToMesaj(DocumentSnapshot doc){
        System.out.println("documentToMesaj");
           Map<String, Object> cevapVerenMap = (Map<String, Object>) doc.get("cevap_veren");

           String cevapId = null;
           String cevapAd = null;
           String cevapicerik=null;

           if (cevapVerenMap != null) {
            cevapId = (String) cevapVerenMap.get("id");
            cevapAd = (String) cevapVerenMap.get("ad");
            cevapicerik = (String) cevapVerenMap.get("icerik");
             }
            String id=doc.getId();
            String atanid = doc.getString("istekatanID");
            String atilanid = doc.getString("istekatılanID");
            String aciklama = doc.getString("aciklama");
            String miktar = doc.getString("miktar");
            Timestamp tarih = doc.getTimestamp("odenecektarih");
            String iban=doc.getString("iban");
            Long mesajAtilmaZamani=doc.getLong("isteginAtildigiZaman");
            Boolean gorulduMu=doc.getBoolean("GorulduMu");
             if (gorulduMu == null) {
                 gorulduMu = false;
            }
            Mesaj mesaj=new Mesaj(atanid, atilanid, aciklama, miktar, tarih, mesajAtilmaZamani,iban,gorulduMu,cevapId,id,cevapicerik,cevapAd);
            if(cevapAd!=null){
                mesaj.setCevabiVarMi(true);
            }else{
                mesaj.setCevabiVarMi(false);
            }
            return mesaj;
    }
    public void BorcIstekleriDb(UyariMesaj uyariMesaj,String istekatan, String istekatilan, String miktar, String aciklama, Timestamp tarih,String iban, String ad, String sohbetId, Long zaman){
        System.out.println("BorcIstekleriDb");
        uyariMesaj.YuklemeDurum("");
        Map<String, Object> borcData = new HashMap<>();
        borcData.put("istekatanAdi",ad);
        borcData.put("istekatanID", istekatan);
        borcData.put("istekatılanID",istekatilan);
        borcData.put("aciklama", aciklama);
        borcData.put("miktar", miktar);
        borcData.put("odenecektarih", tarih);
        borcData.put("iban",iban);
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
    public void SohbetIDsindenAliciya(String sohbetID){
        System.out.println("SohbetIDsindenAliciya");
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
    public void GelenCevabiKaydetme(String sohbetId, String borcIstekId, String cevapVerenId, String kullaniciAdi,String icerik) {
        Map<String, Object> cevapVeri = new HashMap<>();
        cevapVeri.put("id", cevapVerenId);
        cevapVeri.put("ad", kullaniciAdi);
        cevapVeri.put("icerik",icerik);

        FirebaseFirestore.getInstance()
                .collection("sohbetler")
                .document(sohbetId)
                .collection("borc_istekleri")
                .document(borcIstekId)
                .update("cevap_veren", cevapVeri)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Cevap başarıyla güncellendi.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Cevap güncellenirken hata oluştu: ", e);
                });
    }

    public void sonMsjDbKaydi(String sohbetId, String yeniSonMesaj, Long yeniSonMsjSaati) {
        System.out.println("sonMsjDbKaydi");
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
    public void MesajSilme(String sohbetid,Mesaj mesaj){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sohbetler")
                .document(sohbetid)
                .collection("borc_istekleri")
                .document(mesaj.getMsjID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });

    }
    public void MesajGuncelleme(String sohbetid,Mesaj mesaj){
        System.out.println("MesajGuncelleme");
        Map<String, Object> guncellemeVerisi = new HashMap<>();
        guncellemeVerisi.put("miktar", mesaj.getMiktar());
        guncellemeVerisi.put("aciklama", mesaj.getAciklama());
        guncellemeVerisi.put("odenecektarih", mesaj.getOdenecekTarih());
        guncellemeVerisi.put("iban",mesaj.getIban());

        db.collection("sohbetler")
                .document(sohbetid)
                .collection("borc_istekleri")
                .document(mesaj.getMsjID())
                .update(guncellemeVerisi)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });

    }
    public void AlinanlarVerilenlerKayit(String eveteBasanId,String istekGonderenId,String aciklama,String miktar,Timestamp odenecekTarih,String iban){
        String ortakId = db.collection("users").document().getId();
        DocumentReference verilenRef=db.collection("users")
                .document(eveteBasanId)
                .collection("verilenler")
                .document(ortakId);
        Map<String,Object>verilenverisi=new HashMap<>();
        verilenverisi.put("kullaniciId",istekGonderenId);
        verilenverisi.put("aciklama",aciklama);
        verilenverisi.put("miktar",miktar);
        verilenverisi.put("iban", iban);
        verilenverisi.put("odemeTarihi",odenecekTarih);
        verilenverisi.put("tarih", FieldValue.serverTimestamp());

        DocumentReference alinanRef=db.collection("users")
                .document(istekGonderenId)
                .collection("alinanlar")
                .document(ortakId);
        Map<String,Object>alinanverisi=new HashMap<>();

        alinanverisi.put("kullaniciId",eveteBasanId);
        alinanverisi.put("aciklama",aciklama);
        alinanverisi.put("miktar",miktar);
        alinanverisi.put("iban", iban);
        alinanverisi.put("odemeTarihi",odenecekTarih);
        alinanverisi.put("tarih", FieldValue.serverTimestamp());

        WriteBatch batch=db.batch();
        batch.set(verilenRef,verilenverisi);
        batch.set(alinanRef,alinanverisi);

        batch.commit().addOnSuccessListener(aVoid -> {
            Log.d("Firestore", "Verilen ve alınan başarılı şekilde eklendi.");
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Hata oluştu: " + e.getMessage());
        });

        db.collection("users")
                .document(eveteBasanId)
                .update("BorcSayisi", FieldValue.increment(Integer.valueOf(miktar)));

    }
    private String TimeStampiSaate(Timestamp gorulme) {
        if (gorulme == null) return "";

        Date date = gorulme.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Calendar now = Calendar.getInstance();

        SimpleDateFormat saatFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat tarihSaatFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());


        if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            return saatFormat.format(date);
        }

        now.add(Calendar.DAY_OF_YEAR, -1); // 1 gün geri git
        if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
            return "Dün " + saatFormat.format(date);
        }
        return tarihSaatFormat.format(date);
    }
}