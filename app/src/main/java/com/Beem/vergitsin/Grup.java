package com.Beem.vergitsin;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Grup {
    private String grupId;
    private String grupAdi;
    private ArrayList<String> uyeler;
    private String olusturan;
    private Timestamp olusturmaTarihi;
    private String GrupFoto;
    private String GrupHakkinda;

    public Grup() {}

    public Grup(String grupId, String grupAdi, ArrayList<String> uyeler, String olusturan, Timestamp olusturmaTarihi) {
        this.grupId = grupId;
        this.grupAdi = grupAdi;
        this.uyeler = uyeler;
        this.olusturan = olusturan;
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public String getGrupId() {
        return grupId;
    }

    public void setGrupId(String grupId) {
        this.grupId = grupId;
    }

    public String getGrupAdi() {
        return grupAdi;
    }

    public void setGrupAdi(String grupAdi) {
        this.grupAdi = grupAdi;
    }

    public ArrayList<String> getUyeler() {
        return uyeler;
    }

    public void setUyeler(ArrayList<String> uyeler) {
        this.uyeler = uyeler;
    }

    public String getOlusturan() {
        return olusturan;
    }

    public void setOlusturan(String olusturan) {
        this.olusturan = olusturan;
    }

    public Timestamp getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public void setOlusturmaTarihi(Timestamp olusturmaTarihi) {
        this.olusturmaTarihi = olusturmaTarihi;
    }

    public void setGrupFoto(String grupFoto) {
        GrupFoto = grupFoto;
    }

    public String getGrupFoto() {
        return GrupFoto;
    }

    public String getGrupHakkinda() {
        return GrupHakkinda;
    }

    public void setGrupHakkinda(String grupHakkinda) {
        GrupHakkinda = grupHakkinda;
    }
}