package com.Beem.vergitsin.Kullanici;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class KullaniciFragment extends Fragment {
    private Button buttonkayit;
    private Button buttonGirisYap;
    private  Button btnKayitOl;
    private Button btngiris;
    private EditText KayitEmail;
    private EditText KayitSifre;
    private EditText GirisKullaniciAdi;
    private EditText GirisSifre;
    private EditText kayitKullaniciAdi;
    private TextView SifremiUnuttum;
    private Button resetPasswordButton;
    private EditText emailEditText;
    private BottomSheetDialog bottomSheetDialogsif;
    private TextView girisMetodu;

    private KullaniciViewModel mViewModel;

    public static KullaniciFragment newInstance() {
        return new KullaniciFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(KullaniciViewModel.class);
    }

    private void fadeAndScaleOut(View view) {
        view.animate()
                .alpha(0f)          // saydamlaştır
                .scaleX(0.7f)       // yatay küçült
                .scaleY(0.7f)       // dikey küçült
                .setDuration(2000)   // animasyon süresi (ms)
                .withEndAction(() -> view.setVisibility(View.GONE)) // görünmez yap
                .start();
    }

    public void Kayit(BottomSheetDialog bottomSheetDialog){
        btnKayitOl.setOnClickListener(a -> {
            String kAdi = kayitKullaniciAdi.getText().toString().trim();
            String email = KayitEmail.getText().toString().trim();
            String sifre = KayitSifre.getText().toString().trim();

            if (kAdi.isEmpty() || email.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }
            mViewModel.Kadi_BenzersizMi(kAdi);

            Observe.observeOnce(mViewModel.kAdi(), getViewLifecycleOwner(), isim -> {
                if (Boolean.FALSE.equals(isim)) {
                    Toast.makeText(getContext(), "Bu kullanıcı adı zaten alınmış", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                } else {
                    Kullanici kullanici=new Kullanici(null,kAdi,email,sifre);
                    MainActivity.kullanici=kullanici;
                    mViewModel.KayitOl(kullanici);
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        });

    }

    public void GirisYap(BottomSheetDialog bottomSheetDialog){
        btngiris.setOnClickListener(b->{
            String kAdi = GirisKullaniciAdi.getText().toString().trim();
            String sifre = GirisSifre.getText().toString().trim();
            mViewModel.EmaileUlasma(kAdi);
            Observe.observeOnce(mViewModel.email(), getViewLifecycleOwner(), email -> {
                mViewModel.GirisYap(email,sifre);
                Observe.observeOnce(mViewModel.girisbasarili(), getViewLifecycleOwner(), degisken -> {
                    if(Boolean.FALSE.equals(degisken)){
                        Toast.makeText(getContext(), "Giriş başarısız", Toast.LENGTH_SHORT).show();
                    }else{
                        Observe.observeOnce(mViewModel.id(), getViewLifecycleOwner(), id -> {
                            Kullanici kullanici = new Kullanici(id, kAdi, email, sifre);
                            MainActivity.kullanici=kullanici;
                            bottomSheetDialog.dismiss();
                            // MainActivity'ye yönlendir
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        });
                    }
                });
            });
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kullanici, container, false);

        buttonkayit=view.findViewById(R.id.buttonkayit);
        buttonGirisYap=view.findViewById(R.id.buttonGirisYap);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_kayit, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        btnKayitOl = bottomSheetView.findViewById(R.id.btnkayit);

        KayitEmail = bottomSheetView.findViewById(R.id.kayitEmail);
        KayitSifre = bottomSheetView.findViewById(R.id.kayitPassword);
        kayitKullaniciAdi = bottomSheetView.findViewById(R.id.kayitUsername);

        View bottomSheetView2 = getLayoutInflater().inflate(R.layout.bottom_sheet_giris, null);
        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(requireContext());
        bottomSheetDialog2.setContentView(bottomSheetView2);
        btngiris = bottomSheetView2.findViewById(R.id.btngiris);
        SifremiUnuttum=bottomSheetView2.findViewById(R.id.SifremiUnuttum);
        GirisSifre = bottomSheetView2.findViewById(R.id.girisPassword);
        GirisKullaniciAdi = bottomSheetView2.findViewById(R.id.girisUsername);

        buttonkayit.setOnClickListener(b->{
            fadeAndScaleOut(btnKayitOl);
            fadeAndScaleOut(btngiris);
            bottomSheetDialog.show();
        });
        buttonGirisYap.setOnClickListener(c->{
            fadeAndScaleOut(btnKayitOl);
            fadeAndScaleOut(btngiris);
            bottomSheetDialog2.show();
        });

      Kayit(bottomSheetDialog);
      GirisYap(bottomSheetDialog2);

        SifremiUnuttum.setOnClickListener(b->{
            bottomSheetDialog2.getBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
            View bottomSheetViewsif = getLayoutInflater().inflate(R.layout.sifremi_unuttum, null);
            bottomSheetDialogsif = new BottomSheetDialog(requireContext());
            bottomSheetDialogsif.setContentView(bottomSheetViewsif);
            resetPasswordButton=bottomSheetViewsif.findViewById(R.id.resetPasswordButton);
            emailEditText=bottomSheetDialogsif.findViewById(R.id.emailEditText);
            girisMetodu=bottomSheetDialogsif.findViewById(R.id.girisMetodu);
            bottomSheetDialogsif.show();
        });
        resetPasswordButton.setOnClickListener(b->{
           String mail= emailEditText.getText().toString().trim();
           mViewModel.SifreSifirla(mail);
            Observe.observeOnce(mViewModel.getSifreSifirlandi(), getViewLifecycleOwner(), basarili -> {
                if (Boolean.TRUE.equals(basarili)) {
                    Toast.makeText(getContext(), "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Eposta hatalı", Toast.LENGTH_SHORT).show();
                }
            });
        });
        girisMetodu.setOnClickListener(b->{
            bottomSheetDialogsif.dismiss();
            bottomSheetDialog2.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        return view;
    }



}