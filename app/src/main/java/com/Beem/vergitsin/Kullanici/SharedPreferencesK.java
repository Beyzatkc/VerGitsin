package com.Beem.vergitsin.Kullanici;
import android.content.Context;
import android.content.SharedPreferences;

import com.Beem.vergitsin.MainActivity;

public class SharedPreferencesK{
    private static final String PREF_NAME = "GirisBilgisi";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "kullaniciAdi";
    private static final String KEY_ID="kullaniciId";

    private static final String KEY_PROFILE_PHOTO = "profilFoto";
    private static final String KEY_BIO = "bio";
    private static final String KEY_ARKADAS_SAYISI = "arkadasSayisi";
    private static final String KEY_GRUP_SAYISI = "grupSayisi";
    private static final String KEY_VERDIGI_BORC = "verdigiBorc";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesK(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void girisYap(String email, String kullaniciAdi,String id) {
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, kullaniciAdi);
        editor.putString(KEY_ID,id);

        editor.apply();
    }

    public void ProfilGuncelle(String profilFoto, String bio,String kullaniciAdi) {
        editor.putString(KEY_PROFILE_PHOTO, profilFoto);
        editor.putString(KEY_BIO, bio);
        editor.putString(KEY_USERNAME, kullaniciAdi);
        editor.apply();
    }

    public void setArkadasSayisi(int arkadasSayisi) {
        editor.putInt(KEY_ARKADAS_SAYISI, arkadasSayisi);
    }
    public void setGrupSayisi(int grupSayisi) {
        editor.putInt(KEY_GRUP_SAYISI, grupSayisi);
    }
    public void setVerdigiBorc(int verdigiBorc) {
        editor.putInt(KEY_VERDIGI_BORC, verdigiBorc);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getKullaniciAdi() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getid() {
        return sharedPreferences.getString(KEY_ID, null);
    }
    public boolean girisYapildiMi() {
        return getEmail() != null;
    }
    public String getProfilFoto() {
        return sharedPreferences.getString(KEY_PROFILE_PHOTO, "user");
    }
    public String getBio() {
        return sharedPreferences.getString(KEY_BIO, "");
    }
    public int getArkadasSayisi() {
        return sharedPreferences.getInt(KEY_ARKADAS_SAYISI, 0);
    }
    public int getGrupSayisi() {
        return sharedPreferences.getInt(KEY_GRUP_SAYISI, 0);
    }
    public int getVerdigiBorc() {
        return sharedPreferences.getInt(KEY_VERDIGI_BORC, 0);
    }

    public void EditorAply(){
        editor.apply();
    }

    public void cikisYap() {
        MainActivity.kullanicistatic = new Kullanici();
        editor.clear();
        editor.apply();
    }
    /*
    SessionManager sessionManager = new SessionManager(requireContext());
sessionManager.cikisYap();

// Ana ekrana ya da giriş fragmentine dön
requireActivity().getSupportFragmentManager()
    .beginTransaction()
    .replace(R.id.konteynir, new KullaniciFragment())
    .commit();

     */
}
