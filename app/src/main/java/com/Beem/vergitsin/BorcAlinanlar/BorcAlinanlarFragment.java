package com.Beem.vergitsin.BorcAlinanlar;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class BorcAlinanlarFragment extends Fragment {

    private BorcAlinanlarYonetici yonetici;
    private AlinanBorcAdapter adapter;
    private ArrayList<AlinanBorcModel> borclar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.borcalinanlar, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.borcRecyclerView);
        borclar = new ArrayList<>();

        adapter = new AlinanBorcAdapter(borclar);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        yonetici = new BorcAlinanlarYonetici(adapter);
        adapter.setBorcOdeClickListener(new AlinanBorcAdapter.OnBorcOdeClickListener() {
            @Override
            public void onBorcOdeClick(AlinanBorcModel borcModel, int position) {
                if(borcModel.isOdendiMi()) return;
                new AlertDialog.Builder(requireContext())
                        .setTitle("Borç Ödeme")
                        .setMessage("Bu borcu ödemek istediğinize emin misiniz?")
                        .setPositiveButton("Evet", (dialog, which) -> {
                            yonetici.BorcuOde(borcModel, position);
                        })
                        .setNegativeButton("Hayır", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }

            @Override
            public void onProfileGec(String id) {
                ProfileGec(id);
            }

            @Override
            public void onIBANClick(String iban) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("IBAN", iban);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "IBAN kopyalandı!", Toast.LENGTH_SHORT).show();
            }
        });
        yonetici.TumAlinanBorclariCek();

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
