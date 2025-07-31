package com.Beem.vergitsin.Gruplar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Profil.ProfilFotoAdapter;
import com.Beem.vergitsin.R;
import com.Beem.vergitsin.UyariMesaj;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class GrupDuzenleBottomSheet extends BottomSheetDialogFragment{

    private FrameLayout profileContainer;
    private EditText editGrupAciklama;
    private Button buttonGrupKaydet;
    private ImageView profileImageView;
    private AlertDialog dialog;
    private UyariMesaj mesaj;
    private GruplarYonetici yonetici;

    String secilen;

    private Runnable grupOnizlemeViewGuncelle;

    private Grup grup;

    public GrupDuzenleBottomSheet(Grup grup, Runnable grupOnizlemeViewGuncelle) {
        this.grup = grup;
        this.grupOnizlemeViewGuncelle = grupOnizlemeViewGuncelle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grup_duzenle, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        profileContainer = view.findViewById(R.id.profileContainer);
        editGrupAciklama = view.findViewById(R.id.editGrupAciklama);
        buttonGrupKaydet = view.findViewById(R.id.buttonGrupKaydet);
        profileImageView = view.findViewById(R.id.profileImageView);

        mesaj = new UyariMesaj(requireContext(),false);
        yonetici = new GruplarYonetici();

        GrupViewleriniDoldur();

        profileContainer.setOnClickListener(v -> FotoSeciciEkranGoster());
        buttonGrupKaydet.setOnClickListener(v-> GrupKaydet());
    }

    private void GrupViewleriniDoldur(){
        editGrupAciklama.setText(grup.getGrupHakkinda());
        SecilenFotoGuncelle(grup.getGrupFoto());
        secilen = grup.getGrupFoto();
    }


    private void SecilenFotoGuncelle(String anahtar) {
        int resId = getResources().getIdentifier(anahtar, "drawable", requireContext().getPackageName());
        profileImageView.setImageResource(resId);
        secilen = anahtar;
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
            SecilenFotoGuncelle(fotoAnahtar);
            dialog.dismiss();
        });

        fotoRecyclerView.setAdapter(adapter);
        builder.setView(view);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void GrupKaydet(){
        grup.setGrupHakkinda(editGrupAciklama.getText().toString().trim());
        grup.setGrupFoto(secilen);
        mesaj.YuklemeDurum("Grup bilgileri güncelleniyor...");
        yonetici.GrupGuncelle(grup,() ->{
            if(grupOnizlemeViewGuncelle != null){
                grupOnizlemeViewGuncelle.run();
            }
            mesaj.BasariliDurum("Grup bilgileri güncellendi.",1000);
            this.dismiss();
        },() ->{
            mesaj.BasarisizDurum("Grup bilgileri güncellenirken hata oluştu.",1000);
        });
    }
}
