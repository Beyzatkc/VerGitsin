package com.Beem.vergitsin.Mesaj;

import com.google.firebase.Timestamp;

import java.util.Map;

public class Mesaj {
    private String msjID;
    private String istegiAtanId;
    private String istekatilanID;
    private String aciklama;
    private String miktar;
    private Timestamp odenecekTarih;
    private String cevap;
    private long zaman;
    private boolean goruldu;
    private boolean cevabiVarMi;
    public String istekAtanAdi;
    public Map<String, Boolean> gorulmeler;

    public Mesaj() {}

    public Mesaj(String istegiAtanId, String istekatilanID, String aciklama, String miktar, Timestamp odenecekTarih, long zaman, boolean goruldu,String cevap,String msjID) {
        this.istegiAtanId = istegiAtanId;
        this.istekatilanID = istekatilanID;
        this.aciklama = aciklama;
        this.miktar = miktar;
        this.odenecekTarih = odenecekTarih;
        this.zaman = zaman;
        this.goruldu = goruldu;
        this.cevap=cevap;
        this.msjID=msjID;
    }

    public Map<String, Boolean> getGorulmeler() {
        return gorulmeler;
    }

    public void setGorulmeler(Map<String, Boolean> gorulmeler) {
        this.gorulmeler = gorulmeler;
    }


    public String getIstekAtanAdi() {
        return istekAtanAdi;
    }

    public void setIstekAtanAdi(String istekAtanAdi) {
        this.istekAtanAdi = istekAtanAdi;
    }

    public boolean isCevabiVarMi() {
        return cevabiVarMi;
    }

    public void setCevabiVarMi(boolean cevabiVarMi) {
        this.cevabiVarMi = cevabiVarMi;
    }

    public String getMsjID() {
        return msjID;
    }

    public void setMsjID(String msjID) {
        this.msjID = msjID;
    }

    public String getCevap() {
        return cevap;
    }

    public void setCevap(String cevap) {
        this.cevap = cevap;
    }

    public String getIstegiAtanId() {
        return istegiAtanId;
    }

    public void setIstegiAtanId(String istegiAtanId) {
        this.istegiAtanId = istegiAtanId;
    }

    public String getIstekatilanID() {
        return istekatilanID;
    }

    public void setIstekatilanID(String istekatilanID) {
        this.istekatilanID = istekatilanID;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getMiktar() {
        return miktar;
    }

    public void setMiktar(String miktar) {
        this.miktar = miktar;
    }

    public Timestamp getOdenecekTarih() {
        return odenecekTarih;
    }

    public void setOdenecekTarih(Timestamp odenecekTarih) {
        this.odenecekTarih = odenecekTarih;
    }

    public long getZaman() {
        return zaman;
    }

    public void setZaman(long zaman) {
        this.zaman = zaman;
    }

    public boolean isGoruldu() {

        return goruldu;
    }

    public void setGoruldu(boolean goruldu) {
        this.goruldu = goruldu;
    }
}
