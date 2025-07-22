package com.Beem.vergitsin.Mesaj;

import com.google.firebase.Timestamp;

public class Mesaj {
    private String istegiAtanId;
    private String istekatilanID;
    private String aciklama;
    private String miktar;
    private Timestamp odenecekTarih;
    private long zaman;
    private boolean goruldu;

    public Mesaj() {}

    public Mesaj(String istegiAtanId, String istekatilanID, String aciklama, String miktar, Timestamp odenecekTarih, long zaman, boolean goruldu) {
        this.istegiAtanId = istegiAtanId;
        this.istekatilanID = istekatilanID;
        this.aciklama = aciklama;
        this.miktar = miktar;
        this.odenecekTarih = odenecekTarih;
        this.zaman = zaman;
        this.goruldu = goruldu;
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
