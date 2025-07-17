package com.Beem.vergitsin.Kullanici;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.Map;


public class KullaniciViewModel extends ViewModel {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean>_KAdi=new MutableLiveData<>();
    public LiveData<Boolean>kAdi(){return _KAdi;}

    private MutableLiveData<String>_email=new MutableLiveData<>();
    public LiveData<String>email(){return _email;}

    private MutableLiveData<Boolean>_girisbasarili=new MutableLiveData<>();
    public LiveData<Boolean>girisbasarili(){return _girisbasarili;}

    private MutableLiveData<String>_id=new MutableLiveData<>();
    public LiveData<String>id(){return _id;}

    private final MutableLiveData<Boolean> sifreSifirlandi = new MutableLiveData<>();
    public LiveData<Boolean> getSifreSifirlandi() {
        return sifreSifirlandi;
    }



    public void Kadi_BenzersizMi(String kullaniciAdi) {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean bulundu = false;

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String kAdi = documentSnapshot.getString("kullaniciAdi");
                            if (kullaniciAdi.equals(kAdi)) {
                                bulundu = true;
                                break;
                            }
                        }
                        _KAdi.setValue(!bulundu); // true = benzersiz
                    } else {
                        Log.e("FIREBASE", "Veri alınamadı: " + task.getException().getMessage());
                    }
                });
    }
    public void KayitOl(Kullanici kullanici) {
        mAuth.createUserWithEmailAndPassword(kullanici.getEmail(), kullanici.getSifre())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {

                            Map<String, Object> kullaniciVerisi = new HashMap<>();
                            kullaniciVerisi.put("email", kullanici.getEmail());
                            kullaniciVerisi.put("kullaniciAdi", kullanici.getKullaniciAdi());
                            kullaniciVerisi.put("sifre",kullanici.getSifre());
                            db.collection("users")
                                    .add(kullaniciVerisi)
                                    .addOnSuccessListener(documentReference -> {
                                        String olusanId = documentReference.getId();
                                        kullanici.setKullaniciId(olusanId);
                                        Log.d("FIRESTORE", "Kullanıcı eklendi. ID: " + olusanId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FIRESTORE", "Kullanıcı ekleme hatası: " + e.getMessage());
                                    });
                        }
                    } else {
                        Log.e("FIREBASE", "Kayıt hatası: " + task.getException().getMessage());
                    }
                });
    }
    public void GirisYap(String email,String sifre) {
        mAuth.signInWithEmailAndPassword(email, sifre)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _girisbasarili.setValue(true);
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            db.collection("users")
                                    .whereEqualTo("email",email)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                                            String id = document.getId();
                                            _id.setValue(id);
                                        } else {
                                            Log.d("FIREBASE", "Kullanıcı bulunamadı.");
                                        }
                                    });
                        }
                    } else {
                        _girisbasarili.setValue(false);
                        Log.e("AUTH", "Giriş hatası: " + task.getException().getMessage());
                    }
                });
    }
    public void EmaileUlasma(String kullaniciAdi) {
        db.collection("users")
                .whereEqualTo("kullaniciAdi", kullaniciAdi)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // ✅ ilk belgeyi al
                        String email = document.getString("email");
                        _email.setValue(email);
                    } else {
                        Log.d("FIREBASE", "Kullanıcı bulunamadı.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Hata: " + e.getMessage());
                });
    }
    public void SifreSifirla(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sifreSifirlandi.setValue(true);
                    } else {
                        sifreSifirlandi.setValue(false);
                    }
                });
    }





}