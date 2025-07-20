package com.Beem.vergitsin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.KullaniciFragment;
import com.Beem.vergitsin.Kullanici.SharedPreferencesK;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static Kullanici kullanicistatic;
    private ArrayList<String>sozler;
    private TextView borcMizahIcerik;
    private LinearLayout arkadasEkleLayout;
    private LinearLayout grupolsuturlayout;
    private UyariMesaj uyariMesaj;
    private Button Borciste;
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
        uyariMesaj=new UyariMesaj(this,false);
        SharedPreferencesK shared = new SharedPreferencesK(this);
        boolean fromFragment = getIntent().getBooleanExtra("fromFragment", false);
        if (shared.girisYapildiMi()||fromFragment) {
            String id=shared.getid();
            String email = shared.getEmail();
            String kAdi = shared.getKullaniciAdi();

            Kullanici kullanici = new Kullanici(id, kAdi, email);
            MainActivity.kullanicistatic = kullanici;
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
        ArkadasEkle();
        GrupOlustur();
        BorcIsteme();
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
        int sayi = rnd.nextInt(41);
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
                ArkadasEklemeDb(kullanici);
            }

            @Override
            public void onArkadasCıkarTiklandi(Kullanici kullanici) {
                ArkadasCikarmaDb(kullanici);
            }
        });
        recycler.setAdapter(adapter);
        dialog.show();
    }

    public void ArkadasEklemeDb(Kullanici kullanici){
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

    }
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
                    } else {
                       uyariMesaj.BasarisizDurum("Grup ismi boş olamaz",1000);
                    }
                });

                builder.setNegativeButton("İptal", (dialogInterface, which) -> dialogInterface.cancel());

                builder.show();

            }
            dialog.show();
        });

    }
    public void GrupDb(String grupAdi ,ArrayList<Kullanici> secilenler,UyariMesaj mesaj){
        ArrayList<String>uyeIDleri=new ArrayList<>();
        for(Kullanici uye:secilenler){
            uyeIDleri.add(uye.getKullaniciId());
        }
        Map<String, Object> grupVerisi = new HashMap<>();
        grupVerisi.put("grupAdi", grupAdi);
        grupVerisi.put("uyeler", uyeIDleri);
        grupVerisi.put("olusturan", MainActivity.kullanicistatic.getKullaniciId());
        grupVerisi.put("olusturmaTarihi", FieldValue.serverTimestamp());

        db.collection("gruplar")
                .add(grupVerisi)
                .addOnSuccessListener(aVoid -> {
                   mesaj.BasariliDurum( "Grup oluşturuldu!",1000);

                   //BURDAN SONRA NOLCAK GRUBA YONLENDRMESİ LAZIM
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
                GrupDb();

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
                dialog.setContentView(R.layout.borc_detay);
                EditText edtMiktar = dialog.findViewById(R.id.edtMiktar);
                EditText edtAciklama = dialog.findViewById(R.id.edtAciklama);
                EditText edtTarih = dialog.findViewById(R.id.edtTarih);
                Button btnGonder = dialog.findViewById(R.id.btnGonder);

                btnGonder.setOnClickListener(v2 -> {
                    String miktar = edtMiktar.getText().toString().trim();
                    String aciklama = edtAciklama.getText().toString().trim();
                    String tarih = edtTarih.getText().toString().trim();

                    if (miktar.isEmpty()) {
                        Toast.makeText(this, "Miktar boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(tarih.isEmpty()){
                        Toast.makeText(this, "Tarih boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        //burada o kisinin profiline yonlendirip borcu atcak
                        dialog.dismiss();
                    }
                });
            }
        });
    }
    public void GrupDb() {
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
                dialog.setContentView(R.layout.borc_detay);
                EditText edtMiktar = dialog.findViewById(R.id.edtMiktar);
                EditText edtAciklama = dialog.findViewById(R.id.edtAciklama);
                EditText edtTarih = dialog.findViewById(R.id.edtTarih);
                Button btnGonder = dialog.findViewById(R.id.btnGonder);

                btnGonder.setOnClickListener(v2 -> {
                    String miktar = edtMiktar.getText().toString().trim();
                    String aciklama = edtAciklama.getText().toString().trim();
                    String tarih = edtTarih.getText().toString().trim();

                    if (miktar.isEmpty()) {
                        Toast.makeText(this, "Miktar boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(tarih.isEmpty()){
                        Toast.makeText(this, "Tarih boş olamaz", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        //burada o kisinin profiline yonlendirip borcu atcak
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}