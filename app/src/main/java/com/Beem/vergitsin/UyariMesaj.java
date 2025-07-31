package com.Beem.vergitsin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class UyariMesaj {
    private Dialog dialog;
    private FrameLayout yuklemeEkrani;
    private TextView durumMetni;
    private ImageView ikonBasarili, ikonHata;
    private ProgressBar yuklemeBar;

    public UyariMesaj(Context context, boolean seffafArkaPlan) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.uyari);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        dialog.setCancelable(false);

        yuklemeEkrani = dialog.findViewById(R.id.progressOverlay);
        durumMetni = dialog.findViewById(R.id.progressMessage);
        ikonBasarili = dialog.findViewById(R.id.successIcon);
        ikonHata = dialog.findViewById(R.id.basariDegilIcon);
        yuklemeBar = dialog.findViewById(R.id.progressBar);

        if (seffafArkaPlan) {
            yuklemeEkrani.setBackground(null);
        }
    }

    private void uyariGoster() {
        if(!dialog.isShowing()){
            dialog.show();
        }
    }
    private void uyariGizle() {
        if (dialog != null && dialog.isShowing()) {
            Context context = dialog.getContext();
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    dialog.dismiss();
                }
            }
            else{
                dialog.dismiss();
            }
        }
    }

    public void BasariliDurum(String mesaj,int kacSalise){
        uyariGoster();
        ikonBasarili.setVisibility(View.VISIBLE);
        yuklemeBar.setVisibility(View.GONE);
        durumMetni.setText(mesaj);
        new android.os.Handler().postDelayed(() -> {
            yuklemeEkrani.setVisibility(View.GONE);
            uyariGizle();
        }, kacSalise);
    }
    public void BasarisizDurum(String mesaj,int kacSalise){
        uyariGoster();
        ikonHata.setVisibility(View.VISIBLE);
        yuklemeBar.setVisibility(View.GONE);
        durumMetni.setText(mesaj);
        new Handler().postDelayed(() -> {
            yuklemeEkrani.setVisibility(View.GONE);
            uyariGizle();
        }, kacSalise);
    }
    public void YuklemeDurum(String mesaj){
        uyariGoster();
        ikonHata.setVisibility(View.GONE);
        ikonBasarili.setVisibility(View.GONE);
        yuklemeBar.setVisibility(View.VISIBLE);
        durumMetni.setText(mesaj);
        yuklemeEkrani.setVisibility(View.VISIBLE);
    }


}