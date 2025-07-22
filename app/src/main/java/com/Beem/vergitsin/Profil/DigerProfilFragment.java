package com.Beem.vergitsin.Profil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Beem.vergitsin.Arkadaslar.Arkadas;
import com.Beem.vergitsin.Arkadaslar.ArkadaslarFragment;
import com.Beem.vergitsin.Arkadaslar.DigerKullaniciArkadasFragment;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.SharedPreferencesK;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class DigerProfilFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Üst Çubuk Butonları
    private ImageButton menuButton;
    private ImageButton borcIsteButton;
    private ImageButton arkEkle;
    private ImageButton arkCikart;

    // Profil Kartı
    private ImageView profilFoto;
    private TextView userName;
    private TextView bioText;
    private TextView arkSayisi;
    private TextView grupSayisi;

    private LinearLayout borcSayisiLinearLayout;
    private LinearLayout ustCubuk;
    private LinearLayout profilKarti;
    private ShimmerFrameLayout yukleme;
    private LinearLayout beniEngelledi;

    private SharedPreferencesK yerelKayit;

    // Düzenle Butonu
    private Button editProfileButton;

    // Borç Rozeti
    private TextView borcSayisiText;

    private Arkadas kullanici;

    private ProfilYonetici yonetici;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profil, container, false);
        if (getArguments() != null) {
            kullanici = (Arkadas) getArguments().getSerializable("kullanici");
            yonetici = new ProfilYonetici(kullanici);
        }
        else{
            yonetici = new ProfilYonetici(null);
        }

        menuButton = view.findViewById(R.id.menuButton);
        borcIsteButton = view.findViewById(R.id.borcIsteButton);
        arkEkle = view.findViewById(R.id.arkEkle);
        arkCikart = view.findViewById(R.id.arkCikart);
        borcSayisiLinearLayout = view.findViewById(R.id.borcSayisiLinearLayout);
        ustCubuk = view.findViewById(R.id.ustCubuk);
        profilKarti = view.findViewById(R.id.profilKarti);
        yukleme = view.findViewById(R.id.shimmerFrameLayout);
        beniEngelledi = view.findViewById(R.id.engelleyenProfilKarti);
        YuklemeBaslat();

        profilFoto = view.findViewById(R.id.profilFoto);
        userName = view.findViewById(R.id.userName);
        bioText = view.findViewById(R.id.bioText);
        arkSayisi = view.findViewById(R.id.arkSayisi);
        grupSayisi = view.findViewById(R.id.grupSayisi);

        editProfileButton = view.findViewById(R.id.editProfileButton);
        borcSayisiText = view.findViewById(R.id.borcSayisiText);

        TemelGorunumAyarlari();
        ProilViewDoldur();

        arkSayisi.setOnClickListener(v->{ DigerKullaniciArkadas(); });
        arkCikart.setOnClickListener(v->{ ArkadasCikar(); });
        arkEkle.setOnClickListener(v->{ ArkadasEkle(); });
        menuButton.setOnClickListener(v->{ MenuGoster(v); });

        yonetici.ProfilDoldur(()->{
            ProilViewDoldur();
        },()->{});


        return view;
    }


    private void TemelGorunumAyarlari(){
        editProfileButton.setVisibility(View.GONE);
        yonetici.KarsiTarafEngelKontrol(()->{
            if(kullanici.isKarsiTarafEngellediMi()) {
                ustCubuk.setVisibility(View.GONE);
                profilKarti.setVisibility(View.GONE);
                beniEngelledi.setVisibility(View.VISIBLE);
               /* arkEkle.setVisibility(View.GONE);
                arkCikart.setVisibility(View.GONE);
                borcIsteButton.setVisibility(View.GONE);
                arkSayisi.setVisibility(View.GONE);
                grupSayisi.setVisibility(View.GONE);
                editProfileButton.setVisibility(View.GONE);
                borcSayisiLinearLayout.setVisibility(View.GONE);
                profilFoto.setImageResource(R.drawable.user);
                menuButton.setVisibility(View.GONE);
                userName.setText("Kullanıcı adı yok");
                bioText.setText("Bio yok");*/
            }
            else if(kullanici.isEngelliMi()){
                arkEkle.setVisibility(View.GONE);
                arkCikart.setVisibility(View.GONE);
                borcIsteButton.setVisibility(View.GONE);
                arkSayisi.setVisibility(View.GONE);
                grupSayisi.setVisibility(View.GONE);
                editProfileButton.setVisibility(View.GONE);
                borcSayisiLinearLayout.setVisibility(View.GONE);
                ustCubuk.setVisibility(View.VISIBLE);
                profilKarti.setVisibility(View.VISIBLE);
            }
            else{
                if(kullanici.isArkdasMi()){
                    borcIsteButton.setVisibility(View.VISIBLE);
                    arkEkle.setVisibility(View.GONE);
                    arkCikart.setVisibility(View.VISIBLE);
                }
                else{
                    borcIsteButton.setVisibility(View.GONE);
                    arkEkle.setVisibility(View.VISIBLE);
                    arkCikart.setVisibility(View.GONE);
                }
                ustCubuk.setVisibility(View.VISIBLE);
                profilKarti.setVisibility(View.VISIBLE);
            }
            YuklemeBitir();
        });
    }

    private void ProilViewDoldur(){
        String arksayisi = "Arkadaşlar: "+ kullanici.getArkSayisi();
        String grupssayisi = "Gruplar: "+ kullanici.getGrupSayisi();
        String borcsayisi = "Verdiği Borç: "+ kullanici.getBorcSayisi();

        userName.setText(STKontrol(kullanici.getKullaniciAdi(),"KullaniciAdi"));
        bioText.setText(STKontrol(kullanici.getBio(),""));
        profilFoto.setImageResource(getResources().getIdentifier(STKontrol(kullanici.getProfilFoto(),"user"), "drawable", requireContext().getPackageName()));
        arkSayisi.setText(arksayisi);
        grupSayisi.setText(grupssayisi);
        borcSayisiText.setText(borcsayisi);
    }
    private String STKontrol(String gelen, String varsayilan){
        return gelen != null ? gelen : varsayilan;
    }

    private void DigerKullaniciArkadas(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("kullanici",kullanici);
        DigerKullaniciArkadasFragment fragment = new DigerKullaniciArkadasFragment();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.konteynir, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void ArkadasEklemeDb(Arkadas kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayUnion(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Arkadaş eklendi!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Ekleme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void ArkadasCikarmaDb(Arkadas kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayRemove(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Arkadaş çıkarıldı!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Ekleme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void ArkadasEkle(){
        if(kullanici==null) return;
        if(kullanici.isArkdasMi()) return;
        kullanici.setArkdasMi(true);
        arkEkle.setVisibility(View.GONE);
        borcIsteButton.setVisibility(View.VISIBLE);
        arkCikart.setVisibility(View.VISIBLE);
        ArkadasEklemeDb(kullanici);
    }
    private void ArkadasCikar(){
        if(kullanici==null) return;
        if(!kullanici.isArkdasMi()) return;
        kullanici.setArkdasMi(false);
        arkEkle.setVisibility(View.VISIBLE);
        borcIsteButton.setVisibility(View.GONE);
        arkCikart.setVisibility(View.GONE);
        ArkadasCikarmaDb(kullanici);
    }


    private void MenuGoster(View vektor){
        View menu = LayoutInflater.from(requireContext()).inflate(R.layout.uc_cizgi, null);
        PopupWindow popupWindow = new PopupWindow(menu,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setElevation(20);

        TextView cikis = menu.findViewById(R.id.btn_logout);
        TextView engelle = menu.findViewById(R.id.btn_block);
        TextView iptal = menu.findViewById(R.id.btn_cancel);
        cikis.setVisibility(View.GONE);
        if(kullanici.isEngelliMi()) engelle.setText("Engeli Kaldır");
        else engelle.setText("Hesabı Engelle");

        engelle.setOnClickListener(v->{
            Engelliyorum();
            popupWindow.dismiss();
        });
        iptal.setOnClickListener(v->{ popupWindow.dismiss(); });

        popupWindow.showAsDropDown(vektor, -20, 10);
    }

    private void Engelliyorum(){
        yonetici.Engelle(()->{
            arkEkle.setVisibility(View.GONE);
            arkCikart.setVisibility(View.GONE);
            borcIsteButton.setVisibility(View.GONE);
            arkSayisi.setVisibility(View.GONE);
            grupSayisi.setVisibility(View.GONE);
            editProfileButton.setVisibility(View.GONE);
            borcSayisiLinearLayout.setVisibility(View.GONE);
        },()->{
            arkCikart.setVisibility(View.GONE);
            arkEkle.setVisibility(View.VISIBLE);
            borcIsteButton.setVisibility(View.GONE);
            arkSayisi.setVisibility(View.VISIBLE);
            grupSayisi.setVisibility(View.VISIBLE);
            borcSayisiLinearLayout.setVisibility(View.VISIBLE);
        });
    }

    private void YuklemeBaslat(){
        ustCubuk.setVisibility(View.GONE);
        profilKarti.setVisibility(View.GONE);
        yukleme.startShimmer();
        yukleme.setVisibility(View.VISIBLE);
    }
    private void YuklemeBitir(){
        yukleme.stopShimmer();
        yukleme.setVisibility(View.GONE);
    }

}
