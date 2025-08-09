package com.Beem.vergitsin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.Manifest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.Beem.vergitsin.BorcAlinanlar.BorcAlinanlarFragment;
import com.Beem.vergitsin.BorcVerilenler.BorcVerilenlerFragment;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.KullaniciFragment;
import com.Beem.vergitsin.Kullanici.KullanicilarAdapter;
import com.Beem.vergitsin.Kullanici.SharedPreferencesK;
import com.Beem.vergitsin.Profil.ProfilFragment;
import com.Beem.vergitsin.Mesaj.MesajGrupFragment;
import com.Beem.vergitsin.Mesaj.MesajKisiFragment;
import com.Beem.vergitsin.Sohbet.SohbetFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static Kullanici kullanicistatic;
    private ArrayList<String>sozler;
    private TextView borcMizahIcerik;
    private LinearLayout arkadasEkleLayout;
    private LinearLayout grupolsuturlayout;
    private UyariMesaj uyariMesaj;
    private Button Borciste;
    private CardView profilAlani;
    private ImageView profilFoto;
    private ViewPager2 gecisliFragment;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private FragmentStateAdapter adapter;

    private ImageButton Sohbetler;
    private ConstraintLayout icerikLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profilAlani = findViewById(R.id.profilCardView);
        profilFoto = findViewById(R.id.profilFoto);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.konteynir);
                if (currentFragment instanceof SohbetFragment) {
                    getSupportFragmentManager().popBackStack();
                    icerikLayout.setVisibility(View.VISIBLE);
                } else {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        finish();
                    }
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        uyariMesaj=new UyariMesaj(this,false);
        SharedPreferencesK shared = new SharedPreferencesK(this);
        boolean fromFragment = getIntent().getBooleanExtra("fromFragment", false);
        if (shared.girisYapildiMi()||fromFragment) {
            String id=shared.getid();
            String email = shared.getEmail();
            String kAdi = shared.getKullaniciAdi();

            String ppFoto = shared.getProfilFoto();

            Kullanici kullanici = new Kullanici(id, kAdi, email);
            kullanici.setProfilFoto(ppFoto);
            MainActivity.kullanicistatic = kullanici;
            ProfilFotoYerlestir();
            cevrimici();
            BorcVerilenlerAlinanlarGecis();

            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(token -> {
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(MainActivity.kullanicistatic.getKullaniciId())
                                .update("fcmToken", token)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FCM", "Token başarıyla Firestore'a kaydedildi.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FCM", "Token kaydedilemedi", e);
                                });
                    });

        } else  {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.konteynir, new KullaniciFragment())
                    .commit();
        }
        borcMizahIcerik=findViewById(R.id.borcMizahIcerik);
        String cumle= RastgeleCumle();
        borcMizahIcerik.setText(cumle);
        arkadasEkleLayout=findViewById(R.id.arkadasEkleLayout);
        grupolsuturlayout=findViewById(R.id.grupolsuturlayout);
        Borciste=findViewById(R.id.Borciste);
        profilAlani.setOnClickListener(b->{ ProfilSayfasinaGec(); });
        Sohbetler=findViewById(R.id.Sohbetler);
        icerikLayout=findViewById(R.id.icerikLayout);
        ArkadasEkle();
        GrupOlustur();
        BorcIsteme();
        SohbetlereBasarsa();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bildirim izni verildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bildirim izni reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void cevrimici() {
        Map<String, Object> data = new HashMap<>();
        data.put("cevrimici", true);
        db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Çevrimiçi durumu güncellendi.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Hata: " + e.getMessage());
                });
    }
    @Override
    protected void onStop() {
        super.onStop();
        Map<String, Object> data = new HashMap<>();
        data.put("cevrimici", false);
        data.put("sonGorulme", FieldValue.serverTimestamp());
        db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Çevrimdışı durumu ve son görülme zamanı güncellendi.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Hata: " + e.getMessage());
                });
    }

    public String RastgeleCumle(){
        sozler = new ArrayList<>();
        sozler.add("Borç, dostluğu test etmenin en pahalı yoludur.");
        sozler.add("Para gider, dost kalır sanırsın. İkisi de gider.");
        sozler.add("Borç verdim, sessizlik kazandım.");
        sozler.add("En hızlı kaçan şey: borç alan arkadaş.");
        sozler.add("Borç dediğin şey dostlukta bir detaydır… Ama küçük detaylar bazen cüzdanı ağlatır.");
        sozler.add("Arkadaşınsa verir, dostunsa geri alırsın.");
        sozler.add("Borç veren unutmaz, alan hatırlamaz.");
        sozler.add("Para gelir geçer, dostluk kalıcıdır… ama yine de bir hatırlatma mesajı fena olmaz.");
        sozler.add("Arkadaşlık; çaya şeker, hayata neşe, borca da biraz sabır katmaktır.");
        sozler.add("Cüzdanımdan eksilen bazen para, ama dostluk hanemde hep artıdayız.");

        sozler.add("Borç verdim, kahkahası bende kaldı. Eh, takas sayılır! 😄💸");
        sozler.add("Dostum borç istedi, ben de verdim. Gönlüm zengin, cüzdanım biraz ekside. 🤗👜");
        sozler.add("Arkadaşlık; bazen çay ısmarlamak, bazen de 'şimdilik bende yok' demektir. ☕💬");
        sozler.add("Borçla gelen dostluk değil, kahveyle pekişen dostluk kalıcıdır. ☕👯");
        sozler.add("Para geri gelmese de, güzel bir muhabbetti be! 😅📉");
        sozler.add("Borç almak kolay, unutmak da kolay. Neyse ki dostluk zor unutuluyor. 😊🤝");
        sozler.add("Cüzdanımdan 50 TL eksildi ama kalbimdeki dostluk tam! 💖💸");
        sozler.add("Borç aldı, ödemedi ama gülüşü hâlâ aklımda… 😄📆");
        sozler.add("Arkadaş dediğin, borcunu geç ödeyebilir ama kahvesini asla unutmaz. ☕⌛");
        sozler.add("İyi arkadaş, borç alır. Çok iyi arkadaş, geri getirir. Efsane arkadaş, kahveyle gelir. 🧡☕💵");
        sozler.add("Dostluk bazen bir kahveye, bazen de küçük bir borca bakar. Önemli olan kahkaha kalıyor mu? 😄☕");
        sozler.add("Borç geldi geçti… Ama dostluk baki, şaka makinesi gibi hâlâ yanımda! 🎭😂");
        sozler.add("Arkadaşım ‘maaş yatınca veririm’ dedi… Şimdi her ay maaş günlerini kutluyorum. 🎉📅");
        sozler.add("Dostum benden borç aldı, sonra bana kahve ısmarladı. Kârdayım! ☕🟢");
        sozler.add("Dostluk; hem kredi hem kahkaha limitini aşabilmektir. 😄💳❤️");

        sozler.add("Borç isterken samimi, verirken cömert, geri alırken sabırlı olmak dostluğun sırrıdır. 🤗💸");
        sozler.add("Dostlukta borç, paylaşmanın başka bir şeklidir; hem kalpten hem cüzdandan. ❤️👜");
        sozler.add("Borç alırken ‘Teşekkür ederim’, geri öderken ‘Seni düşünüyorum’ demek yeter. 😊💬");
        sozler.add("Dostlar birbirine destek olur, bazen küçük borçlarla, bazen kahkahalarla. 🤝☕");
        sozler.add("Cüzdan hafifleyebilir ama dostluk asla! 💖💸");
        sozler.add("Borç vermek, dostluğa olan güvenin tatlı bir ifadesidir. 🌸🤗");
        sozler.add("Parayı vermek kolay, dostluğu yaşatmak zordur. 🌟💬");
        sozler.add("Bir kahve ısmarlamak bazen küçük bir borçtan daha değerlidir. ☕😊");
        sozler.add("Dostluk, borç ve kahkaha üçlüsüyle güçlenir. 😄🤝💸");
        sozler.add("Borç almak bir ihtiyaç, geri ödemek ise bir sevgi göstergesidir. ❤️💵");
        sozler.add("Dostun borcu, kalbin hatırladığı tatlı bir hikayedir. 📖💖");
        sozler.add("Borç, dostlukta paylaşılan küçük bir maceradır. 🚀🤗");
        sozler.add("Dostluk, borcun değil, beraber geçirilen güzel anların toplamıdır. 🌈😊");
        sozler.add("Birlikte güldüğümüz anlar, borçtan daha değerli. 😂❤️");
        sozler.add("Borç verirken gülümse, geri alınca da teşekkür et; dostluk böyle büyür. 🌟🤝");
        Random rnd = new Random();
        int sayi = rnd.nextInt(sozler.size());
        String cumle = sozler.get(sayi);
        return cumle;
    }

    private void ArkadasEkle(){
        arkadasEkleLayout.setOnClickListener(b->{
            KullanicilarDb();
        });
    }
    private void KullanicilarDb(){
        ArrayList<Kullanici> tum=new ArrayList<>();
        db.collection("users")
                .document(MainActivity.kullanicistatic.getKullaniciId())
                .get()
                .addOnSuccessListener(kendi->{
                    ArrayList<String>arkadaslar=(ArrayList<String>)kendi.get("arkadaslar");
                    if (arkadaslar == null) arkadaslar = new ArrayList<>();

                    ArrayList<String> finalArkadaslar = arkadaslar;
                    db.collection("users")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                tum.clear();
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    String id=doc.getId();
                                    if(!finalArkadaslar.contains(id)&& !id.equals(MainActivity.kullanicistatic.getKullaniciId())) {
                                        String email = doc.getString("email");
                                        String kAdi = doc.getString("kullaniciAdi");
                                        String photo = doc.getString("ProfilFoto");
                                        if (photo == null) {
                                            photo = "user";
                                        }
                                        Kullanici kullanici = new Kullanici(id, kAdi, email);
                                        kullanici.setProfilFoto(photo);
                                        tum.add(kullanici);
                                    }
                                }
                                Kullancilar(tum);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Kullanıcılar alınamadı!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
            Toast.makeText(this, "Arkadaşlar alınamadı!", Toast.LENGTH_SHORT).show();
        });
    }

    public void Kullancilar(ArrayList<Kullanici> kullanicilar){
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.kullanicilar_recycler);

        RecyclerView recycler = dialog.findViewById(R.id.recyclerViewKullanicilar);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        KullanicilarAdapter adapter = new KullanicilarAdapter(kullanicilar, this,  new KullanicilarAdapter.OnArkadasEkleListener() {
            @Override
            public void onArkadasEkleTiklandi(Kullanici kullanici) {
                //ArkadasEklemeDb(kullanici);
                ArkadasEkle.getEkle().ArkadasEklemeDb(kullanici);
            }

            @Override
            public void onArkadasCıkarTiklandi(Kullanici kullanici) {
                //ArkadasCikarmaDb(kullanici);
                ArkadasEkle.getEkle().ArkadasCikarmaDb(kullanici);
            }
        },getSupportFragmentManager(),()->{
            dialog.dismiss();
        });
        recycler.setAdapter(adapter);
        dialog.show();
    }

    /*    public void ArkadasEklemeDb(Kullanici kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayUnion(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Arkadaş eklendi!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ekleme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void ArkadasCikarmaDb(Kullanici kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayRemove(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Arkadaş çıkarıldı!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ekleme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    } */
    public void GrupOlustur(){
        grupolsuturlayout.setOnClickListener(b->{
            ArkadaslariGetir(true);
        });
    }
    public void ArkadaslariGetir(Boolean tetikle) {
        DocumentReference docRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        ArrayList<String>tumarklar=new ArrayList<>();
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ArrayList<String> arkadasListesi = (ArrayList<String>) documentSnapshot.get("arkadaslar");
                if (arkadasListesi == null) arkadasListesi = new ArrayList<>();
                    for (String arkadasId : arkadasListesi){
                       tumarklar.add(arkadasId);
                    }
                arkadasBilgilerineUlasma(tumarklar, arkadaslar -> {
                    if(tetikle) {
                        GrupOlusturGorunumu(arkadaslar);
                    }else{
                        ArkadasSecimiDialogu(arkadaslar);
                    }
                });
            } else {
                Log.d("Arkadas", "Kullanıcı bulunamadı");
            }
        }).addOnFailureListener(e -> {
            Log.e("Arkadas", "Veri alınamadı: " + e.getMessage());
        });
    }
    public void arkadasBilgilerineUlasma(ArrayList<String>arklar, ArkadasCallback callback){
        ArrayList<Kullanici>arkadaslar=new ArrayList<>();
        AtomicInteger sayac = new AtomicInteger(0);
        for (String arkadasId : arklar) {
            db.collection("users")
                    .document(arkadasId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String id=documentSnapshot.getId();
                            String kullaniciAdi = documentSnapshot.getString("kullaniciAdi");
                            String email = documentSnapshot.getString("email");
                            String photo = documentSnapshot.getString("ProfilFoto");
                            if (photo == null) {
                                photo = "user";
                            }
                            Kullanici kullanici=new Kullanici(id,kullaniciAdi,email);
                            kullanici.setProfilFoto(photo);
                            arkadaslar.add(kullanici);
                        }
                        if (sayac.incrementAndGet() == arklar.size()) {
                            callback.onArkadaslarHazir(arkadaslar);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ArkadasBilgi", "Veri alınamadı: " + e.getMessage());
                    });
        }

    }
    public void GrupOlusturGorunumu(ArrayList<Kullanici> kullanicilar) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.grup_recycler);

        RecyclerView recycler = dialog.findViewById(R.id.recyclerViewgrup);
        Button btnGrupOlustur = dialog.findViewById(R.id.btnGrupOlustur);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Kullanici> secilenler = new ArrayList<>();
        GrupSecilenAdapter grupadapter = new GrupSecilenAdapter(kullanicilar, secilenler, this);
        recycler.setAdapter(grupadapter);

        dialog.show();
        btnGrupOlustur.setOnClickListener(v -> {
            if (secilenler.isEmpty()) {
                Toast.makeText(this, "Lütfen en az 1 kişi seçin", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Grup İsmi");

                final EditText input = new EditText(this);
                input.setHint("Grubun adını girin");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Oluştur", (dialogInterface, which) -> {
                    uyariMesaj.YuklemeDurum("Grup oluşturuluyor...");
                    String grupIsmi = input.getText().toString().trim();
                    if (!grupIsmi.isEmpty()) {
                        GrupDb(grupIsmi,secilenler,uyariMesaj);
                        dialog.dismiss();
                    } else {
                       uyariMesaj.BasarisizDurum("Grup ismi boş olamaz",1000);
                    }
                });

                builder.setNegativeButton("İptal", (dialogInterface, which) -> dialogInterface.cancel());

                builder.show();

            }
        });
    }
    public void GrupDb(String grupAdi ,ArrayList<Kullanici> secilenler,UyariMesaj mesaj){
        ArrayList<String>uyeIDleri=new ArrayList<>();
        for(Kullanici uye:secilenler){
            uyeIDleri.add(uye.getKullaniciId());
        }
        uyeIDleri.add(MainActivity.kullanicistatic.getKullaniciId());
        Map<String, Object> grupVerisi = new HashMap<>();
        grupVerisi.put("grupAdi", grupAdi);
        grupVerisi.put("uyeler", uyeIDleri);
        grupVerisi.put("olusturan", MainActivity.kullanicistatic.getKullaniciId());
        grupVerisi.put("olusturmaTarihi", FieldValue.serverTimestamp());

        db.collection("gruplar")
                .add(grupVerisi)
                .addOnSuccessListener(documentReference  -> {
                    String grupId = documentReference.getId();
                    sohbetOlusturDbGrup(grupId,grupAdi,System.currentTimeMillis(), null,String.format("%s sohbet oluşturdu",MainActivity.kullanicistatic.getKullaniciAdi()),sohbetId -> {
                        icerikLayout.setVisibility(View.GONE);
                        Fragment sohbetFragment = new SohbetFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.konteynir, sohbetFragment)
                                .addToBackStack(null)
                                .commit();
                    });
                   mesaj.BasariliDurum( "Grup oluşturuldu!",1000);
                })
                .addOnFailureListener(e -> {
                    mesaj.BasarisizDurum( "Grup oluşturulumadı!",1000);
                });
    }
    public void BorcIsteme(){
        Borciste.setOnClickListener(B->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.borc_secimi, null);

            Button btnArkadas = view.findViewById(R.id.btnArkadas);
            Button btnGrup = view.findViewById(R.id.btnGrup);

            AlertDialog dialog = builder.setView(view).create();
            btnArkadas.setOnClickListener(v -> {
                dialog.dismiss();
                ArkadaslariGetir(false);
            });
            btnGrup.setOnClickListener(v -> {
                dialog.dismiss();
                GrupDbCek();
            });
            dialog.show();
        });
    }
    public void ArkadasSecimiDialogu(ArrayList<Kullanici>arkadasListesi){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.borc_istearksec);

        RecyclerView recycler = dialog.findViewById(R.id.recyclerArkadas);
        Button btnDevamEt = dialog.findViewById(R.id.btnDevamEt);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        ArkadasAdapter adapter = new ArkadasAdapter(arkadasListesi,this);
        recycler.setAdapter(adapter);

        btnDevamEt.setOnClickListener(v -> {
            Kullanici secilen = adapter.getSecilenKullanici();
            if (secilen == null) {
                Toast.makeText(this, "Lütfen bir arkadaş seçin", Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                Dialog detayDialog = new Dialog(this);
                detayDialog.setContentView(R.layout.borc_detay);

                EditText edtMiktar = detayDialog.findViewById(R.id.edtMiktar);
                EditText edtAciklama = detayDialog.findViewById(R.id.edtAciklama);
                EditText edtTarih = detayDialog.findViewById(R.id.edtTarih);
                EditText edtiban=detayDialog.findViewById(R.id.ibanEditt);
                Button btnGonder = detayDialog.findViewById(R.id.btnGonder);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();

                edtTarih.setInputType(InputType.TYPE_NULL);
                edtTarih.setFocusable(false);

                edtTarih.setOnClickListener(b-> {
                    int yil = calendar.get(Calendar.YEAR);
                    int ay = calendar.get(Calendar.MONTH);
                    int gun = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            this,
                            (view, year, month, dayOfMonth) -> {
                                String secilenTarih = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                        dayOfMonth, month + 1, year);

                                try {
                                    Date girilenTarih = sdf.parse(secilenTarih);
                                    Date bugun = sdf.parse(sdf.format(new Date()));

                                    if (girilenTarih.before(bugun)) {
                                        Toast.makeText(this, "Geçmiş bir tarih seçemezsiniz!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        edtTarih.setText(secilenTarih);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            },
                            yil, ay, gun
                    );

                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                });

                btnGonder.setOnClickListener(v2 -> {
                    String miktar = edtMiktar.getText().toString().trim();
                    String aciklama = edtAciklama.getText().toString().trim();
                    String tarih = edtTarih.getText().toString().trim();
                    String iban=edtiban.getText().toString().trim();
                    if (miktar.isEmpty()) {
                        Toast.makeText(this, "Miktar boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(tarih.isEmpty()){
                        Toast.makeText(this, "Tarih boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        Date girilenTarih = sdf.parse(tarih);
                        Timestamp timestamp = new Timestamp(girilenTarih);
                            SohbetOlusturDbArkadas(MainActivity.kullanicistatic.getKullaniciId(),secilen.getKullaniciId(),secilen.getKullaniciAdi(), System.currentTimeMillis(), secilen.getProfilFoto(), String.format("%s TL borç isteği",miktar), sohbetId -> {
                                        BorcIstekleriDb(
                                                MainActivity.kullanicistatic.getKullaniciId(),
                                                secilen.getKullaniciId(),
                                                miktar,
                                                aciklama,
                                                timestamp,
                                                MainActivity.kullanicistatic.getKullaniciAdi(),
                                                sohbetId,
                                                System.currentTimeMillis(),
                                                iban,
                                                () -> {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("kaynak", "mainactivity");
                                                    bundle.putString("pp",secilen.getProfilFoto());
                                                    bundle.putString("istekatilanAdi",secilen.getKullaniciAdi());
                                                    bundle.putString("miktar", miktar);
                                                    bundle.putString("aciklama", aciklama);
                                                    bundle.putString("odemeTarihi", tarih);
                                                    bundle.putString("sohbetId", sohbetId);
                                                    bundle.putString("iban",iban);

                                                    MesajKisiFragment fragment = new MesajKisiFragment();
                                                    fragment.setArguments(bundle);
                                                    getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.konteynir, fragment)
                                                            .addToBackStack(null)
                                                            .commit();

                                                    detayDialog.dismiss();
                                                }
                                        );
                                    }
                            );
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                });
                detayDialog.show();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void SohbetOlusturDbArkadas(String gonderenId, String aliciId,String kullaniciAdi, Long sonMsjSaati, String ppfoto, String sonMesaj, Consumer<String> onSohbetOlusturuldu) {
        String sohbetId = gonderenId.compareTo(aliciId) < 0 ?
                gonderenId + "_" + aliciId :
                aliciId + "_" + gonderenId;
        db.collection("sohbetler")
                .document(sohbetId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        onSohbetOlusturuldu.accept(sohbetId);
                    } else {
                        Map<String, Object> sohbetData = new HashMap<>();
                        sohbetData.put("tur", "kisi");
                        sohbetData.put("sohbetId",sohbetId);
                        sohbetData.put("kullaniciAdi", kullaniciAdi);
                        sohbetData.put("sonMsjSaati", sonMsjSaati);
                        sohbetData.put("ppfoto", ppfoto);
                        sohbetData.put("sonMesaj", sonMesaj);
                        sohbetData.put("katilimcilar", Arrays.asList(gonderenId, aliciId));

                        db.collection("sohbetler").document(sohbetId)
                                .set(sohbetData)
                                .addOnSuccessListener(aVoid -> onSohbetOlusturuldu.accept(sohbetId))
                                .addOnFailureListener(e -> Log.e("Firestore", "Sohbet kaydedilirken hata oluştu", e));
                    }
                }).addOnFailureListener(e -> Log.e("Firestore", "Sohbet kontrolünde hata oluştu", e));
    }
    public void sohbetOlusturDbGrup(String grupId, String kullaniciAdi, Long sonMsjSaati, String ppfoto, String sonMesaj, Consumer<String> onSohbetOlusturuldu) {
        db.collection("gruplar")
                .document(grupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> uyeler = (ArrayList<String>) documentSnapshot.get("uyeler");
                        if (uyeler == null) {
                            uyeler = new ArrayList<>();
                        }
                        String sohbetId = grupId;
                        ArrayList<String> finalUyeler = uyeler;
                        db.collection("sohbetler")
                                .document(sohbetId)
                                .get()
                                .addOnSuccessListener(sohbetSnapshot -> {
                                    if (sohbetSnapshot.exists()) {
                                        onSohbetOlusturuldu.accept(sohbetId);
                                    } else {
                                        Map<String, Object> sohbetData = new HashMap<>();
                                        sohbetData.put("tur", "grup");
                                        sohbetData.put("sohbetId", sohbetId);
                                        sohbetData.put("kullaniciAdi", kullaniciAdi);
                                        sohbetData.put("sonMsjSaati", sonMsjSaati);
                                        sohbetData.put("ppfoto", ppfoto);
                                        sohbetData.put("sonMesaj", sonMesaj);
                                        sohbetData.put("katilimcilar", finalUyeler); // Grup üyeleri

                                        db.collection("sohbetler")
                                                .document(sohbetId)
                                                .set(sohbetData)
                                                .addOnSuccessListener(aVoid -> onSohbetOlusturuldu.accept(sohbetId))
                                                .addOnFailureListener(e -> Log.e("Firestore", "Sohbet kaydedilirken hata oluştu", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Sohbet kontrolünde hata oluştu", e));
                    } else {
                        Log.e("Firestore", "Grup bulunamadı: " + grupId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Grup çekilirken hata oluştu", e));
    }

    public void BorcIstekleriDb(String istekatan,String istekatilan,String miktar,String aciklama,Timestamp tarih,String ad,String sohbetId,Long zaman,String iban,Runnable onSuccessCallback){
        uyariMesaj.YuklemeDurum("");
        Map<String, Object> borcData = new HashMap<>();
        borcData.put("istekatanAdi",ad);
        borcData.put("istekatanID", istekatan);
        borcData.put("istekatılanID",istekatilan);
        borcData.put("aciklama", aciklama);
        borcData.put("miktar", miktar);
        borcData.put("odenecektarih", tarih);
        borcData.put("isteginAtildigiZaman",zaman);
        borcData.put("GorulduMu",false);
        borcData.put("iban",iban);

        db.collection("sohbetler")
                .document(sohbetId)
                .collection("borc_istekleri")
                .add(borcData)
                .addOnSuccessListener(aVoid -> {
                    onSuccessCallback.run();
                    uyariMesaj.BasariliDurum("",1000);
                    Log.d("Firestore", "Borç isteği başarıyla kaydedildi: " );
                })
                .addOnFailureListener(e -> {
                    uyariMesaj.BasarisizDurum("",1000);
                    Log.e("Firestore", "Borç isteği kaydedilirken hata: ", e);
                });
    }
    public void SohbetlereBasarsa(){
        Sohbetler.setOnClickListener(b->{
            icerikLayout.setVisibility(View.GONE);
            SohbetFragment fragment = new SohbetFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.konteynir, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
    public void GrupDbCek() {
        ArrayList<Grup>grupListesi=new ArrayList<Grup>();
        db.collection("gruplar")
                .whereArrayContains("uyeler", MainActivity.kullanicistatic.getKullaniciId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    grupListesi.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String grupId = doc.getId();
                        String grupAdi = doc.getString("grupAdi");
                        ArrayList<String> uyeler = (ArrayList<String>) doc.get("uyeler");
                        String olusturan = doc.getString("olusturan");
                        Timestamp tarih = doc.getTimestamp("olusturmaTarihi");

                        Grup grup = new Grup(grupId, grupAdi, uyeler, olusturan, tarih);
                        grupListesi.add(grup);
                    }
                    GrupSecimiDialogu(grupListesi);

                })
                .addOnFailureListener(e -> {
                    Log.e("GrupCek", "Gruplar alınamadı: " + e.getMessage());
                });
    }
    public void GrupSecimiDialogu(ArrayList<Grup>gruplar){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.borc_istegrupsec);

        RecyclerView recycler = dialog.findViewById(R.id.recyclerGrup);
        Button btnDevamEt = dialog.findViewById(R.id.btnDevamEtgrup);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        GrupAdapter adapter = new GrupAdapter(gruplar,this);
        recycler.setAdapter(adapter);

        btnDevamEt.setOnClickListener(v -> {
            Grup secilen = adapter.getSecilenGrup();
            if (secilen == null) {
                Toast.makeText(this, "Lütfen bir grup seçin", Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss(); // eski dialogu kapat
                Dialog detayDialog = new Dialog(this);
                detayDialog.setContentView(R.layout.borc_detay);

                EditText edtMiktar = detayDialog.findViewById(R.id.edtMiktar);
                EditText edtAciklama = detayDialog.findViewById(R.id.edtAciklama);
                EditText edtTarih = detayDialog.findViewById(R.id.edtTarih);
                EditText edtiban=detayDialog.findViewById(R.id.ibanEditt);
                Button btnGonder = detayDialog.findViewById(R.id.btnGonder);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                edtTarih.setInputType(InputType.TYPE_NULL);
                edtTarih.setFocusable(false);

                edtTarih.setOnClickListener(b -> {
                    int yil = calendar.get(Calendar.YEAR);
                    int ay = calendar.get(Calendar.MONTH);
                    int gun = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            this,
                            (view, year, month, dayOfMonth) -> {
                                String secilenTarih = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                        dayOfMonth, month + 1, year);

                                try {
                                    Date girilenTarih = sdf.parse(secilenTarih);
                                    Date bugun = sdf.parse(sdf.format(new Date()));

                                    if (girilenTarih.before(bugun)) {
                                        Toast.makeText(this, "Geçmiş bir tarih seçemezsiniz!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        edtTarih.setText(secilenTarih);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            },
                            yil, ay, gun
                    );

                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                });

                btnGonder.setOnClickListener(v2 -> {
                    String miktar = edtMiktar.getText().toString().trim();
                    String aciklama = edtAciklama.getText().toString().trim();
                    String tarih = edtTarih.getText().toString().trim();
                    String iban=edtiban.getText().toString().trim();

                    if (miktar.isEmpty()) {
                        Toast.makeText(this, "Miktar boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(tarih.isEmpty()){
                        Toast.makeText(this, "Tarih boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        Date girilenTarih = sdf.parse(tarih);
                        Timestamp timestamp = new Timestamp(girilenTarih);
                        sohbetOlusturDbGrup(secilen.getGrupId(),secilen.getGrupAdi(), System.currentTimeMillis(), null,String.format("%s TL borç isteği",miktar), sohbetId -> {
                                        BorcIstekleriDb(
                                                MainActivity.kullanicistatic.getKullaniciId(),
                                                secilen.getGrupId(),
                                                miktar,
                                                aciklama,
                                                timestamp,
                                                MainActivity.kullanicistatic.getKullaniciAdi(),
                                                sohbetId,
                                                System.currentTimeMillis(),
                                                iban,
                                                () -> {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("kaynak", "mainactivity");
                                                    bundle.putString("istekatilanAdi", secilen.getGrupAdi());
                                                    bundle.putString("pp",null);
                                                    bundle.putString("miktar", miktar);
                                                    bundle.putString("aciklama", aciklama);
                                                    bundle.putString("odemeTarihi", tarih);
                                                    bundle.putString("sohbetId", sohbetId);
                                                    bundle.putString("iban",iban);

                                                    MesajGrupFragment fragment = new MesajGrupFragment();
                                                    fragment.setArguments(bundle);
                                                    getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.konteynir, fragment)
                                                            .addToBackStack(null)
                                                            .commit();
                                                    detayDialog.dismiss();
                                                }
                                        );
                                    }
                            );
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                });
                detayDialog.show();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
    private void ProfilSayfasinaGec(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.konteynir, new ProfilFragment())
                .addToBackStack(null)
                .commit();
    }
    private void ProfilFotoYerlestir(){
        int resId = getResources().getIdentifier(kullanicistatic.getProfilFoto(), "drawable", this.getPackageName());
        profilFoto.setImageResource(resId);
    }

    private void BorcVerilenlerAlinanlarGecis(){
        gecisliFragment = findViewById(R.id.viewPager);
        fragmentList.add(new BorcAlinanlarFragment());
        fragmentList.add(new AnaSayfaFragment());
        fragmentList.add(new BorcVerilenlerFragment());

        adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return fragmentList.size();
            }
        };
        gecisliFragment.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != 1) {
                    findViewById(R.id.icerikLayout).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.icerikLayout).setVisibility(View.VISIBLE);
                }
            }
        });
        gecisliFragment.setAdapter(adapter);
        gecisliFragment.setCurrentItem(1);
    }

}
