package com.Beem.vergitsin.BorcAlinanlar;

public class AlinanBorcModel {
    private String aciklama;
    private String miktar;
    private String odenecekTarih;
    private String zaman;

    public AlinanBorcModel(String aciklama, String miktar, String odenecekTarih, String zaman) {
        this.aciklama = aciklama;
        this.miktar = miktar;
        this.odenecekTarih = odenecekTarih;
        this.zaman = zaman;
    }

    public String getAciklama() {
        return aciklama;
    }

    public String getMiktar() {
        return miktar;
    }

    public String getOdenecekTarih() {
        return odenecekTarih;
    }

    public String getZaman() {
        return zaman;
    }
}
