package com.Beem.vergitsin.Mesaj;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
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
import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MesajKisiFragment extends Fragment implements CevapGeldi {

    private MesajViewModel mViewModel;
    private RecyclerView recyclerView;
    private MesajAdapterKisi adapter;
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

    private String miktari;
    private String aciklamasi;
    private String odemeTarihi;
    private String sohbetID;
    private String sohbetIdAdptr;
    private UyariMesaj uyarimesaji;
    private String istekatilanAd;
    private String sohbetedilenAd;
    private String sohbetEdilenPP;
    private String PP;
    private String SonMesaj;
    private Long SonMesajSaat;
    private String SonMesajadptr;
    private Long SonMesajSaatadptr;
    String kaynak;
    private boolean ilkMesajAlindi = false;
    private boolean ilkMesajAlindiadptr = false;
    private Long ilkmsjSaati;
    private Long ilkmsjSaatiadptr;


    public static MesajKisiFragment newInstance() {
        return new MesajKisiFragment();
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
                PP=getArguments().getString("pp");
                istekatilanAd = getArguments().getString("istekatilanAdi");
                miktari = getArguments().getString("miktar").trim();
                aciklamasi = getArguments().getString("aciklama").trim();
                odemeTarihi = getArguments().getString("odemeTarihi").trim();
                sohbetID=getArguments().getString("sohbetId");
            }
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View view =inflater.inflate(R.layout.fragment_mesaj, container, false);
        uyarimesaji=new UyariMesaj(requireContext(),false);
        kaynak = null;
        if (getArguments() != null) {
            kaynak = getArguments().getString("kaynak");
        }
        recyclerView=view.findViewById(R.id.mesajRecyclerView);
        kisiAdiText=view.findViewById(R.id.kisiAdiText);
        kisi_fotosu=view.findViewById(R.id.kisi_fotosu);
        kisiDurumText=view.findViewById(R.id.kisiDurumText);

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

        //adapter.setListenercvp(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MesajAdapterKisi(new ArrayList<>(), requireContext());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isLoading = false;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstVisibleItemPosition() == 0 && !isLoading) {
                    isLoading = true; // yükleniyor flag'i
                    if ("mainactivity".equals(kaynak)) {
                        mViewModel.EskiMesajlariYukle(sohbetID,ilkmsjSaati);
                    }else{
                        mViewModel.EskiMesajlariYukle(sohbetIdAdptr,ilkmsjSaatiadptr);
                    }

                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // Mesajlar yüklendikten sonra scroll durunca tekrar isLoading false yapılır.
                isLoading = false;
            }
        });

        if ("mainactivity".equals(kaynak)) {
            mViewModel.SohbetIDsindenAliciya(sohbetID);
            mViewModel.AliciID().observe(getViewLifecycleOwner(), id -> {
                if (id != null) {
                    mViewModel.CevrimiciSongorulmeDb(id);
                    mViewModel.durum().observe(getViewLifecycleOwner(), durum -> {
                        if (durum != null) {
                            if (durum.getCevirmici()) {
                                kisiDurumText.setText("Çevrimiçi");
                            } else {
                                kisiDurumText.setText("Son görülme: " + durum.getSonGorulme());
                            }
                        }
                    });
                }
            });
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

            mViewModel.tumMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                if (adapter == null) {
                    adapter = new MesajAdapterKisi(new ArrayList<>(), requireContext());
                   // adapter.setListenercvp(this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(adapter);
                }
                if(!ilkMesajAlindi){
                    ilkmsjSaati=mesajList.get(0).getZaman();
                    ilkMesajAlindi=true;
                }
                adapter.guncelleMesajListesi(mesajList);
                istekEditTextViewLayout.setVisibility(View.VISIBLE);
                istekTextViewLayout.setVisibility(View.GONE);
                SonMesaj=mesajList.get(mesajList.size()-1).getMiktar()+" Tl borç isteği";
                SonMesajSaat=mesajList.get(mesajList.size()-1).getZaman();
                mViewModel.sonMsjDbKaydi(sohbetID,SonMesaj,SonMesajSaat);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            });
            mViewModel.eskiMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                adapter.eskiMesajlariBasaEkle(mesajList);
            });

            mViewModel.eklenen().observe(getViewLifecycleOwner(), mesaj -> {
                if (mesaj != null) {
                    adapter.mesajEkle(mesaj);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    SonMesaj=mesaj.getMiktar()+" TL borç isteği";
                    SonMesajSaat=mesaj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetID,SonMesaj,SonMesajSaat);
                }
            });

        } else {
            mViewModel.SohbetIDsindenAliciya(sohbetIdAdptr);

            mViewModel.AliciID().observe(getViewLifecycleOwner(), id -> {
                if (id != null) {
                    mViewModel.CevrimiciSongorulmeDb(id);
                }
            });

            mViewModel.durum().observe(getViewLifecycleOwner(), durum -> {
                if (durum != null) {
                    if (durum.getCevirmici()) {
                        kisiDurumText.setText("Çevrimiçi");
                    } else {
                        kisiDurumText.setText("Son görülme: " + durum.getSonGorulme());
                    }
                }
            });
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
                if (adapter == null) {
                    adapter = new MesajAdapterKisi(new ArrayList<>(), requireContext());
                 //   adapter.setListenercvp(this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(adapter);
                }
                if(!ilkMesajAlindiadptr){
                    ilkmsjSaatiadptr=mesajList.get(0).getZaman();
                    ilkMesajAlindiadptr=true;
                }
                adapter.guncelleMesajListesi(mesajList);
                SonMesajadptr=mesajList.get(mesajList.size()-1).getMiktar()+" Tl borç isteği";
                SonMesajSaatadptr=mesajList.get(mesajList.size()-1).getZaman();
                mViewModel.sonMsjDbKaydi(sohbetIdAdptr,SonMesajadptr,SonMesajSaatadptr);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            });
            mViewModel.eskiMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                adapter.eskiMesajlariBasaEkle(mesajList);
            });

            mViewModel.eklenen().observe(getViewLifecycleOwner(), mesaj -> {
                if (mesaj != null) {
                    adapter.mesajEkle(mesaj);
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    SonMesajadptr=mesaj.getMiktar()+" Tl borç isteği";
                    SonMesajSaatadptr=mesaj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetIdAdptr,SonMesajadptr,SonMesajSaatadptr);
                }
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
                    mViewModel.AliciID().observe(getViewLifecycleOwner(),id->{
                            mViewModel.BorcIstekleriDb(uyarimesaji,MainActivity.kullanicistatic.getKullaniciId(),id,miktar,aciklama,tarih,MainActivity.kullanicistatic.getKullaniciAdi(),sohbetIdAdptr,mesajZamani);
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
            return null;
        }
    }
    @Override
    public void onCevapGeldi(String cvp,String idmsj) {
        mViewModel.GelenCevabiKaydetme(sohbetIdAdptr,idmsj,cvp);
    }
}
