package com.Beem.vergitsin.Mesaj;

public interface MesajSilmeGuncellemeKisi {
    void onSilmeislemi(Mesaj mesaj);
    void onSonMesajSilme(Mesaj oncekiMesaj);
    void onMesajGuncelleme(Mesaj mesaj);
    void onSonMesajGuncelleme(Mesaj oncekiMesaj);
}
