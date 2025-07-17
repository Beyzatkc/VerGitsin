package com.Beem.vergitsin.Profil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.Beem.vergitsin.Kullanici.Kullanici;
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

    // Düzenle Butonu
    private Button editProfileButton;

    // Borç Rozeti
    private TextView borcSayisiText;

    private Kullanici kullanici;

    private ProfilYonetici yonetici = ProfilYonetici.getYonetici();

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

        // burasi güncellenemsi lazım
        kullanici = new Kullanici();
        kullanici.setKullaniciId("ijIsEPxQXsa4EvJGITWO");
        yonetici.setKullanici(kullanici);

        yonetici.ProfilDoldur(()->{
            KullaniciDoldur();
        });

        editProfileButton = view.findViewById(R.id.editProfileButton);

        borcSayisiText = view.findViewById(R.id.borcSayisiText);

        editProfileButton.setOnClickListener(v ->{ ProfilEdit(); });

        return view;
    }

    private void ProfilEdit(){
        ProfilDuzenleBottomSheet bottomSheet = new ProfilDuzenleBottomSheet((username, bio, secilenFoto)->{
            yonetici.ProfilDuzenle(username,bio,secilenFoto);
        });
        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
    }

    private void KullaniciDoldur(){
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
}
