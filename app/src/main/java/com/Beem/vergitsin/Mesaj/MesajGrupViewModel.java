package com.Beem.vergitsin.Mesaj;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.Beem.vergitsin.Kullanici.Kullanici;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    private HashMap<String,String> IddenAdlari= new HashMap<>();

    private ArrayList<Mesaj> geciciEskiMesajListesi;
    public void setGeciciEskiMesajListesi(ArrayList<Mesaj> liste) {
        this.geciciEskiMesajListesi = liste;
    }
    private ListenerRegistration borcIstekleriListener;
    public ArrayList<Mesaj> getGeciciEskiMesajListesi() {
        return geciciEskiMesajListesi;
    }


    public void MesajBorcistekleriDbCek(String aktifSohbetId,Long gizlemeZamani){
        System.out.println("MesajBorcistekleriDbCek");
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
                    System.out.println("ilk tetikleme");
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
        db.collection("gruplar")
                .document(aktifSohbetId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> katilimcilar = (ArrayList<String>) documentSnapshot.get("uyeler");
                        ArrayList<String> gorecekler = new ArrayList<>();

                        for (String katilimci : katilimcilar) {
                            if (!katilimci.equals(mesaj.getIstegiAtanId())) {
                                gorecekler.add(katilimci);
                            }
                        }
                        if (gorecekler.contains(kendiId)) {
                            Map<String, Boolean> gorulmeler = mesaj.getGorulmeler();
                            Map<String, Boolean> gorulmeleradlar = mesaj.getadlar();
                            if (gorulmeler == null) {
                                gorulmeler = new HashMap<>();
                            }
                            if (gorulmeleradlar == null) {
                                gorulmeleradlar = new HashMap<>();
                            }

                            if (!Boolean.TRUE.equals(gorulmeler.get(kendiId))) {
                                gorulmeler.put(kendiId, true);
                                gorulmeleradlar.put(MainActivity.kullanicistatic.getKullaniciAdi(),true);
                                mesaj.setGorulmeler(gorulmeler);
                                mesaj.setadlar(gorulmeleradlar);
                                db.collection("sohbetler")
                                        .document(aktifSohbetId)
                                        .collection("borc_istekleri")
                                        .document(mesaj.getMsjID())
                                        .update("gorulmeler", gorulmeler)
                                        .addOnSuccessListener(aVoid -> {
                                            if (onComplete != null) onComplete.run();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Görülme durumu güncellenemedi", e);
                                            if (onComplete != null) onComplete.run();
                                        });
                            } else {
                                if (onComplete != null) onComplete.run();
                            }
                        } else {
                            if (onComplete != null) onComplete.run();
                        }

                    } else {
                        Log.e("Firestore", "Grup bulunamadı");
                        if (onComplete != null) onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Katılımcılar çekilirken hata oluştu", e);
                    if (onComplete != null) onComplete.run();
                });
    }


    public void EskiMesajlariYukle(String aktifSohbetId,Long zaman, Long gizlemeZamani){
        System.out.println("EskiMesajlariYukle");
        System.out.println(zaman+"-girdim-"+gizlemeZamani);
        Query query = db.collection("sohbetler")
                .document(aktifSohbetId)
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.DESCENDING)
                .limit(3);

        if (zaman != null && zaman!=0) {
            query = query.whereLessThan("isteginAtildigiZaman", zaman);
        }
        if (gizlemeZamani != null && gizlemeZamani!=0) {
            query = query.whereGreaterThan("isteginAtildigiZaman", gizlemeZamani);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Mesaj> eskiMesajlar = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                Mesaj mesaj=documentToMesaj(doc);
                System.out.println(mesaj.getMsjID());
                System.out.println(mesaj.getZaman()+"---");
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
        Long mesajAtilmaZamani=doc.getLong("isteginAtildigiZaman");
        String iban=doc.getString("iban");
        Mesaj mesaj=new Mesaj(atanid, atilanid, aciklama, miktar, tarih, mesajAtilmaZamani,iban,false,cevapId,id,cevapicerik,cevapAd);
        if(cevapAd!=null){
            mesaj.setCevabiVarMi(true);
        }else{
            mesaj.setCevabiVarMi(false);
        }
        Map<String, Boolean> gorulmeler = (Map<String, Boolean>) doc.get("gorulmeler");
        if (gorulmeler != null) {
            GorenlerinAdlariniBul(gorulmeler, mesaj, () -> {
                mesaj.setGorulmeler(gorulmeler);
            });
        } else {
            mesaj.setGorulmeler(new HashMap<>());
            mesaj.setadlar(new HashMap<>());
        }
        if(IddenAdlari.containsKey(mesaj.getIstegiAtanId())){
            mesaj.setIstekAtanAdi(IddenAdlari.get(mesaj.getIstegiAtanId()));
        }
        return mesaj;
    }
    public void GorenlerinAdlariniBul(Map<String, Boolean> gorulmeler,Mesaj mesaj,Runnable onComplete) {
        System.out.println("GorenlerinAdlariniBul");
        Map<String, Boolean> adlarigtr = new HashMap<>();
        mesaj.setadlar(adlarigtr);
        for(String key: gorulmeler.keySet()){
            if(IddenAdlari.containsKey(key)){
                mesaj.getadlar().put(IddenAdlari.get(key),gorulmeler.get(key));
            }
        }
        if (gorulmeler == null || gorulmeler.isEmpty()) return;
        int toplam = gorulmeler.size();
        AtomicInteger sayac = new AtomicInteger(0);
        for (Map.Entry<String, Boolean> entry : gorulmeler.entrySet()) {
            String kullaniciId = entry.getKey();
            Boolean gorulduMu = entry.getValue();
                db.collection("users")
                        .document(kullaniciId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String adSoyad = documentSnapshot.getString("kullaniciAdi");
                                adlarigtr.put(adSoyad, gorulduMu);
                                if (sayac.incrementAndGet() == toplam) {
                                     mesaj.setadlar(adlarigtr);
                                    if (onComplete != null) onComplete.run();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Hata", "Kullanıcı alınamadı: " + kullaniciId, e);
                            if (sayac.incrementAndGet() == toplam) {
                                if (onComplete != null) onComplete.run();
                            }
                        });
        }
    }

    public void sonMsjDbKaydi(String sohbetId, String yeniSonMesaj, Long yeniSonMsjSaati,String id, boolean guncellendiMi) {
        System.out.println("sonMsjDbKaydi");
        DocumentReference docRef = db.collection("sohbetler").document(sohbetId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String sonMesajID = documentSnapshot.contains("sonMesajID") ? documentSnapshot.getString("sonMesajID") : "_";

                if (!sonMesajID.equals(id) || guncellendiMi) {
                    System.out.println("son mesaj güncellendi");
                    Map<String, Object> guncelleme = new HashMap<>();
                    guncelleme.put("sonMesaj", yeniSonMesaj);
                    guncelleme.put("sonMesajID", id);
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
        System.out.println("IddenGonderenAdaUlasma - "+tip);
        AtomicInteger sayac = new AtomicInteger(0);
        for(int i=0;i<tummesajlar.size();i++){
            int finalI = i;
            db.collection("users")
                    .document(tummesajlar.get(i).getIstegiAtanId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String adi=documentSnapshot.getString("kullaniciAdi");
                        IddenAdlari.put(tummesajlar.get(finalI).getIstegiAtanId(),adi);
                        tummesajlar.get(finalI).setIstekAtanAdi(adi);
                        if (sayac.incrementAndGet() == tummesajlar.size()) {
                            if(tip.equals("mesajlar")) {
                                _tamamlandi.setValue(true);
                                System.out.println("_tamamlandi.setValue(true);");
                            }else if(tip.equals("eskimsjlar")){
                                _tamamlandieskimesajlar.setValue(true);
                                System.out.println("_tamamlandieskimesajlar.setValue(true);");
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
        System.out.println("IddenGonderenAdaUlasmaTekKisi");
        if(IddenAdlari.containsKey(mesaj.getIstegiAtanId())){
            mesaj.setIstekAtanAdi(IddenAdlari.get(mesaj.getIstegiAtanId()));
            _tamamlananMesaj.setValue(mesaj);
            return;
        }
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


    public void BorcIstekleriDb(UyariMesaj uyariMesaj, String istekatan, String istekatilan, String miktar, String aciklama, Timestamp tarih,String iban, String ad, String sohbetId, Long zaman){
        uyariMesaj.YuklemeDurum("");
        Map<String, Object> borcData = new HashMap<>();
        borcData.put("istekatanAdi",ad);
        borcData.put("istekatanID", istekatan);
        borcData.put("istekatılanID",istekatilan);
        borcData.put("aciklama", aciklama);
        borcData.put("miktar", miktar);
        borcData.put("odenecektarih", tarih);
        borcData.put("isteginAtildigiZaman",zaman);
        borcData.put("iban",iban);
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
        verilenverisi.put("iban",iban);
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
        alinanverisi.put("iban",iban);
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


    public void GruptanCikilmisMesajlar(String id, Long baslangicZ, Long bitisZ){
        Query query=  db.collection("sohbetler")
                .document(id)
                .collection("borc_istekleri")
                .orderBy("isteginAtildigiZaman", Query.Direction.ASCENDING)
                .limit(3);
        if(baslangicZ!=null){
            query = query.whereGreaterThan("isteginAtildigiZaman", baslangicZ);
            System.out.println("girildi");
        }
        if(bitisZ!=null){
            query = query.whereLessThan("isteginAtildigiZaman", bitisZ);
            System.out.println("girildi**");
        }
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Mesaj> mesajlar = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        System.out.println("Doc time: " + doc.getLong("isteginAtildigiZaman"));
                        Mesaj mesaj = documentToMesaj(doc);
                        mesajlar.add(mesaj);
                        System.out.println(mesaj.getMsjID());
                    }
                    _tumMesajlar.setValue((ArrayList<Mesaj>) mesajlar);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Mesajlar çekilirken hata", e);
                });
    }
}
















