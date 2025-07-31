package com.Beem.vergitsin.Arkadaslar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DigerKullaniciArkadasFragment extends Fragment {
    private TextView textViewDialogBaslik;
    private RecyclerView recyclerViewKullanicilar;
    private ArkadaslarAdapter adapter;
    private ArrayList<Arkadas> arkadaslar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArkadaslarYonetici yonetici;
    private Kullanici arkadas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.kullanicilar_recycler, container, false);

        if (getArguments() != null) {
            arkadas = (Kullanici) getArguments().getSerializable("kullanici");
        }

        textViewDialogBaslik = view.findViewById(R.id.textViewDialogBaslik);
        recyclerViewKullanicilar = view.findViewById(R.id.recyclerViewKullanicilar);
        textViewDialogBaslik.setText("Arkadaşlar");
        recyclerViewKullanicilar.setLayoutManager(new LinearLayoutManager(getContext()));

        arkadaslar = new ArrayList<>();
        adapter = new ArkadaslarAdapter(arkadaslar, getContext(), new ArkadaslarAdapter.OnArkadasEkleListener() {
            @Override
            public void onArkadasEkleTiklandi(Kullanici kullanici) {
                ArkadasEklemeDb(kullanici);
            }
            @Override
            public void onArkadasCıkarTiklandi(Kullanici kullanici) {
                ArkadasCikarmaDb(kullanici);
            }
        },getParentFragmentManager());
        recyclerViewKullanicilar.setAdapter(adapter);

        yonetici = new ArkadaslarYonetici(adapter, new ArkadaslarListenir() {
            @Override
            public void onArkadaslarSayisi(int sayi) {
                System.out.println(sayi+"dasfam");
                MainActivity.kullanicistatic.setArkSayisi(sayi);
            }
        });
        yonetici.DigerKullanicininArkadaslari(arkadas);

        return view;
    }


    public void ArkadasEklemeDb(Kullanici kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayUnion(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Arkadaş eklendi!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Ekleme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void ArkadasCikarmaDb(Kullanici kullanici){
        DocumentReference kendiDocRef = db.collection("users").document(MainActivity.kullanicistatic.getKullaniciId());
        kendiDocRef.update("arkadaslar", FieldValue.arrayRemove(kullanici.getKullaniciId()))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Arkadaş çıkarıldı!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Ekleme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}
