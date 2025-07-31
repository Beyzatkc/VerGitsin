package com.Beem.vergitsin.Gruplar;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.google.firebase.Timestamp;
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
                        GrupNesneleriniOlustur(dokuman);
                    }
                    islemTamam.run();
                });
    }


    private void GrupNesneleriniOlustur(DocumentSnapshot dokuman){
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
        adapter.getGrupList().add(grup);
        adapter.notifyDataSetChanged();
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
        for(String uye : yeniUyeler) {
            System.out.println(uye);
            db.collection("gruplar")
                    .document(grup.getGrupId())
                    .update("uyeler", FieldValue.arrayUnion(uye))
                    .addOnSuccessListener(dok -> {
                        db.collection("sohbetler")
                                .document(grup.getGrupId())
                                .update("katilimcilar", FieldValue.arrayUnion(uye))
                                .addOnSuccessListener(dokuman -> {
                                });
                        grup.getUyeler().add(uye);
                        uyeAdapterGuncelle.run();
                    });
        }
    }

    public void GrupKisiCikar(Grup grup, ArrayList<String> cikacaklar,Runnable tamamdir){
        for(String uye : cikacaklar) {
            db.collection("gruplar")
                    .document(grup.getGrupId())
                    .update("uyeler", FieldValue.arrayRemove(uye))
                    .addOnSuccessListener(dok -> {
                        db.collection("sohbetler")
                                .document(grup.getGrupId())
                                .update("katilimcilar", FieldValue.arrayRemove(uye))
                                .addOnSuccessListener(dokuman -> {
                                });
                        for (int i=0; i<grup.getUyeler().size(); i++){
                            if(grup.getUyeler().get(i).equals(uye)){
                                grup.getUyeler().remove(i);
                                break;
                            }
                        }
                        System.out.println("boyut: "+grup.getUyeler().size());
                        tamamdir.run();
                    });
        }
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
}
