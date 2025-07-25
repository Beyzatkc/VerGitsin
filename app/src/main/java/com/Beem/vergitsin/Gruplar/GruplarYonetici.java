package com.Beem.vergitsin.Gruplar;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class GruplarYonetici {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GruplarAdapter adapter;

    public GruplarYonetici(GruplarAdapter adapter){
        this.adapter = adapter;
    }

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

        String grupFoto = dokuman.contains("ProfilFoto") ? dokuman.getString("ProfilFoto") : "user";

        ArrayList<String> uyeler = dokuman.contains("uyeler") ? (ArrayList<String>) dokuman.get("uyeler") : new ArrayList<>();

        String gruphakkinda = dokuman.contains("gruphakkinda") ? dokuman.getString("gruphakkinda") : "Açık grup";

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
}
