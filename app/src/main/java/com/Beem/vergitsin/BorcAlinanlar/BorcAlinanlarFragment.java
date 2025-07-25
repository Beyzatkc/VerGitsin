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

import java.util.ArrayList;

public class BorcAlinanlarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.borcalinanlar, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.borcRecyclerView);
        ArrayList<AlinanBorcModel> borclar = new ArrayList<>();
        borclar.add(new AlinanBorcModel("Çay ve Simit", "₺12.50", "01.08.2025", "25 Temmuz"));
        borclar.add(new AlinanBorcModel("Karım ve Ben", "₺12.50", "01.08.2025", "06 Mayıs"));
        borclar.add(new AlinanBorcModel("Çay ve Simit", "₺12.50", "01.08.2025", "25 Temmuz"));

        AlinanBorcAdapter adapter = new AlinanBorcAdapter(borclar);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}
