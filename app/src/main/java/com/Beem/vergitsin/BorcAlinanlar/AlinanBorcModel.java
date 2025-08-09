package com.Beem.vergitsin.BorcAlinanlar;

import com.google.firebase.Timestamp;

public class AlinanBorcModel {
    private String ID;
    private String kullaniciId;
    private String aciklama;
    private String alinanAdi;
    private String miktar;
    private Timestamp odenecekTarih;
    private String iban;
    private boolean odendiMi;
    private Timestamp tarih;

    public AlinanBorcModel() {
    }

    public AlinanBorcModel(String kullaniciId, String aciklama, String miktar, Timestamp odenecekTarih, Timestamp tarih, boolean odendiMi, String iban) {
        this.kullaniciId = kullaniciId;
        this.aciklama = aciklama;
        this.miktar = miktar;
        this.odenecekTarih = odenecekTarih;
        this.tarih = tarih;
        this.odendiMi = odendiMi;
        this.iban = iban;
    }

    public String getKullaniciId() {
        return kullaniciId;
    }

    public String getAciklama() {
        return aciklama;
    }

    public String getMiktar() {
        return miktar;
    }

    public Timestamp getOdenecekTarih() {
        return odenecekTarih;
    }

    public Timestamp getTarih() {
        return tarih;
    }

    public void setKullaniciId(String kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public void setMiktar(String miktar) {
        this.miktar = miktar;
    }

    public void setOdenecekTarih(Timestamp odenecekTarih) {
        this.odenecekTarih = odenecekTarih;
    }

    public void setTarih(Timestamp tarih) {
        this.tarih = tarih;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getIban() {
        return iban;
    }

    public void setOdendiMi(boolean odendiMi) {
        this.odendiMi = odendiMi;
    }

    public boolean isOdendiMi() {
        return odendiMi;
    }

    public void setAlinanAdi(String alinanAdi) {
        this.alinanAdi = alinanAdi;
    }

    public String getAlinanAdi() {
        return alinanAdi;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
