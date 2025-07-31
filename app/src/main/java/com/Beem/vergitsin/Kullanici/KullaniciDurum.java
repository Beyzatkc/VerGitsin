package com.Beem.vergitsin.Kullanici;

public class KullaniciDurum {
    private Boolean Cevirmici;
    private String sonGorulme;

    public KullaniciDurum(Boolean cevirmici, String sonGorulme) {
        Cevirmici = cevirmici;
        this.sonGorulme = sonGorulme;
    }

    public Boolean getCevirmici() {
        return Cevirmici;
    }

    public void setCevirmici(Boolean cevirmici) {
        Cevirmici = cevirmici;
    }

    public String getSonGorulme() {
        return sonGorulme;
    }

    public void setSonGorulme(String sonGorulme) {
        this.sonGorulme = sonGorulme;
    }
}
