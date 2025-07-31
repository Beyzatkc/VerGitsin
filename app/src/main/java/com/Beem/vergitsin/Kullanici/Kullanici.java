package com.Beem.vergitsin.Kullanici;

import java.io.Serializable;
import java.util.Objects;

public class Kullanici implements Serializable {
    private String KullaniciId;
    private String KullaniciAdi;
    private String Email;
    private String Sifre;
    private String Bio;
    private String ProfilFoto;
    private int GrupSayisi;
    private int ArkSayisi;
    private int BorcSayisi;
    private boolean arkdasMi = false;
    private boolean engelliMi = false;
    private boolean karsiTarafEngellediMi = false;

    public Kullanici() {
    }

    public Kullanici(String kullaniciId, String kullaniciAdi, String email) {
        KullaniciId = kullaniciId;
        KullaniciAdi = kullaniciAdi;
        Email = email;
    }
    public Kullanici(String kullaniciId, String kullaniciAdi, String email, String profilFoto) {
        KullaniciId = kullaniciId;
        KullaniciAdi = kullaniciAdi;
        Email = email;
        this.ProfilFoto = profilFoto;
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

    public String getProfilFoto() {
        return ProfilFoto;
    }

    public void setProfilFoto(String profilFoto) {
        ProfilFoto = profilFoto;
    }

    public int getGrupSayisi() {
        return GrupSayisi;
    }

    public void setGrupSayisi(int grupSayisi) {
        GrupSayisi = grupSayisi;
    }

    public int getArkSayisi() {
        return ArkSayisi;
    }

    public void setArkSayisi(int arkSayisi) {
        ArkSayisi = arkSayisi;
    }

    public int getBorcSayisi() {
        return BorcSayisi;
    }

    public void setBorcSayisi(int borcSayisi) {
        BorcSayisi = borcSayisi;
    }

    public void setArkdasMi(boolean arkdasMi) {
        this.arkdasMi = arkdasMi;
    }

    public boolean isArkdasMi() {
        return arkdasMi;
    }

    public boolean isEngelliMi() {
        return engelliMi;
    }

    public void setEngelliMi(boolean engelliMi) {
        this.engelliMi = engelliMi;
    }

    public void setKarsiTarafEngellediMi(boolean karsiTarafEngellediMi) {
        this.karsiTarafEngellediMi = karsiTarafEngellediMi;
    }

    public boolean isKarsiTarafEngellediMi() {
        return karsiTarafEngellediMi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kullanici kullanici = (Kullanici) o;
        return Objects.equals(KullaniciId, kullanici.KullaniciId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(KullaniciId);
    }

}
