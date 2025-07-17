package com.Beem.vergitsin;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.FrameLayout;
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
import com.Beem.vergitsin.Profil.ProfilFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static Kullanici kullanicistatic;
    private ArrayList<String>sozler;
    private TextView borcMizahIcerik;
    private LinearLayout arkadasEkleLayout;
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
        SharedPreferencesK shared = new SharedPreferencesK(this);
        boolean fromFragment = getIntent().getBooleanExtra("fromFragment", false);
        if (shared.girisYapildiMi()||fromFragment) {
            String id=shared.getid();
            String email = shared.getEmail();
            String kAdi = shared.getKullaniciAdi();

            Kullanici kullanici = new Kullanici(id, kAdi, email); // şifreyi tutmana gerek yok
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
        ArkadasEkle();
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
        sozler.add("Parayı vermek kolay, dostluğu yaşatmak güzeldir. 🌟💬");
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
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tum.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String email=doc.getString("email");
                        String id=doc.getId();
                        String kAdi=doc.getString("kullaniciAdi");
                        Kullanici kullanici=new Kullanici(id,kAdi,email);
                        tum.add(kullanici);
                    }
                    Kullancilar(tum);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Kullanıcılar alınamadı!", Toast.LENGTH_SHORT).show();
                });
    }

    public void Kullancilar(ArrayList<Kullanici> kullanicilar){
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.kullanicilar_recycler);

        RecyclerView recycler=new RecyclerView(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        KullanicilarAdapter adapter = new KullanicilarAdapter(kullanicilar, this, kullanici -> {
                    ArkadasEklemeDb(kullanici);
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

}