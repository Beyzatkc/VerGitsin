package com.Beem.vergitsin.Kullanici;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.Beem.vergitsin.UyariMesaj;
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
    private ImageView eyeIconkayit;
    private ImageView eyeIcongiris;
    private SharedPreferencesK shared;
    private UyariMesaj uyari;

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
    private void fadeAndScaleIn(View view) {
        view.setAlpha(1f);         // Tam görünür
        view.setScaleX(1f);        // Yatay orijinal boyut
        view.setScaleY(1f);        // Dikey orijinal boyut
        view.setVisibility(View.VISIBLE); // Görünür yap
    }


    public void Kayit(BottomSheetDialog bottomSheetDialog){
        btnKayitOl.setOnClickListener(a -> {
            uyari.YuklemeDurum("Kayıt yapılıyor...");
            String kAdi = kayitKullaniciAdi.getText().toString().trim();
            String email = KayitEmail.getText().toString().trim();
            String sifre = KayitSifre.getText().toString().trim();

            if (kAdi.isEmpty() || email.isEmpty() || sifre.isEmpty()) {
                uyari.BasarisizDurum("Lütfen tüm alanları doldurun",1000);
                return;
            }
            mViewModel.Kadi_BenzersizMi(kAdi);

            Observe.observeOnce(mViewModel.kAdi(), getViewLifecycleOwner(), isim -> {
                if (Boolean.FALSE.equals(isim)) {
                    uyari.BasarisizDurum("Bu kullanıcı adı zaten alınmış",1000);
                } else {
                    mViewModel.KayitOl(sifre,email,kAdi);
                    Observe.observeOnce(mViewModel.id(), getViewLifecycleOwner(), id -> {
                        if(id!=null){
                            Kullanici kullanici=new Kullanici(id,kAdi,email);
                            MainActivity.kullanicistatic=kullanici;
                            shared.girisYap(email, kAdi, id);
                            uyari.BasariliDurum("Kayıt başarılı.",1000);
                            bottomSheetDialog.dismiss();
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            intent.putExtra("fromFragment", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else{
                            uyari.BasarisizDurum("Bu email zaten kayıtlı",1000);
                        }
                    });
                }
            });
        });
    }

    public void GirisYap(BottomSheetDialog bottomSheetDialog){
        btngiris.setOnClickListener(b -> {
            uyari.YuklemeDurum("Giriş yapılıyor...");
            String kAdi = GirisKullaniciAdi.getText().toString().trim();
            String sifre = GirisSifre.getText().toString().trim();

            if(kAdi.isEmpty() || sifre.isEmpty()){
               uyari.BasarisizDurum("Lütfen tüm alanları doldurun",1000);
                return;
            }

            mViewModel.EmaileUlasma(kAdi);
            Observe.observeOnce(mViewModel.email(), getViewLifecycleOwner(), email -> {
                if(email != null){
                    mViewModel.GirisYap(email, sifre);
                    Observe.observeOnce(mViewModel.girisbasarili(), getViewLifecycleOwner(), degisken -> {
                        if (Boolean.FALSE.equals(degisken)) {
                            uyari.BasarisizDurum("Şifre hatalı",1000);
                        } else {
                            Observe.observeOnce(mViewModel.id(), getViewLifecycleOwner(), id -> {
                                shared.girisYap(email, kAdi,id);
                                Kullanici kullanici = new Kullanici(id, kAdi, email);
                                MainActivity.kullanicistatic = kullanici;
                                uyari.BasariliDurum("Giriş başarılı.",1000);
                                bottomSheetDialog.dismiss();
                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                intent.putExtra("fromFragment", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                        }
                    });
                } else {
                    uyari.BasarisizDurum("Kayıtlı kullanıcı bulunamadı",1000);
                }
            });
        });
    }
    public void sifreGosterGizle(ImageView eyeicon,EditText sifre){
        final boolean[] sifreGorunur = {false};
        eyeicon.setOnClickListener(v -> {
            if (sifreGorunur[0]) {
                sifre.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeicon.setImageResource(R.drawable.kapali_goz);
                sifreGorunur[0] = false;
            } else {
                sifre.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeicon.setImageResource(R.drawable.acik_goz);
                sifreGorunur[0] = true;
            }
            sifre.setSelection(sifre.length()); // İmleç sona gelsin
        });
    }
    private void showBottomSheetWithDelay(BottomSheetDialog dialog, long delayMillis) {
        new android.os.Handler().postDelayed(dialog::show, delayMillis);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kullanici, container, false);
        shared = new SharedPreferencesK(requireContext());
        uyari=new UyariMesaj(requireContext(),false);

        buttonkayit=view.findViewById(R.id.buttonkayit);
        buttonGirisYap=view.findViewById(R.id.buttonGirisYap);

        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_kayit, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setOnDismissListener(dialog -> {
            fadeAndScaleIn(buttonkayit);
            fadeAndScaleIn(buttonGirisYap);
        });
        btnKayitOl = bottomSheetView.findViewById(R.id.btnkayit);
        KayitEmail = bottomSheetView.findViewById(R.id.kayitEmail);
        KayitSifre = bottomSheetView.findViewById(R.id.kayitPassword);
        eyeIconkayit=bottomSheetView.findViewById(R.id.eyeIconkayit);
        sifreGosterGizle(eyeIconkayit,KayitSifre);
        kayitKullaniciAdi = bottomSheetView.findViewById(R.id.kayitUsername);

        View bottomSheetView2 = getLayoutInflater().inflate(R.layout.bottom_sheet_giris, null);
        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(requireContext());
        bottomSheetDialog2.setContentView(bottomSheetView2);
        bottomSheetDialog2.setOnDismissListener(dialog -> {
            fadeAndScaleIn(buttonkayit);
            fadeAndScaleIn(buttonGirisYap);
        });
        btngiris = bottomSheetView2.findViewById(R.id.btngiris);
        SifremiUnuttum=bottomSheetView2.findViewById(R.id.SifremiUnuttum);
        GirisSifre = bottomSheetView2.findViewById(R.id.girisPassword);
        eyeIcongiris=bottomSheetView2.findViewById(R.id.eyeIcongiris);
        GirisKullaniciAdi = bottomSheetView2.findViewById(R.id.girisUsername);
        sifreGosterGizle(eyeIcongiris,GirisSifre);

        buttonkayit.setOnClickListener(b -> {
            fadeAndScaleOut(buttonkayit);
            fadeAndScaleOut(buttonGirisYap);
            showBottomSheetWithDelay(bottomSheetDialog, 2000);
        });

        buttonGirisYap.setOnClickListener(c->{
            fadeAndScaleOut(buttonkayit);
            fadeAndScaleOut(buttonGirisYap);
            showBottomSheetWithDelay(bottomSheetDialog2, 2000);
        });

      Kayit(bottomSheetDialog);
      GirisYap(bottomSheetDialog2);

        SifremiUnuttum.setOnClickListener(b -> {
            bottomSheetDialog2.getBehavior().setState(BottomSheetBehavior.STATE_HIDDEN);
            View bottomSheetViewsif = getLayoutInflater().inflate(R.layout.sifremi_unuttum, null);
            bottomSheetDialogsif = new BottomSheetDialog(requireContext());
            bottomSheetDialogsif.setContentView(bottomSheetViewsif);

            resetPasswordButton = bottomSheetViewsif.findViewById(R.id.resetPasswordButton);
            emailEditText = bottomSheetViewsif.findViewById(R.id.emailEditText);
            girisMetodu = bottomSheetViewsif.findViewById(R.id.girisMetodu);

            resetPasswordButton.setOnClickListener(b2 -> {
                String mail = emailEditText.getText().toString().trim();
                mViewModel.SifreSifirla(mail);
                Observe.observeOnce(mViewModel.getSifreSifirlandi(), getViewLifecycleOwner(), basarili -> {
                    if (Boolean.TRUE.equals(basarili)) {
                        Toast.makeText(getContext(), "Şifre sıfırlama bağlantısı e-posta adresinize gönderildi", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Eposta hatalı", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            girisMetodu.setOnClickListener(b2 -> {
                bottomSheetDialogsif.dismiss();
                bottomSheetDialog2.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            });

            bottomSheetDialogsif.show();
        });

        return view;
    }

}