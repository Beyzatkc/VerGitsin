package com.Beem.vergitsin.Gruplar;

import static com.google.common.reflect.Reflection.getPackageName;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Arkadaslar.Arkadas;
import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GrupOnizlemeBottomSheet extends BottomSheetDialogFragment {


    private TextView textGrupAdi;
    private ImageView imageGrupProfil;
    private RecyclerView recyclerUyeler;
    private TextView textGrupAciklama;
    private TextView textGrupTarih;
    private ArrayList<Kullanici> uyeler;
    private UyelerAdapter uyelerAdapter;

    private Grup grup;

    public GrupOnizlemeBottomSheet(Grup grup) {
        this.grup = grup;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.grup_onizleme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textGrupAdi = view.findViewById(R.id.textGrupAdi);
        imageGrupProfil = view.findViewById(R.id.imageGrupProfil);
        recyclerUyeler = view.findViewById(R.id.recyclerUyeler);
        textGrupAciklama = view.findViewById(R.id.textGrupAciklama);
        textGrupTarih = view.findViewById(R.id.textGrupTarih);

        textGrupAdi.setText(grup.getGrupAdi());
        textGrupAciklama.setText(grup.getGrupHakkinda());
        int resId = getResources().getIdentifier(grup.getGrupFoto(), "drawable", requireContext().getPackageName());
        imageGrupProfil.setImageResource(resId);
        textGrupTarih.setText(formatTarih(grup.getOlusturmaTarihi()));

        uyeler = new ArrayList<>();
        uyelerAdapter = new UyelerAdapter(uyeler);
        recyclerUyeler.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerUyeler.setAdapter(uyelerAdapter);
        UyeleriDoldur();
    }





    private String formatTarih(Timestamp tarih) {
        Date date = tarih.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = formatter.format(date);
        return "Oluşturuldu: "+formattedDate;
    }


    private void UyeleriDoldur(){
        for(String kullaniciId : grup.getUyeler()){
            FirebaseFirestore.getInstance().collection("users")
                    .document(kullaniciId)
                    .get()
                    .addOnSuccessListener(dokuman->{
                        Dokumantasyon(dokuman);
                    });
        }
    }

    private void Dokumantasyon(DocumentSnapshot dokuman){
        Kullanici arkadas = new Kullanici();

        String ID = dokuman.getId();
        String name = dokuman.contains("kullaniciAdi") ? dokuman.getString("kullaniciAdi") : "İsim yok";
        String email = dokuman.contains("email") ? dokuman.getString("email") : "Email yok";
        String bio = dokuman.contains("Bio") ? dokuman.getString("Bio") : "";

        String profilFoto = dokuman.contains("ProfilFoto") ? dokuman.getString("ProfilFoto") : "user";

        int grupSayisi = dokuman.contains("GrupSayisi") && dokuman.getLong("GrupSayisi") != null
                ? dokuman.getLong("GrupSayisi").intValue() : 0;

        int arkSayisi = dokuman.contains("arkadaslar") && dokuman.get("arkadaslar") != null
                ? ((ArrayList<String>) dokuman.get("arkadaslar")).size() : 0;

        int borcSayisi = dokuman.contains("BorcSayisi") && dokuman.getLong("BorcSayisi") != null
                ? dokuman.getLong("BorcSayisi").intValue() : 0;

        arkadas.setKullaniciId(ID);
        arkadas.setKullaniciAdi(name);
        arkadas.setEmail(email);
        arkadas.setBio(bio);
        arkadas.setProfilFoto(profilFoto);
        arkadas.setGrupSayisi(grupSayisi);
        arkadas.setArkSayisi(arkSayisi);
        arkadas.setBorcSayisi(borcSayisi);
        System.out.println(arkadas.getKullaniciAdi());
        uyelerAdapter.getUyeler().add(arkadas);
        uyelerAdapter.notifyDataSetChanged();
    }
}
