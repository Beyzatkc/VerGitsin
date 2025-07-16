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

import com.Beem.vergitsin.R;

public class ProfilFragment extends Fragment {
    // Üst Çubuk Butonları
    ImageButton menuButton;
    ImageButton borcIsteButton;
    ImageButton arkEkle;
    ImageButton arkCikart;

    // Profil Kartı
    ImageView profilFoto;
    TextView userName;
    TextView bioText;
    TextView arkSayisi;
    TextView grupSayisi;

    // Düzenle Butonu
    Button editProfileButton;

    // Borç Rozeti
    TextView borcSayisiText;

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

        editProfileButton.setOnClickListener(v ->{  });

        return view;
    }

    private void ProfilEdit(){

    }
}
