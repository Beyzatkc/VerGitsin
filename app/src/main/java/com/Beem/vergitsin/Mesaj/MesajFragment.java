package com.Beem.vergitsin.Mesaj;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.Beem.vergitsin.Sohbet.SohbetFragment;
import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MesajFragment extends Fragment {

    private MesajViewModel mViewModel;
    private RecyclerView recyclerView;
    private MesajAdapter adapter;
    private TextView kisiAdiText;
    private ImageView kisi_fotosu;
    private TextView kisiDurumText;
    private LinearLayout istekEditTextViewLayout;
    private EditText miktaredit;
    private EditText aciklamaedit;
    private ImageButton gonderButton2edit;
    private EditText odemeTarihiedit;

    private LinearLayout istekTextViewLayout;
    private TextView miktartext;
    private TextView aciklamatext;
    private ImageButton gonderButtontext;
    private TextView odemeTarihitext;

    private String istekAtilanId;
    private String miktari;
    private String aciklamasi;
    private String odemeTarihi;
    private String sohbetID;
    private Long mesajinZamani;
    private String sohbetIdAdptr;
    private UyariMesaj uyarimesaji;
    private String istekatilanAd;
    private String sohbetedilenAd;
    private String sohbetEdilenPP;
    private String PP;

    public static MesajFragment newInstance() {
        return new MesajFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MesajViewModel.class);

        if (getArguments() != null) {
            String kaynak = getArguments().getString("kaynak", "");

            if (kaynak.equals("SohbetAdapter")) {
                sohbetIdAdptr= getArguments().getString("sohbetId");
                sohbetedilenAd=getArguments().getString("sohbetedilenAd");
                sohbetEdilenPP=getArguments().getString("sohbetEdilenPP");
            } else if (kaynak.equals("mainactivity")) {
                istekAtilanId=getArguments().getString("istekatilanID");
                PP=getArguments().getString("pp");
                istekatilanAd = getArguments().getString("istekatilanAdi");
                miktari = getArguments().getString("miktar").trim();
                aciklamasi = getArguments().getString("aciklama").trim();
                odemeTarihi = getArguments().getString("odemeTarihi").trim();
                sohbetID=getArguments().getString("sohbetId");
                mesajinZamani=getArguments().getLong("mesajinatildigizaman");
            }
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View view =inflater.inflate(R.layout.fragment_mesaj, container, false);
        uyarimesaji=new UyariMesaj(requireContext(),false);
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.konteynir, new SohbetFragment())
                                .commit();
                    }
                }
        );
        String kaynak = null;
        if (getArguments() != null) {
            kaynak = getArguments().getString("kaynak");
        }
        recyclerView=view.findViewById(R.id.mesajRecyclerView);
        kisiAdiText=view.findViewById(R.id.kisiAdiText);
        kisi_fotosu=view.findViewById(R.id.kisi_fotosu);
        kisiDurumText=view.findViewById(R.id.kisiDurumText);//DAHA YAPMADIM

        istekEditTextViewLayout=view.findViewById(R.id.istekEditTextViewLayout);
        miktaredit=view.findViewById(R.id.miktaredit);
        aciklamaedit=view.findViewById(R.id.aciklamaedit);
        gonderButton2edit=view.findViewById(R.id.gonderButton2edit);
        odemeTarihiedit=view.findViewById(R.id.odemeTarihiedit);

        istekTextViewLayout=view.findViewById(R.id.istekTextViewLayout);
        miktartext=view.findViewById(R.id.miktar);
        aciklamatext=view.findViewById(R.id.aciklama);
        gonderButtontext=view.findViewById(R.id.gonderButton2);
        odemeTarihitext=view.findViewById(R.id.odemeTarihi);

        if ("mainactivity".equals(kaynak)) {
            istekEditTextViewLayout.setVisibility(View.GONE);
            istekTextViewLayout.setVisibility(View.VISIBLE);
            miktartext.setText(miktari);
            aciklamatext.setText(aciklamasi);
            odemeTarihitext.setText(odemeTarihi);

               kisiAdiText.setText(istekatilanAd);
                   if (PP != null) {
                       int resId = requireContext().getResources().getIdentifier(
                               PP, "drawable", requireContext().getPackageName());
                       kisi_fotosu.setImageResource(resId);
                   } else {
                       int resId = requireContext().getResources().getIdentifier(
                               "user", "drawable", requireContext().getPackageName());
                       kisi_fotosu.setImageResource(resId);
                   }
            mViewModel.MesajBorcistekleriDbCek(sohbetID);

            mViewModel.tumMesajlar().observe(getViewLifecycleOwner(), mesajList ->{
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                adapter = new MesajAdapter(mesajList,requireContext());
                recyclerView.setAdapter(adapter);
            });
            //BUTONA BASMAYI KALDIRDIM
                Timestamp tarih=stringToTimestamp(odemeTarihi);
                Mesaj mesaj=new Mesaj(MainActivity.kullanicistatic.getKullaniciId(),istekAtilanId,aciklamasi,miktari,tarih,mesajinZamani,false);
                adapter.mesajEkle(mesaj);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                istekEditTextViewLayout.setVisibility(View.VISIBLE);
                istekTextViewLayout.setVisibility(View.GONE);
        } else {
           kisiAdiText.setText(sohbetedilenAd);
            if (sohbetEdilenPP != null) {
                int resId = requireContext().getResources().getIdentifier(
                        sohbetEdilenPP, "drawable", requireContext().getPackageName());
                kisi_fotosu.setImageResource(resId);
            } else {
                int resId = requireContext().getResources().getIdentifier(
                        "user", "drawable", requireContext().getPackageName());
                kisi_fotosu.setImageResource(resId);
            }
            mViewModel.MesajBorcistekleriDbCek(sohbetIdAdptr);

            mViewModel.tumMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        adapter = new MesajAdapter(mesajList, requireContext());
                        recyclerView.setAdapter(adapter);
                    });

            istekEditTextViewLayout.setVisibility(View.VISIBLE);
            istekTextViewLayout.setVisibility(View.GONE);
            gonderButton2edit.setOnClickListener(b->{
                String miktar=miktaredit.getText().toString().trim();
                String aciklama=aciklamaedit.getText().toString().trim();
                String odemetarihi=odemeTarihiedit.getText().toString().trim();
                Long mesajZamani=System.currentTimeMillis();
                String regex = "^\\d{2}/\\d{2}/\\d{4}$";
                if (!odemetarihi.matches(regex)) {
                    Toast.makeText(getContext(), "Lütfen tarihi gün/ay/yıl formatında girin (örn: 22/07/2025)", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Timestamp tarih=stringToTimestamp(odemetarihi);
                    mViewModel.SohbetIDsindenAliciya(sohbetIdAdptr);
                    mViewModel.AliciID().observe(getViewLifecycleOwner(),id->{
                        Mesaj mesaj=new Mesaj(MainActivity.kullanicistatic.getKullaniciId(),id,aciklama,miktar,tarih,mesajZamani,false);
                            adapter.mesajEkle(mesaj);
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            mViewModel.BorcIstekleriDb(uyarimesaji,MainActivity.kullanicistatic.getKullaniciId(),id,miktar,aciklama,tarih,MainActivity.kullanicistatic.getKullaniciAdi(),sohbetIdAdptr,System.currentTimeMillis());
                            miktaredit.setText("");
                            aciklamaedit.setText("");
                            odemeTarihiedit.setText("");
                    });
                }
            });
        }
       return view;
    }
    public Timestamp stringToTimestamp(String odemeTarihiStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(odemeTarihiStr);
            return new Timestamp(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // veya default bir Timestamp dönebilirsin
        }
    }
}
