package com.Beem.vergitsin.BorcVerilenler;

import static androidx.core.content.ContextCompat.getSystemService;

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
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Profil.DigerProfilFragment;
import com.Beem.vergitsin.Profil.ProfilYonetici;
import com.Beem.vergitsin.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BorcVerilenlerFragment extends Fragment {

    private BorcVerilenlerYonetici yonetici;
    private VerilenBorcAdapter adapter;
    private ArrayList<VerilenBorcModel> borclar;
    private FirebaseFunctions fonksiyonlar = FirebaseFunctions.getInstance();

    private RewardedAd reklam;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(requireContext(), initializationStatus -> {});
        loadRewardedAd();
    }

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
                Hatirlat(borcModel,position);
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

    private void Hatirlat(VerilenBorcModel borcModel,int pozisyon){
        System.out.println("hatilar valıstı");
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(borcModel.getKullaniciId())
                .get()
                .addOnSuccessListener(doc->{
                    String token = doc.getString("fcmToken");
                    String borcVerenAdi = borcModel.getVerilenAdi();
                    int miktar = Integer.valueOf(borcModel.getMiktar());
                    System.out.println("hatilar valıstı icerde");
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(borcModel.getKullaniciId())
                            .collection("alinanlar")
                            .document(borcModel.getID())
                            .get()
                            .addOnSuccessListener(doc1->{
                                Boolean odendiMi = doc1.getBoolean("odendiMi");
                                if(Boolean.FALSE.equals(odendiMi) || odendiMi==null){
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("token", token);
                                    data.put("borcVerenAdi", borcVerenAdi);
                                    data.put("miktar", miktar);
                                    fonksiyonlar
                                            .getHttpsCallable("borcHatirlat")
                                            .call(data)
                                            .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                    Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                                                    Boolean success = (Boolean) result.get("success");
                                                    if (success) {
                                                        if(reklam!=null){
                                                            reklam.show(requireActivity(), rewardItem ->{
                                                                Toast.makeText(requireContext(), "Ödül kazandınız!", Toast.LENGTH_SHORT).show();
                                                            });
                                                        }
                                                    } else {
                                                        String errorMsg = (String) result.get("error");
                                                        System.out.println("Hata: " + errorMsg);
                                                    }
                                                }
                                            });
                                }
                                else{
                                    borcModel.setOdendiMi(true);
                                    adapter.notifyItemChanged(pozisyon);
                                }
                            });
                });

    }



    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(requireContext(),
                "ca-app-pub-3940256099942544/5224354917",
                adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        reklam = null;
                        Toast.makeText(requireContext(), "Reklam yüklenemedi: " + loadAdError, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        super.onAdLoaded(rewardedAd);

                        reklam = rewardedAd;

                        // Reklam kapandığında tekrar yükle
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                reklam = null;
                                loadRewardedAd();
                            }
                        });
                    }
                });
    }

}
