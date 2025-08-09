package com.Beem.vergitsin.BorcAlinanlar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        adapter.setBorcOdeClickListener((borcModel, position) -> {
            if(borcModel.isOdendiMi()) return;
            yonetici.BorcuOde(borcModel, position);
        });
        yonetici.TumAlinanBorclariCek();

        return view;
    }
}
