package com.Beem.vergitsin.Kullanici;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesK{
    private static final String PREF_NAME = "GirisBilgisi";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "kullaniciAdi";
    private static final String KEY_ID="kullaniciId";

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

    public void cikisYap() {
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
