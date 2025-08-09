package com.Beem.vergitsin.Gruplar;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GruplarYonetici {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GruplarAdapter adapter;

    public GruplarYonetici(GruplarAdapter adapter){
        this.adapter = adapter;
    }
    public GruplarYonetici(){}

    public void TumGruplariCek(Kullanici kullanici,Runnable islemTamam){
        db.collection("gruplar")
                .whereArrayContains("uyeler", kullanici.getKullaniciId())
                .get()
                .addOnSuccessListener(dokumanlar->{
                    for(QueryDocumentSnapshot dokuman : dokumanlar){
                        Grup grup = GrupNesneleriniOlustur(dokuman);
                        adapter.getGrupList().add(grup);
                        adapter.notifyDataSetChanged();
                    }
                    islemTamam.run();
                });
    }


    private Grup GrupNesneleriniOlustur(DocumentSnapshot dokuman){
        String name = dokuman.contains("grupAdi") ? dokuman.getString("grupAdi") : "İsim yok";

        Timestamp tarih = dokuman.contains("olusturmaTarihi") ? dokuman.getTimestamp("olusturmaTarihi") : null;

        String grupFoto = dokuman.contains("ppfoto") ? dokuman.getString("ppfoto") : "user";

        ArrayList<String> uyeler = dokuman.contains("uyeler") ? (ArrayList<String>) dokuman.get("uyeler") : new ArrayList<>();

        String gruphakkinda = dokuman.contains("hakkinda") ? dokuman.getString("hakkinda") : "Açık grup";

        Grup grup = new Grup();
        grup.setGrupAdi(name);
        grup.setOlusturmaTarihi(tarih);
        grup.setGrupFoto(grupFoto);
        grup.setUyeler(uyeler);
        grup.setGrupId(dokuman.getId());
        grup.setGrupFoto(grupFoto);
        grup.setOlusturan(dokuman.getString("olusturan"));
        grup.setGrupHakkinda(gruphakkinda);
        return grup;
    }
    private Grup GrupNesneleriniOlustur(DocumentSnapshot dokuman,Grup grup){
        String name = dokuman.contains("grupAdi") ? dokuman.getString("grupAdi") : "İsim yok";

        Timestamp tarih = dokuman.contains("olusturmaTarihi") ? dokuman.getTimestamp("olusturmaTarihi") : null;

        String grupFoto = dokuman.contains("ppfoto") ? dokuman.getString("ppfoto") : "user";

        ArrayList<String> uyeler = dokuman.contains("uyeler") ? (ArrayList<String>) dokuman.get("uyeler") : new ArrayList<>();

        String gruphakkinda = dokuman.contains("hakkinda") ? dokuman.getString("hakkinda") : "Açık grup";

        if(grup == null) new Grup();
        grup.setGrupAdi(name);
        grup.setOlusturmaTarihi(tarih);
        grup.setGrupFoto(grupFoto);
        grup.setUyeler(uyeler);
        grup.setGrupId(dokuman.getId());
        grup.setGrupFoto(grupFoto);
        grup.setOlusturan(dokuman.getString("olusturan"));
        grup.setGrupHakkinda(gruphakkinda);
        return grup;
    }

    public void GrupGuncelle(Grup grup,Runnable Guncellendi, Runnable GuncelleneMedi){
        Map<String, Object> yeniVeriler = new HashMap<>();
        yeniVeriler.put("ppfoto", grup.getGrupFoto());
        yeniVeriler.put("hakkinda", grup.getGrupHakkinda());
        db.collection("gruplar")
                .document(grup.getGrupId())
                .set(yeniVeriler, SetOptions.merge())
                .addOnSuccessListener(basarili ->{
                    db.collection("sohbetler")
                            .document(grup.getGrupId())
                            .set(yeniVeriler, SetOptions.merge())
                            .addOnSuccessListener(basarili2 ->{
                                Guncellendi.run();
                            })
                            .addOnFailureListener(hata ->{
                                GuncelleneMedi.run();
                            });
                })
                .addOnFailureListener(hata ->{
                    GuncelleneMedi.run();
                });
    }

    public void GruptanCik(Grup grup,Runnable Cikildi, Runnable Cikilinamadi){
        db.collection("gruplar")
                .document(grup.getGrupId())
                .update("uyeler", FieldValue.arrayRemove(MainActivity.kullanicistatic.getKullaniciId()))
                .addOnSuccessListener(documentReference ->{
                    db.collection("sohbetler")
                            .document(grup.getGrupId())
                            .update("katilimcilar", FieldValue.arrayRemove(MainActivity.kullanicistatic.getKullaniciId()));
                    Cikildi.run();
                })
                .addOnFailureListener(hata->{
                    Cikilinamadi.run();
                });
    }
    interface ArkListener{
        void arkListener(ArrayList<Kullanici> arklar);
    }
    public void GrupArkEklemekIcinArkCek(ArkListener arkListener){
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .get()
                .addOnSuccessListener(dokuman->{
                    ArrayList<String> arklar = dokuman.contains("arkadaslar") ? (ArrayList<String>) dokuman.get("arkadaslar") : new ArrayList<>();
                    ArrayList<Kullanici> arkadasNesneleri = new ArrayList<>();
                    for(String ark : arklar){
                        db.collection("users")
                                .document(ark)
                                .get()
                                .addOnSuccessListener(dokumant->{
                                    Kullanici arkadas = ArkadasNesneOlustur(dokumant);
                                    arkadasNesneleri.add(arkadas);
                                    if(arkadasNesneleri.size() == arklar.size()){
                                        arkListener.arkListener(arkadasNesneleri);
                                    }
                                });
                    }
                });
    }

    private Kullanici ArkadasNesneOlustur(DocumentSnapshot dokuman){
        String ID = dokuman.getId();
        String name = dokuman.contains("kullaniciAdi") ? dokuman.getString("kullaniciAdi") : "İsim yok";
        String profilFoto = dokuman.contains("ProfilFoto") ? dokuman.getString("ProfilFoto") : "user";
        Kullanici arkadas = new Kullanici();
        arkadas.setKullaniciId(ID);
        arkadas.setKullaniciAdi(name);
        arkadas.setProfilFoto(profilFoto);
        return arkadas;
    }

    public void GrupArkEkle(Grup grup, ArrayList<String> yeniUyeler, Runnable uyeAdapterGuncelle){
        Map<String, Long> girisZamanlari = new HashMap<>();
        for(String uye: yeniUyeler){
            girisZamanlari.put(uye, System.currentTimeMillis());
        }

        int toplam = yeniUyeler.size();
        final int[] tamamlanan = {0};

        DocumentReference grupRef = db.collection("gruplar").document(grup.getGrupId());
        DocumentReference sohbetRef = db.collection("sohbetler").document(grup.getGrupId());

        final Map<String, Long>[] yeniKatilimcilarRef = new Map[]{new HashMap<>()};
        final Map<String, Long>[] eskiKatilimcilarRef = new Map[]{new HashMap<>()};

        grupRef.get().addOnSuccessListener(dokuman -> {
            Map<String, Long> eskiKatilimcilar = (Map<String, Long>) dokuman.get("eskikatilimcilar");
            Map<String, Long> yeniKatilimcilar = (Map<String, Long>) dokuman.get("yenikatilimcilar");
            if (eskiKatilimcilar == null) eskiKatilimcilar = new HashMap<>();
            if (yeniKatilimcilar == null) yeniKatilimcilar = new HashMap<>();
            yeniKatilimcilar.putAll(girisZamanlari);
            yeniKatilimcilarRef[0] = yeniKatilimcilar;
            eskiKatilimcilarRef[0] = eskiKatilimcilar;

            for(String yeniler: yeniKatilimcilar.keySet()){
                eskiKatilimcilar.remove(yeniler);
            }

            for(String uye : yeniUyeler){
                grupRef.update("uyeler", FieldValue.arrayUnion(uye))
                        .addOnSuccessListener(dokumans ->{
                            sohbetRef.update("katilimcilar", FieldValue.arrayUnion(uye));

                            grup.getUyeler().add(uye);
                            tamamlanan[0]++;
                            uyeAdapterGuncelle.run();
                            if(tamamlanan[0] == toplam) {
                                grupRef.update("yenikatilimcilar", yeniKatilimcilarRef[0]);
                                grupRef.update("eskikatilimcilar", eskiKatilimcilarRef[0]);
                                sohbetRef.update("yenikatilimcilar", yeniKatilimcilarRef[0]);
                                sohbetRef.update("eskikatilimcilar", eskiKatilimcilarRef[0]);
                            }
                        });
            }
        });
    }

    public void GrupKisiCikar(Grup grup, ArrayList<String> cikacaklar, Runnable tamamdir) {
        Map<String, Long> cikisZamanlari = new HashMap<>();
        for (String uye : cikacaklar) {
            cikisZamanlari.put(uye, System.currentTimeMillis());
        }

        int toplam = cikacaklar.size();
        final int[] tamamlanan = {0};

        DocumentReference grupRef = db.collection("gruplar").document(grup.getGrupId());
        DocumentReference sohbetRef = db.collection("sohbetler").document(grup.getGrupId());

        final Map<String, Long>[] eskiKatilimcilarRef = new Map[]{new HashMap<>()};
        final Map<String, Long>[] yeniKatilimcilarRef = new Map[]{new HashMap<>()};

        grupRef.get().addOnSuccessListener(dokuman -> {
            Map<String, Long> eskiKatilimcilar = (Map<String, Long>) dokuman.get("eskikatilimcilar");
            Map<String, Long> yeniKatilimcilar = (Map<String, Long>) dokuman.get("yenikatilimcilar");
            if (yeniKatilimcilar == null) yeniKatilimcilar = new HashMap<>();
            if (eskiKatilimcilar == null) eskiKatilimcilar = new HashMap<>();
            eskiKatilimcilar.putAll(cikisZamanlari);

            for (String uye : cikacaklar) {
                yeniKatilimcilar.remove(uye);
            }

            yeniKatilimcilarRef[0] = yeniKatilimcilar;
            eskiKatilimcilarRef[0] = eskiKatilimcilar;

            for (String uye : cikacaklar) {
                grupRef.update("uyeler", FieldValue.arrayRemove(uye))
                        .addOnSuccessListener(a -> {
                            sohbetRef.update("katilimcilar", FieldValue.arrayRemove(uye));

                            grup.getUyeler().removeIf(id -> id.equals(uye));
                            System.out.println("boyut: " + grup.getUyeler().size());

                            tamamlanan[0]++;
                            tamamdir.run();

                            if (tamamlanan[0] == toplam) {
                                grupRef.update("eskikatilimcilar", eskiKatilimcilarRef[0]);
                                grupRef.update("yenikatilimcilar", yeniKatilimcilarRef[0]);
                                sohbetRef.update("eskikatilimcilar", eskiKatilimcilarRef[0]);
                                sohbetRef.update("yenikatilimcilar", yeniKatilimcilarRef[0]);
                            }
                        });
            }
        });
    }

    public void GrupSayisiKayit(int sayi){
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .update("GrupSayisi", sayi);
    }

    public void YeniGrupYoneticisi(String grupID,String ID){
        db.collection("gruplar")
                .document(grupID)
                .update("olusturan", ID);
    }

    public void GrupNesneKontrolu(Grup grup, Runnable tamamdir){
        if(grup.getOlusturan()!=null && !grup.getOlusturan().isEmpty()){
            return;
        }
        db.collection("gruplar")
                .document(grup.getGrupId())
                .get()
                .addOnSuccessListener(dokuman->{
                    GrupNesneleriniOlustur(dokuman,grup);
                    tamamdir.run();
                });
    }
}
