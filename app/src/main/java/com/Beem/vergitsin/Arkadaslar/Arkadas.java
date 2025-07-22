package com.Beem.vergitsin.Arkadaslar;

import com.Beem.vergitsin.Kullanici.Kullanici;

import java.io.Serializable;

public class Arkadas extends Kullanici implements Serializable {

    public Arkadas(String kullaniciId, String kullaniciAdi, String profilFoto) {
        super(kullaniciId, kullaniciAdi, profilFoto);
    }
    public Arkadas(){
        super();
    }

    public Arkadas(Kullanici arkadas){
        super(arkadas.getKullaniciId(),arkadas.getKullaniciAdi(),arkadas.getEmail(),arkadas.getProfilFoto());
    }

}
