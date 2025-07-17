package com.Beem.vergitsin.Kullanici;

public class Kullanici {
    private String KullaniciId;
    private String KullaniciAdi;
    private String Email;
    private String Sifre;
    private String Bio;

    public Kullanici() {

    }

    public Kullanici(String kullaniciId, String kullaniciAdi, String email) {
        KullaniciId = kullaniciId;
        KullaniciAdi = kullaniciAdi;
        Email = email;
    }

    public String getKullaniciId() {
        return KullaniciId;
    }

    public void setKullaniciId(String kullaniciId) {
        KullaniciId = kullaniciId;
    }

    public String getKullaniciAdi() {
        return KullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        KullaniciAdi = kullaniciAdi;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getSifre() {
        return Sifre;
    }

    public void setSifre(String sifre) {
        Sifre = sifre;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }
}
