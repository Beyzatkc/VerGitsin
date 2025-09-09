package com.Beem.vergitsin.Sohbet;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Beem.vergitsin.FragmentYonlendirici;
import com.Beem.vergitsin.Mesaj.MesajGrupFragment;
import com.Beem.vergitsin.Mesaj.MesajKisiFragment;
import com.Beem.vergitsin.R;

import java.util.ArrayList;
import java.util.Map;

public class SohbetFragment extends Fragment{
    private SohbetViewModel mViewModel;
    private RecyclerView recyclerView;
    private SohbetAdapter adapter;

    public static SohbetFragment newInstance() {
        return new SohbetFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SohbetViewModel.class);
    }
    public void msjlaraUlas() {
        ArrayList<Sohbet> sohbetler = mViewModel._sohbetler.getValue();

        if (sohbetler != null&&!sohbetler.isEmpty()) {
            for (int i = 0; i < sohbetler.size(); i++) {
                Sohbet sohbet = sohbetler.get(i);
                if (sohbet != null && Boolean.TRUE.equals(sohbet.getSohbeteGirildiMi())) {
                    sohbet.setSohbeteGirildiMi(false);
                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        msjlaraUlas();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sohbet, container, false);
        recyclerView=view.findViewById(R.id.recyclerSohbetler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mViewModel.SohbetleriCek();
        mViewModel.sohbetler().observe(getViewLifecycleOwner(),sohbetler -> {
             adapter = new SohbetAdapter(sohbetler, requireContext(), new SohbetAdapter.OnSohbetClickListener() {
                @Override
                public void onSohbetClicked(Sohbet sohbet) {
                    sohbet.setGorulmemisMesajSayisi(0);
                    sohbet.setSohbeteGirildiMi(true);
                    if (sohbet.getTur().equals("kisi")){
                                Bundle bundle = new Bundle();
                                bundle.putString("sohbetId", sohbet.getSohbetID());
                                bundle.putString("sohbetedilenAd",sohbet.getKullaniciAdi());
                                bundle.putString("sohbetEdilenPP",sohbet.getPpfoto());
                                Long acilmaZamaniObj = sohbet.getAcilmazamani();
                                if (acilmaZamaniObj != null) {
                                    bundle.putLong("acilmaZamani", acilmaZamaniObj);
                                }
                                Long cikilmaZaman = sohbet.getEskiGrupZaman();
                                if (cikilmaZaman!=null && cikilmaZaman!=0){
                                    System.out.println("sf de girdim");
                                    System.out.println(sohbet.getEskiGrupZaman()+"---"+cikilmaZaman);
                                    bundle.putLong("CikilmaZaman",cikilmaZaman);
                                    bundle.putString("kaynak", "cikilmis");
                                }
                                else{
                                    bundle.putString("kaynak", "SohbetAdapter");
                                }
                            Fragment mesajKisiFragment = new MesajKisiFragment();
                            mesajKisiFragment.setArguments(bundle);
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.konteynir, mesajKisiFragment)
                                    .addToBackStack(null)
                                    .commit();

                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putString("sohbetId", sohbet.getSohbetID());
                        bundle.putString("sohbetedilenAd", sohbet.getKullaniciAdi());
                        bundle.putString("sohbetEdilenPP", sohbet.getPpfoto());
                        Long acilmaZamaniObj = sohbet.getAcilmazamani();
                        if (acilmaZamaniObj != null) {
                            bundle.putLong("acilmaZamani", acilmaZamaniObj);
                        }
                        Long cikilmaZaman = sohbet.getEskiGrupZaman();
                        if (cikilmaZaman != null && cikilmaZaman != 0) {
                            System.out.println("sf de girdim");
                            System.out.println(sohbet.getEskiGrupZaman() + "---" + cikilmaZaman);
                            bundle.putLong("CikilmaZaman", cikilmaZaman);
                            bundle.putString("kaynak", "cikilmis");
                        } else {
                            bundle.putString("kaynak", "SohbetAdapter");
                        }
                        Fragment mesajGrupFragment = new MesajGrupFragment();
                        mesajGrupFragment.setArguments(bundle);
                        FragmentYonlendirici.Yonlendir(requireActivity().getSupportFragmentManager(), mesajGrupFragment,sohbet.getSohbetID());
                        /*requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.konteynir, mesajGrupFragment)
                                .addToBackStack(null)
                                .commit();*/
                    }else if(sohbet.getTur().equals("kisi")){
                        Fragment mesajKisiFragment = new MesajKisiFragment();
                        mesajKisiFragment.setArguments(bundle);
                        FragmentYonlendirici.Yonlendir(requireActivity().getSupportFragmentManager(), mesajKisiFragment,sohbet.getSohbetID());
                        /*
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.konteynir, mesajKisiFragment)
                                .addToBackStack(null)
                                .commit();*/

                    }
                }

                 @Override
                 public void onSohbetSilindi(Sohbet sohbet) {
                    mViewModel.SohbetSilme(sohbet);
                 }
             });
            recyclerView.setAdapter(adapter);
            adapter.setKatilimciAdBulmaListener((katilimciId, callback) -> {
                mViewModel.iddenAdvePP(katilimciId, (isim, pp) -> {
                    callback.onComplete(isim, pp);
                });
            });

        });
        mViewModel.getGorulmeyenMesajSayilari().observe(getViewLifecycleOwner(), sohbet -> {
                    adapter.GorulmeyenSayisi(sohbet);
        });

        mViewModel.EskiGrupSohbetleriCek();

        mViewModel.getGorulmeyenMesajSayilariGrup().observe(getViewLifecycleOwner(), sohbet -> {
            adapter.GorulmeyenSayisi(sohbet);
        });
        mViewModel.eklenenSohbet().observe(getViewLifecycleOwner(), sohbet -> {
            if (sohbet != null) {
                adapter.sohbetEkle(sohbet);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
        mViewModel.guncellenenSohbet().observe(getViewLifecycleOwner(), sohbet -> {
            if (sohbet != null) {
                adapter.SohbetGuncelle(sohbet);
            }
        });
        mViewModel.silindiSohbet().observe(getViewLifecycleOwner(),sohbet->{
            if (sohbet != null) {
                adapter.sohbetiSil(sohbet);
            }
        });
        return view;
    }
}