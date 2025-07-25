package com.Beem.vergitsin.Gruplar;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Kullanici.SharedPreferencesK;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;

import java.util.ArrayList;

public class GruplarFragment extends Fragment {

    private TextView textViewDialogBaslik;
    private RecyclerView recyclerView;
    private GruplarAdapter adapter;
    private ArrayList<Grup> gruplar;
    private GruplarYonetici yonetici;
    private SharedPreferencesK yerelKayit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kullanicilar_recycler, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewKullanicilar);
        textViewDialogBaslik = view.findViewById(R.id.textViewDialogBaslik);
        textViewDialogBaslik.setText("Gruplarım");

        yerelKayit = new SharedPreferencesK(requireContext());

        gruplar = new ArrayList<>();
        adapter = new GruplarAdapter(requireContext(), gruplar, grup->{ GrupOnizlemeyeGec(grup); });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        yonetici = new GruplarYonetici(adapter);
        yonetici.TumGruplariCek(MainActivity.kullanicistatic, ()->{
            GrupSayisiniYerelKayitaEkle();
        });

        return view;
    }

    private void GrupSayisiniYerelKayitaEkle(){
        System.out.println(gruplar.size());
        yerelKayit.setGrupSayisi(gruplar.size());
        yerelKayit.EditorAply();
    }

    private void GrupOnizlemeyeGec(Grup grup){
        GrupOnizlemeBottomSheet bottomSheet = new GrupOnizlemeBottomSheet(grup);
        bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
    }

}
