package com.Beem.vergitsin;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.Manifest;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import com.Beem.vergitsin.databinding.BorcArkKombineBinding;
import com.Beem.vergitsin.databinding.BorcIstegrupkombineBinding;
import com.Beem.vergitsin.databinding.GrupRecyclerBinding;
import com.Beem.vergitsin.databinding.KullanicilarRecyclerBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
    private ArkadasAdapter adapterark;
    private GrupAdapter adaptergrup;
    private KullanicilarAdapter adapterkullanici;
    private GrupSecilenAdapter grupadapter;
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
            System.out.println("main girildi");
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

            System.out.println("mainin ustu");
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(token -> {
                        System.out.println("token"+token);
                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(MainActivity.kullanicistatic.getKullaniciId())
                                .update("fcmToken", token)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("mainin ici");
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
                KullanicilarBottom(new ArrayList<>());
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
                                if(adapterkullanici!=null){
                                    adapterkullanici.guncelleListe(tum);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Kullanıcılar alınamadı!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
            Toast.makeText(this, "Arkadaşlar alınamadı!", Toast.LENGTH_SHORT).show();
        });
    }

    public void KullanicilarBottom(ArrayList<Kullanici> kullanicilar) {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        KullanicilarRecyclerBinding binding = KullanicilarRecyclerBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        RecyclerView recycler = binding.recyclerViewKullanicilar;
        EditText aramaEditText =binding.aramaEditText;

        recycler.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Kullanici> gosterilecekKullanicilar = new ArrayList<>(kullanicilar);

        adapterkullanici = new KullanicilarAdapter(kullanicilar,gosterilecekKullanicilar, this,  new KullanicilarAdapter.OnArkadasEkleListener() {
            @Override
            public void onArkadasEkleTiklandi(Kullanici kullanici) {
                ArkadasEkle.getEkle().ArkadasEklemeDb(kullanici);
            }

            @Override
            public void onArkadasCıkarTiklandi(Kullanici kullanici) {
                ArkadasEkle.getEkle().ArkadasCikarmaDb(kullanici);
            }
        },getSupportFragmentManager(),()->{
            dialog.dismiss();
        });
        recycler.setAdapter(adapterkullanici);
        aramaEditText.setText("");
        adapterkullanici.filtrele("");
        aramaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterkullanici.filtrele(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.show();
    }


    public void GrupOlustur(){
        grupolsuturlayout.setOnClickListener(b->{
            GrupOlusturGorunumu(new ArrayList<>());
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
                        if(grupadapter!=null){
                            grupadapter.guncelleListe(arkadaslar);
                        }
                    }else{
                        if(adapterark != null){
                            adapterark.guncelleListe(arkadaslar);
                        }
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
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        GrupRecyclerBinding binding = GrupRecyclerBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        RecyclerView recycler = binding.recyclerViewgrup;
        AppCompatButton btnGrupsec = binding.btnGrupsec;
        LinearLayout grupsec = binding.grupsec;
        LinearLayout grupadi = binding.grupadi;
        TextView tvAdim1 = binding.tvAdim1;
        TextView tvAdim2 = binding.tvAdim2;
        ImageView geributon = binding.btnGeri;


        EditText etGrupAdi = binding.etGrupAdi;
        AppCompatButton btnGrupOlustur = binding.btnGrupOlustur;


        recycler.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Kullanici> secilenler = new ArrayList<>();
        grupadapter = new GrupSecilenAdapter(kullanicilar, secilenler, this);
        recycler.setAdapter(grupadapter);

        btnGrupsec.setOnClickListener(v -> {
            if (secilenler.isEmpty()) {
                Toast.makeText(this, "Lütfen en az 1 kişi seçin", Toast.LENGTH_SHORT).show();
            } else {
                animasyonileri(grupsec,grupadi);
                geributon.setVisibility(View.VISIBLE);
                tvAdim1.setBackgroundResource(R.drawable.circle_inactive);
                tvAdim2.setBackgroundResource(R.drawable.circle_active);
                tvAdim1.setTextColor(Color.BLACK);
                tvAdim2.setTextColor(Color.WHITE);

                btnGrupOlustur.setOnClickListener(v2 -> {
                    String baslik = etGrupAdi.getText().toString().trim();
                    if (baslik.isEmpty()) {
                        Toast.makeText(this, "Grup adı boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        uyariMesaj.YuklemeDurum("Grup oluşturuluyor...");
                        GrupDb(baslik,secilenler,uyariMesaj);
                        dialog.dismiss();
                    }
                });
            }
        });
        geributon.setOnClickListener(v -> {
            animasyonGeri(grupsec,grupadi);
            geributon.setVisibility(View.GONE);
            tvAdim1.setTextColor(Color.WHITE);
            tvAdim2.setTextColor(Color.BLACK);
            tvAdim1.setBackgroundResource(R.drawable.circle_active);
            tvAdim2.setBackgroundResource(R.drawable.circle_inactive);
        });
        dialog.show();
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
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.RoundedDialogTheme);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.borc_secimi, null);

            AppCompatButton btnArkadas = view.findViewById(R.id.btnArkadas);
            AppCompatButton btnGrup = view.findViewById(R.id.btnGrup);

            builder.setView(view);
            androidx.appcompat.app.AlertDialog dialog = builder.create();

            dialog.setOnShowListener(d -> {
                Window window = dialog.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            });

            btnArkadas.setOnClickListener(v -> {
                dialog.dismiss();
                BorcIslemiArkBottomSheet(new ArrayList<>());
                ArkadaslariGetir(false);

            });
            btnGrup.setOnClickListener(v -> {
                dialog.dismiss();
                GrupSecimiDialogu(new ArrayList<>());
                GrupDbCek();
            });
            dialog.show();
        });
    }

    public void animasyonileri(LinearLayout layoutArkadas,LinearLayout layoutDetay){
        layoutArkadas.animate()
                .translationX(-layoutArkadas.getWidth())
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    layoutArkadas.setVisibility(View.GONE);
                    layoutDetay.setVisibility(View.VISIBLE);
                    layoutArkadas.setTranslationX(0);
                    layoutArkadas.setAlpha(1f);
                })
                .start();

        layoutDetay.animate()
                .translationX(0)
                .alpha(1f)
                .setDuration(300)
                .start();
    }
    public void animasyonGeri(LinearLayout layoutArkadas, LinearLayout layoutDetay) {
        layoutDetay.animate()
                .translationX(layoutDetay.getWidth())
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    layoutDetay.setVisibility(View.GONE);
                    layoutArkadas.setVisibility(View.VISIBLE);
                    layoutDetay.setTranslationX(0);
                    layoutDetay.setAlpha(1f);
                })
                .start();

        layoutArkadas.animate()
                .translationX(0)
                .alpha(1f)
                .setDuration(300)
                .start();
    }
    public void BorcIslemiArkBottomSheet(ArrayList<Kullanici> arkadasListesi) {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        BorcArkKombineBinding binding = BorcArkKombineBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());


        LinearLayout layoutArkadas = binding.layoutArkadas;
        LinearLayout layoutDetay = binding.layoutDetay;
        TextView tvAdim1 = binding.tvAdim1;
        TextView tvAdim2 = binding.tvAdim2;
        ImageView geributon = binding.btnGeri;

        RecyclerView recycler = binding.recyclerArkadas;
        AppCompatButton btnDevamEt = binding.btnDevamEt;
        EditText aramaEditText = binding.aramaEditText;


        EditText edtMiktar = binding.edtMiktar;
        EditText edtAciklama = binding.edtAciklama;
        EditText edtTarih = binding.edtTarih;
        EditText edtiban=binding.ibanEditt;
        AppCompatButton btnGonder= binding.btnGonder;

            recycler.setLayoutManager(new LinearLayoutManager(this));
            adapterark= new ArkadasAdapter(arkadasListesi, new ArrayList<>(arkadasListesi), this);
            recycler.setAdapter(adapterark);
            adapterark.filtrele("");

            btnDevamEt.setOnClickListener(v -> {
                Kullanici secilen = adapterark.getSecilenKullanici();
                if (secilen == null) {
                    Toast.makeText(this, "Lütfen bir arkadaş seçin", Toast.LENGTH_SHORT).show();
                } else {
                    animasyonileri(layoutArkadas,layoutDetay);
                    geributon.setVisibility(View.VISIBLE);
                    tvAdim1.setBackgroundResource(R.drawable.circle_inactive);
                    tvAdim2.setBackgroundResource(R.drawable.circle_active);
                    tvAdim1.setTextColor(Color.BLACK);
                    tvAdim2.setTextColor(Color.WHITE);

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
                                                    bundle.putString("istekatilanid",secilen.getKullaniciId());
                                                    bundle.putString("istekatanad",MainActivity.kullanicistatic.getKullaniciAdi());
                                                    bundle.putString("istekatanpp",MainActivity.kullanicistatic.getProfilFoto());
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

                                                    dialog.dismiss();
                                                }
                                        );
                                    }
                            );
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
            aramaEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapterark.filtrele(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });


        geributon.setOnClickListener(v -> {
            animasyonGeri(layoutArkadas,layoutDetay);
            geributon.setVisibility(View.GONE);
            tvAdim1.setTextColor(Color.WHITE);
            tvAdim2.setTextColor(Color.BLACK);
            tvAdim1.setBackgroundResource(R.drawable.circle_active);
            tvAdim2.setBackgroundResource(R.drawable.circle_inactive);
        });

        dialog.show();
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
                    if(adaptergrup != null){
                        adaptergrup.guncelleListe(grupListesi);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GrupCek", "Gruplar alınamadı: " + e.getMessage());
                });
    }
    public void GrupSecimiDialogu(ArrayList<Grup>gruplar){
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        BorcIstegrupkombineBinding binding = BorcIstegrupkombineBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        LinearLayout layoutGrup = binding.layoutGrup;
        LinearLayout layoutDetay = dialog.findViewById(R.id.layoutgrupDetay);
        TextView tvAdim1 = binding.tvAdim1;
        TextView tvAdim2 = binding.tvAdim2;
        ImageView geributon = binding.btnGeri;

        RecyclerView recycler = binding.recyclerGrup;
        AppCompatButton btnDevamEt = binding.btnDevamEtgrup;
        EditText edtMiktar = binding.edtMiktar;
        EditText edtAciklama = binding.edtAciklama;
        EditText edtTarih = binding.edtTarih;
        EditText edtiban=binding.ibanEditt;
        AppCompatButton btnGonder= binding.btnGonder;

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adaptergrup = new GrupAdapter(gruplar,this);
        recycler.setAdapter(adaptergrup);

        btnDevamEt.setOnClickListener(v -> {
            Grup secilen = adaptergrup.getSecilenGrup();
            if (secilen == null) {
                Toast.makeText(this, "Lütfen bir grup seçin", Toast.LENGTH_SHORT).show();
            } else {
                animasyonileri(layoutGrup,layoutDetay);
                geributon.setVisibility(View.VISIBLE);
                tvAdim1.setBackgroundResource(R.drawable.circle_inactive);
                tvAdim2.setBackgroundResource(R.drawable.circle_active);
                tvAdim1.setTextColor(Color.BLACK);
                tvAdim2.setTextColor(Color.WHITE);

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
                                                dialog.dismiss();
                                            }
                                    );
                                }
                        );
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                });
            }
          });
        geributon.setOnClickListener(v -> {
            animasyonGeri(layoutGrup,layoutDetay);
            geributon.setVisibility(View.GONE);
            tvAdim1.setTextColor(Color.WHITE);
            tvAdim2.setTextColor(Color.BLACK);
            tvAdim1.setBackgroundResource(R.drawable.circle_active);
            tvAdim2.setBackgroundResource(R.drawable.circle_inactive);
        });
        dialog.show();
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
            SohbetFragment fragment = new SohbetFragment();
            FragmentYonlendirici.Yonlendir(getSupportFragmentManager(),fragment,"sohbetler");
            /*getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.konteynir, fragment)
                    .addToBackStack(null)
                    .commit();*/
        });
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
