package com.Beem.vergitsin.Sohbet;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Sohbet {
    private String sohbetID;
    private String kullaniciAdi;
    private Long sonmsjsaati;
    private String ppfoto;
    private String sonMesaj;
    private ArrayList<String> katilimcilar;

    public Sohbet(String sohbetID, String kullaniciAdi, Long sonmsjsaati, String ppfoto, String sonMesaj,ArrayList<String> katilimcilar) {
        this.sohbetID = sohbetID;
        this.kullaniciAdi = kullaniciAdi;
        this.sonmsjsaati = sonmsjsaati;
        this.ppfoto = ppfoto;
        this.sonMesaj = sonMesaj;
        this.katilimcilar=katilimcilar;
    }

    public ArrayList<String> getKatilimcilar() {
        return katilimcilar;
    }

    public void setKatilimcilar(ArrayList<String> katilimcilar) {
        this.katilimcilar = katilimcilar;
    }

    public String getSohbetID() {
        return sohbetID;
    }

    public void setSohbetID(String sohbetID) {
        this.sohbetID = sohbetID;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public Long getSonmsjsaati() {
        return sonmsjsaati;
    }

    public void setSonmsjsaati(Long sonmsjsaati) {
        this.sonmsjsaati = sonmsjsaati;
    }

    public String getPpfoto() {
        return ppfoto;
    }

    public void setPpfoto(String ppfoto) {
        this.ppfoto = ppfoto;
    }

    public String getSonMesaj() {
        return sonMesaj;
    }

    public void setSonMesaj(String sonMesaj) {
        this.sonMesaj = sonMesaj;
    }
}
