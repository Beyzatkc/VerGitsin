package com.Beem.vergitsin.BorcVerilenler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Profil.DigerProfilFragment;
import com.Beem.vergitsin.Profil.ProfilYonetici;
import com.Beem.vergitsin.R;

import java.util.ArrayList;

public class BorcVerilenlerFragment extends Fragment {

    private BorcVerilenlerYonetici yonetici;
    private VerilenBorcAdapter adapter;
    private ArrayList<VerilenBorcModel> borclar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.borcverilenler, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.borcRecyclerView);
        borclar = new ArrayList<>();

        adapter = new VerilenBorcAdapter(borclar);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        yonetici = new BorcVerilenlerYonetici(adapter);
        adapter.setHatirlatClickListener(new VerilenBorcAdapter.OnHatirlatClickListener() {
            @Override
            public void onHatirlatClick(VerilenBorcModel borcModel, int position) {

            }

            @Override
            public void onProfileGec(String id) {
                ProfileGec(id);
            }
        });

        yonetici.TumVerilenBorclariCek();

        return view;
    }



    private void ProfileGec(String id){
        Kullanici k1 = new Kullanici();
        k1.setKullaniciId(id);
        ProfilYonetici yonetici = new ProfilYonetici(k1);
        yonetici.ProfilDoldur(()->{
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DigerProfilFragment profilFragment = new DigerProfilFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("kullanici", k1);
            profilFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.konteynir, profilFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        },()->{});
    }
}
