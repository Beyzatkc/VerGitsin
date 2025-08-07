package com.Beem.vergitsin.Mesaj;

public interface CevapGeldi {
    void onCevapGeldi(String cvpverenid,String mesajID,String cvpverenad,String icerik);
    void onEveteBasti(String cvpverenid,Mesaj mesaj);
}
