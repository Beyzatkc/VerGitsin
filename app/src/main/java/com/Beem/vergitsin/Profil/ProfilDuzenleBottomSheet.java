package com.Beem.vergitsin.Profil;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.R;
import com.Beem.vergitsin.UyariMesaj;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfilDuzenleBottomSheet extends BottomSheetDialogFragment {

    ProfilDuzenleListener duzenleListener;
    private AlertDialog dialog;
    private ImageView profileImageView;
    private EditText username;
    private ImageView changePhotoIcon;
    private EditText bio;
    private Button saveButton;
    private String secilenFoto;
    private ProfilYonetici yonetici;

    public ProfilDuzenleBottomSheet(ProfilDuzenleListener duzenleListener, ProfilYonetici yonetici) {
        this.duzenleListener = duzenleListener;
        this.yonetici = yonetici;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profil_edit_tasarim, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        username = view.findViewById(R.id.editUsername);
        bio = view.findViewById(R.id.editBio);
        saveButton = view.findViewById(R.id.saveButton);
        profileImageView = view.findViewById(R.id.profileImageView);
        changePhotoIcon = view.findViewById(R.id.changePhotoIcon);

        //ProfilYonetici.getYonetici().getKullanici().getKullaniciAdi()
        username.setText(yonetici.getKullanici().getKullaniciAdi());
        bio.setText(yonetici.getKullanici().getBio());
        secilenFoto =yonetici.getKullanici().getProfilFoto();
        SecilenFotoGuncelle(yonetici.getKullanici().getProfilFoto());

        changePhotoIcon.setOnClickListener(v -> FotoSeciciEkranGoster());
        profileImageView.setOnClickListener(v -> FotoSeciciEkranGoster());

        saveButton.setOnClickListener(v -> {
            String newUsername = username.getText().toString().trim();
            String newBio = bio.getText().toString().trim();
            if(!Kontroller(newUsername,newBio)) return;
            if(duzenleListener!=null){
                duzenleListener.onProfilKaydet(newUsername,newBio,secilenFoto);
            }
            dismiss();
        });
    }

    private void SecilenFotoGuncelle(String anahtar) {
        int resId = getResources().getIdentifier(anahtar, "drawable", requireContext().getPackageName());
        profileImageView.setImageResource(resId);
    }

    private void FotoSeciciEkranGoster() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.foto_secim_ekrani, null);

        RecyclerView fotoRecyclerView = view.findViewById(R.id.photoRecyclerView);
        fotoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        String[] fotoAnahtarlar = {
                "avatar_1", "avatar_2", "avatar_3", "avatar_4", "avatar_5",
                "avatar_6", "avatar_7", "avatar_8", "avatar_9", "avatar_10",
                "avatar_11", "avatar_12", "avatar_13", "avatar_14", "avatar_15",
                "avatar_16", "avatar_17", "avatar_18", "avatar_19", "avatar_20",
                "avatar_21"
        };

        ProfilFotoAdapter adapter = new ProfilFotoAdapter(fotoAnahtarlar, fotoAnahtar -> {
            secilenFoto = fotoAnahtar;
            SecilenFotoGuncelle(fotoAnahtar);
            dialog.dismiss();
        });

        fotoRecyclerView.setAdapter(adapter);
        builder.setView(view);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private boolean Kontroller(String newUsername, String newBio){
        UyariMesaj uyariMesaj = new UyariMesaj(getContext(),false);
        uyariMesaj.YuklemeDurum("Kaydediliyor...");
        if(newUsername.isEmpty()){
            uyariMesaj.BasarisizDurum("Kullanıcı adı boş bırakılamaz",2000);
            username.setError("Bu alan boş bırakılamaz");
            return false;
        }
        uyariMesaj.BasariliDurum("Kayıt Başarılı",2000);
        return true;
    }
}