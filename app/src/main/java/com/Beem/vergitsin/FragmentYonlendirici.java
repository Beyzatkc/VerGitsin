package com.Beem.vergitsin;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentYonlendirici {

    public static void Yonlendir(FragmentManager menajer, Fragment fragment, String fragmentId){
        FragmentTransaction ft = menajer.beginTransaction();

        ft.replace(R.id.konteynir, fragment, fragmentId);

        ft.addToBackStack(fragmentId);

        ft.commit();
    }
}
