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

import com.Beem.vergitsin.Mesaj.MesajGrupFragment;
import com.Beem.vergitsin.Mesaj.MesajKisiFragment;
import com.Beem.vergitsin.R;

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
                    Bundle bundle = new Bundle();
                    bundle.putString("kaynak", "SohbetAdapter");
                    bundle.putString("sohbetId", sohbet.getSohbetID());
                    bundle.putString("sohbetedilenAd",sohbet.getKullaniciAdi());
                    bundle.putString("sohbetEdilenPP",sohbet.getPpfoto());

                    if(sohbet.getTur().equals("grup")) {
                        Fragment mesajGrupFragment = new MesajGrupFragment();
                        mesajGrupFragment.setArguments(bundle);
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.konteynir, mesajGrupFragment)
                                .addToBackStack(null)
                                .commit();
                    }else if(sohbet.getTur().equals("kisi")){
                        Fragment mesajKisiFragment = new MesajKisiFragment();
                        mesajKisiFragment.setArguments(bundle);
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.konteynir, mesajKisiFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        });

        mViewModel.getGorulmeyenMesajSayilari().observe(getViewLifecycleOwner(), sohbet -> {
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
        return view;
    }
}