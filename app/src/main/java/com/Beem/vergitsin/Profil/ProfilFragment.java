package com.Beem.vergitsin.Profil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Beem.vergitsin.Arkadaslar.ArkadaslarFragment;
import com.Beem.vergitsin.Gruplar.GruplarFragment;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.SharedPreferencesK;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;

public class ProfilFragment extends Fragment {
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

    private SharedPreferencesK yerelKayit;

    // Düzenle Butonu
    private Button editProfileButton;

    // Borç Rozeti
    private TextView borcSayisiText;

    private Kullanici kullanici;

    private ProfilYonetici yonetici;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profil, container, false);

        menuButton = view.findViewById(R.id.menuButton);
        borcIsteButton = view.findViewById(R.id.borcIsteButton);
        arkEkle = view.findViewById(R.id.arkEkle);
        arkCikart = view.findViewById(R.id.arkCikart);

        profilFoto = view.findViewById(R.id.profilFoto);
        userName = view.findViewById(R.id.userName);
        bioText = view.findViewById(R.id.bioText);
        arkSayisi = view.findViewById(R.id.arkSayisi);
        grupSayisi = view.findViewById(R.id.grupSayisi);

        editProfileButton = view.findViewById(R.id.editProfileButton);
        borcSayisiText = view.findViewById(R.id.borcSayisiText);

        TemelGorunumAyarlari();

        yerelKayit = new SharedPreferencesK(requireContext());

        kullanici = MainActivity.kullanicistatic;
        YerelKullanici();
        yonetici = new ProfilYonetici(kullanici);

        yonetici.ProfilDoldur(()->{
            KullaniciDoldur();
        }, ()->{
            YerelKullaniciKayitGuncelle();
        });

        menuButton.setOnClickListener(v->{ MenuGoster(v); });
        editProfileButton.setOnClickListener(v ->{ ProfilEdit(); });
        arkSayisi.setOnClickListener(v ->{ Arkadaslar(); });
        grupSayisi.setOnClickListener(v ->{ Gruplar(); });

        return view;
    }

    private void ProfilEdit(){
        ProfilDuzenleBottomSheet bottomSheet = new ProfilDuzenleBottomSheet((username, bio, secilenFoto)->{
            yonetici.ProfilDuzenle(username,bio,secilenFoto);
        },yonetici);
        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
    }

    private void KullaniciDoldur(){
        if(!isAdded()) return;
        String arksayisi = "Arkadaşlar: "+ kullanici.getArkSayisi();
        String grupssayisi = "Gruplar: "+ kullanici.getGrupSayisi();
        String borcsayisi = "Verdiği Borç: "+ kullanici.getBorcSayisi();
        userName.setText(kullanici.getKullaniciAdi());
        bioText.setText(kullanici.getBio());
        profilFoto.setImageResource(getResources().getIdentifier(kullanici.getProfilFoto(), "drawable", requireContext().getPackageName()));
        arkSayisi.setText(arksayisi);
        grupSayisi.setText(grupssayisi);
        borcSayisiText.setText(borcsayisi);
    }
    private void YerelKullanici(){
        kullanici.setProfilFoto(yerelKayit.getProfilFoto());
        kullanici.setKullaniciAdi(yerelKayit.getKullaniciAdi());
        kullanici.setBio(yerelKayit.getBio());
        kullanici.setArkSayisi(yerelKayit.getArkadasSayisi());
        kullanici.setGrupSayisi(yerelKayit.getGrupSayisi());
        kullanici.setBorcSayisi(yerelKayit.getVerdigiBorc());
        KullaniciDoldur();
    }
    private void YerelKullaniciKayitGuncelle(){
        yerelKayit.setArkadasSayisi(kullanici.getArkSayisi());
        yerelKayit.setGrupSayisi(kullanici.getGrupSayisi());
        yerelKayit.setVerdigiBorc(kullanici.getBorcSayisi());
        System.out.println(kullanici.getProfilFoto());
        yerelKayit.ProfilGuncelle(kullanici.getProfilFoto(),kullanici.getBio(),kullanici.getKullaniciAdi());
        yerelKayit.EditorAply();
    }

    private void Arkadaslar(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ArkadaslarFragment fragment = new ArkadaslarFragment();
        fragmentTransaction.replace(R.id.konteynir, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void Gruplar(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GruplarFragment fragment = new GruplarFragment();
        fragmentTransaction.replace(R.id.konteynir, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void TemelGorunumAyarlari(){
        arkCikart.setVisibility(View.GONE);
        arkEkle.setVisibility(View.GONE);
        borcIsteButton.setVisibility(View.GONE);
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
        engelle.setVisibility(View.GONE);

        cikis.setOnClickListener(v->{ popupWindow.dismiss(); yonetici.CikisYap(requireContext()); });
        iptal.setOnClickListener(v->{ popupWindow.dismiss(); });

        popupWindow.showAsDropDown(vektor, -20, 10);
    }
}
