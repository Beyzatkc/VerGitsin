package com.Beem.vergitsin.Mesaj;

public interface MesajSilmeGuncellemeGrup {
    void onSilmeislemi(Mesaj mesaj);
    void onSonMesajSilme(Mesaj oncekiMesaj);
    void onMesajGuncelleme(Mesaj mesaj);
}
