package com.Beem.vergitsin.BorcVerilenler;

import com.google.firebase.Timestamp;

public class VerilenBorcModel {
    private String ID;
    private String kullaniciId;
    private String aciklama;
    private String verilenAdi;
    private String miktar;
    private Timestamp odenecekTarih;
    private Timestamp tarih;
    private String iban;
    private boolean odendiMi;

    public VerilenBorcModel() {
    }

    public VerilenBorcModel(String kullaniciId, String aciklama, String miktar, Timestamp odenecekTarih, Timestamp tarih, boolean odendiMi, String iban) {
        this.kullaniciId = kullaniciId;
        this.aciklama = aciklama;
        this.miktar = miktar;
        this.odenecekTarih = odenecekTarih;
        this.tarih = tarih;
        this.odendiMi = odendiMi;
        this.iban = iban;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(String kullaniciId) {
        this.kullaniciId = kullaniciId;
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

    public Timestamp getTarih() {
        return tarih;
    }

    public void setTarih(Timestamp tarih) {
        this.tarih = tarih;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public boolean isOdendiMi() {
        return odendiMi;
    }

    public void setOdendiMi(boolean odendiMi) {
        this.odendiMi = odendiMi;
    }

    public String getVerilenAdi() {
        return verilenAdi;
    }

    public void setVerilenAdi(String verilenAdi) {
        this.verilenAdi = verilenAdi;
    }
}
