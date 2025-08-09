package com.Beem.vergitsin.Sohbet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sohbet {
    private String sohbetID;
    private String kullaniciAdi;
    private Long sonmsjsaati;
    private String ppfoto;
    private String sonMesaj;
    private ArrayList<String> katilimcilar;
    private String tur;
    private int gorulmemisMesajSayisi;
    private Boolean sohbeteGirildiMi=false;
    private Long Acilmazamani;
    private Boolean gizlemeKaldirildiMi=false;
    private Boolean gizlendiMi=false;
    private Long sohbetGizlenmeZaman=0L;
    private boolean eskiGrubumMu=false;
    private boolean yeniGrubumMu=false;
    private long eskiGrupZaman=0;
    private boolean grupCikildiMi=false;

    public Sohbet(String sohbetID, String kullaniciAdi, Long sonmsjsaati, String ppfoto, String sonMesaj, ArrayList<String> katilimcilar, String tur) {
        this.sohbetID = sohbetID;
        this.kullaniciAdi = kullaniciAdi;
        this.sonmsjsaati = sonmsjsaati;
        this.ppfoto = ppfoto;
        this.sonMesaj = sonMesaj;
        this.katilimcilar=katilimcilar;
        this.tur=tur;
    }

    public Long getSohbetGizlenmeZaman() {
        return sohbetGizlenmeZaman;
    }

    public void setSohbetGizlenmeZaman(Long sohbetGizlenmeZaman) {
        this.sohbetGizlenmeZaman = sohbetGizlenmeZaman;
    }

    public Boolean getGizlendiMi() {
        return gizlendiMi;
    }

    public void setGizlendiMi(Boolean gizlendiMi) {
        this.gizlendiMi = gizlendiMi;
    }

    public Boolean getgizlemeKaldirildiMi() {
        return gizlemeKaldirildiMi;
    }

    public void setgizlemeKaldirildiMi(Boolean gizlendiMi) {
        this.gizlemeKaldirildiMi = gizlendiMi;
    }

    private ArrayList<Map<String, Object>> gizleyenler;

    public void setGizleyenler(ArrayList<Map<String, Object>> gizleyenler) {
        this.gizleyenler = gizleyenler;
    }

    public ArrayList<Map<String, Object>> getGizleyenler(){
        return this.gizleyenler;
    }

    public Long getAcilmazamani() {
        return Acilmazamani;
    }

    public void setAcilmazamani(Long acilmazamani) {
        Acilmazamani = acilmazamani;
    }

    public int getGorulmemisMesajSayisi() {
        return gorulmemisMesajSayisi;
    }

    public void setGorulmemisMesajSayisi(int gorulmemisMesajSayisi) {
        this.gorulmemisMesajSayisi = gorulmemisMesajSayisi;
    }

    public Boolean getSohbeteGirildiMi() {
        return sohbeteGirildiMi;
    }

    public void setSohbeteGirildiMi(Boolean sohbeteGirildiMi) {
        this.sohbeteGirildiMi = sohbeteGirildiMi;
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

    public void setEskiGrubumMu(boolean eskiGrubumMu) {
        this.eskiGrubumMu = eskiGrubumMu;
    }

    public void setYeniGrubumMu(boolean yeniGrubumMu) {
        this.yeniGrubumMu = yeniGrubumMu;
    }

    public boolean isEskiGrubumMu() {
        return eskiGrubumMu;
    }

    public boolean isYeniGrubumMu() {
        return yeniGrubumMu;
    }

    public void setEskiGrupZaman(long eskiGrupZaman) {
        this.eskiGrupZaman = eskiGrupZaman;
    }

    public long getEskiGrupZaman() {
        return eskiGrupZaman;
    }

    public void setGrupCikildiMi(boolean grupCikildiMi) {
        this.grupCikildiMi = grupCikildiMi;
    }

    public boolean isGrupCikildiMi() {
        return grupCikildiMi;
    }
}
