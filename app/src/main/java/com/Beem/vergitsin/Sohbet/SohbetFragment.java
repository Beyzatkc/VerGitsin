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

import com.Beem.vergitsin.ArkadasAdapter;
import com.Beem.vergitsin.Kullanici.KullaniciViewModel;
import com.Beem.vergitsin.Mesaj.MesajFragment;
import com.Beem.vergitsin.R;
import com.google.firebase.Timestamp;

public class SohbetFragment extends Fragment {
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
                    Bundle bundle = new Bundle();
                    bundle.putString("kaynak", "SohbetAdapter");
                    bundle.putString("sohbetId", sohbet.getSohbetID());
                    bundle.putString("sohbetedilenAd",sohbet.getKullaniciAdi());
                    bundle.putString("sohbetEdilenPP",sohbet.getPpfoto());
                    Fragment mesajFragment = new MesajFragment();
                    mesajFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.konteynir, mesajFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            recyclerView.setAdapter(adapter);
        });
        return view;
    }


}