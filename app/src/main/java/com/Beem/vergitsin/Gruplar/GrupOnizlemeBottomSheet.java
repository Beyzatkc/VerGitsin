package com.Beem.vergitsin.Gruplar;

import static com.google.common.reflect.Reflection.getPackageName;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Profil.DigerProfilFragment;
import com.Beem.vergitsin.Profil.ProfilFragment;
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
    private Button grupDuzenle;
    private Button gruptanCik;
    private Button arkadasEkle;
    private Runnable gruptanCikildi;
    private Runnable gruplarAdapterGuncelle;
    private Button arkadasCikar;

    private GruplarYonetici yonetici;

    private Grup grup;

    public GrupOnizlemeBottomSheet(Grup grup, Runnable gruptanCikildi, Runnable gruplarAdapterGuncelle) {
        this.grup = grup;
        this.gruptanCikildi = gruptanCikildi;
        this.gruplarAdapterGuncelle = gruplarAdapterGuncelle;
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
        grupDuzenle = view.findViewById(R.id.grupDuzenle);
        gruptanCik = view.findViewById(R.id.gruptanCik);
        arkadasEkle = view.findViewById(R.id.arkadasEkle);
        arkadasCikar = view.findViewById(R.id.arkadasCikar);

        yonetici = new GruplarYonetici();

        GrupOnizlemeViewAyarlari();

        textGrupAdi.setText(grup.getGrupAdi());
        textGrupAciklama.setText(grup.getGrupHakkinda());
        int resId = getResources().getIdentifier(grup.getGrupFoto(), "drawable", requireContext().getPackageName());
        imageGrupProfil.setImageResource(resId);
        textGrupTarih.setText(formatTarih(grup.getOlusturmaTarihi()));

        uyeler = new ArrayList<>();
        uyelerAdapter = new UyelerAdapter(uyeler,uye -> { ProfileYonlendir(uye); }, grup.getOlusturan());
        recyclerUyeler.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerUyeler.setAdapter(uyelerAdapter);
        UyeleriDoldur();

        grupDuzenle.setOnClickListener(v->{ GrupDuzenleyeYonlendir(); });
        gruptanCik.setOnClickListener(v->{ GruptanCik(); });
        arkadasEkle.setOnClickListener(v->{ ArkadasEkle(); });
        arkadasCikar.setOnClickListener(v->{ ArkadasCikar(); });
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

    private void GrupOnizlemeViewAyarlari(){
        if(!grup.getOlusturan().equals(MainActivity.kullanicistatic.getKullaniciId())){
            arkadasCikar.setVisibility(View.GONE);
            grupDuzenle.setVisibility(View.GONE);
            arkadasEkle.setVisibility(View.GONE);
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

    private void ProfileYonlendir(Kullanici uye){
        this.dismiss();
        if(uye.getKullaniciId().equals(MainActivity.kullanicistatic.getKullaniciId())){
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ProfilFragment fragment = new ProfilFragment();
            fragmentTransaction.replace(R.id.konteynir, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return;
        }
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DigerProfilFragment fragment = new DigerProfilFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("kullanici", uye);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.konteynir, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void GrupDuzenleyeYonlendir(){
        GrupDuzenleBottomSheet bottomSheet = new GrupDuzenleBottomSheet(grup,() -> {
            textGrupAciklama.setText(grup.getGrupHakkinda());
            int resId = getResources().getIdentifier(grup.getGrupFoto(), "drawable", requireContext().getPackageName());
            imageGrupProfil.setImageResource(resId);
        });
        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
    }



    private void GruptanCik(){
        if(grup.getOlusturan().equals(MainActivity.kullanicistatic.getKullaniciId())){
            YeniYoneticiSec();
            return;
        }
        yonetici.GruptanCik(grup, () -> {
            // cikildi
            gruptanCikildi.run();
            this.dismiss();
        }, () -> {
            // hata
        });
    }

    private void ArkadasEkle(){
        GrupOlusturGorunumu();
    }


    private void GrupOlusturGorunumu() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.grup_ark_ekle);
        RecyclerView recyclerArkadas = dialog.findViewById(R.id.recyclerArkadas);
        Button btnDevamEt = dialog.findViewById(R.id.btnDevamEt);
        btnDevamEt.setText("Arkadaş Ekle");
        yonetici.GrupArkEklemekIcinArkCek(arklar -> {
            GrupArkadasEkleAdapter adapter = new GrupArkadasEkleAdapter(arklar,requireContext(),grup);
            recyclerArkadas.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerArkadas.setAdapter(adapter);
            btnDevamEt.setOnClickListener(v -> {
                yonetici.GrupArkEkle(grup,adapter.getYeniUyeler(),()->{
                    uyeler.addAll(adapter.getYeniArklar());
                    gruplarAdapterGuncelle.run();
                    uyelerAdapter.notifyDataSetChanged();
                    adapter.getYeniUyeler().clear();
                    adapter.getYeniArklar().clear();
                });
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    private void ArkadasCikar(){
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.grup_ark_cikart);
        RecyclerView recyclerArkadas = dialog.findViewById(R.id.recyclerArkadasCikar);
        Button btnDevamEt = dialog.findViewById(R.id.btnCikarmayiOnayla);
        GrupArkadasCikarAdapter adapter = new GrupArkadasCikarAdapter(BenYokumListesi(),requireContext());
        recyclerArkadas.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerArkadas.setAdapter(adapter);
        btnDevamEt.setOnClickListener(v -> {
            yonetici.GrupKisiCikar(grup,adapter.getSecilenCikacaklar(),()->{


            for(Kullanici kullanici: adapter.getSecilenKullanicilar()){
                for(int i = 0; i < uyeler.size(); i++){
                    if(uyeler.get(i).getKullaniciId().equals(kullanici.getKullaniciId())){
                        uyeler.remove(i);
                        break;
                    }
                }
            }

            gruplarAdapterGuncelle.run();
            uyelerAdapter.notifyDataSetChanged();
            adapter.getSecilenCikacaklar().clear();
            adapter.getSecilenKullanicilar().clear();
            dialog.dismiss();
            });
        });
        dialog.show();

    }

    private void YeniYoneticiSec(){
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.grup_ark_cikart);
        RecyclerView recyclerArkadas = dialog.findViewById(R.id.recyclerArkadasCikar);
        Button btnDevamEt = dialog.findViewById(R.id.btnCikarmayiOnayla);
        TextView baslik = dialog.findViewById(R.id.baslik);
        baslik.setText("Yeni Yönetici");
        btnDevamEt.setText("Yeni Yöneticiyi Seç");
        YoneticiSecAdapter adapter = new YoneticiSecAdapter(requireContext(),BenYokumListesi());
        recyclerArkadas.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerArkadas.setAdapter(adapter);
        btnDevamEt.setOnClickListener(v -> {
            String kullaniciId = adapter.getSonSecili();
            grup.setOlusturan(kullaniciId);
            yonetici.GruptanCik(grup, () -> {
                // cikildi
                gruptanCikildi.run();
                yonetici.YeniGrupYoneticisi(grup.getGrupId(), kullaniciId);
            }, () -> {
                // hata
            });
            dialog.dismiss();
            this.dismiss();
        });
        dialog.show();
    }

    private ArrayList<Kullanici> BenYokumListesi(){
        ArrayList<Kullanici> benYokumListesi = new ArrayList<>();
        for (Kullanici k: uyeler){
            if(k.getKullaniciId().equals(MainActivity.kullanicistatic.getKullaniciId())){
                continue;
            }
            benYokumListesi.add(k);
        }
        return benYokumListesi;
    }
}