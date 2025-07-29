package com.Beem.vergitsin.Sohbet;

import java.util.ArrayList;

public class Sohbet {
    private String sohbetID;
    private String kullaniciAdi;
    private Long sonmsjsaati;
    private String ppfoto;
    private String sonMesaj;
    private ArrayList<String> katilimcilar;
    private String tur;
    private int gorulmemisMesajSayisi;

    public Sohbet(String sohbetID, String kullaniciAdi, Long sonmsjsaati, String ppfoto, String sonMesaj, ArrayList<String> katilimcilar, String tur) {
        this.sohbetID = sohbetID;
        this.kullaniciAdi = kullaniciAdi;
        this.sonmsjsaati = sonmsjsaati;
        this.ppfoto = ppfoto;
        this.sonMesaj = sonMesaj;
        this.katilimcilar=katilimcilar;
        this.tur=tur;
    }
    public int getGorulmemisMesajSayisi() {
        return gorulmemisMesajSayisi;
    }

    public void setGorulmemisMesajSayisi(int gorulmemisMesajSayisi) {
        this.gorulmemisMesajSayisi = gorulmemisMesajSayisi;
    }


    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
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
