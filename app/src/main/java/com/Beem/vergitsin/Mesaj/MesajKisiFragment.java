package com.Beem.vergitsin.Mesaj;

import androidx.activity.OnBackPressedCallback;
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
import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MesajKisiFragment extends Fragment {

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
    private Long AcilmaZamani;
    private Long AcilmaZamaniadptr;
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
    private boolean isLoading = false;
    private CevapGeldi arayuzum;
    private MesajSilmeGuncellemeKisi arayuzSilme;


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
                AcilmaZamaniadptr=getArguments().getLong("acilmaZamani");
            } else if (kaynak.equals("mainactivity")) {
                PP=getArguments().getString("pp");
                istekatilanAd = getArguments().getString("istekatilanAdi");
                miktari = getArguments().getString("miktar").trim();
                aciklamasi = getArguments().getString("aciklama").trim();
                odemeTarihi = getArguments().getString("odemeTarihi").trim();
                sohbetID=getArguments().getString("sohbetId");
                AcilmaZamani=getArguments().getLong("acilmaZamani");
            }
        }
        // Geri tuşuna basıldığında fragmentten çık
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
                 mViewModel.DinleyiciKaldir();
            }
        });
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


        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MesajAdapterKisi(new ArrayList<>(), requireContext());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstVisibleItemPosition() == 0 && !isLoading) {
                    isLoading = true; // yükleniyor flag'i
                    if ("mainactivity".equals(kaynak)) {
                        mViewModel.EskiMesajlariYukle(sohbetID,ilkmsjSaati,AcilmaZamani);
                    }else{
                        mViewModel.EskiMesajlariYukle(sohbetIdAdptr,ilkmsjSaatiadptr,AcilmaZamaniadptr);
                    }
                }
            }
        });

        if ("mainactivity".equals(kaynak)) {
            arayuzum=new CevapGeldi() {
                @Override
                public void onCevapGeldi(String cvpverenid, String mesajID,String cvpverenad,String icerik) {
                    mViewModel.GelenCevabiKaydetme(sohbetID,mesajID,cvpverenid,cvpverenad,icerik);
                }
            };
            adapter.setListenercvp(arayuzum);
            arayuzSilme=new MesajSilmeGuncellemeKisi() {
                @Override
                public void onMesajGuncelleme(Mesaj mesaj) {
                    mViewModel.MesajGuncelleme(sohbetID,mesaj);
                }

                @Override
                public void onSonMesajGuncelleme(Mesaj mesaj) {
                    SonMesaj=mesaj.getMiktar()+" TL borç isteği";
                    SonMesajSaat=mesaj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetID,SonMesaj,SonMesajSaat);
                }

                @Override
                public void onSilmeislemi(Mesaj mesaj) {
                    mViewModel.MesajSilme(sohbetID,mesaj);
                }

                @Override
                public void onSonMesajSilme(Mesaj oncekiMesaj) {
                    if(oncekiMesaj!=null) {
                        SonMesaj = oncekiMesaj.getMiktar() + " TL borç isteği";
                        SonMesajSaat = oncekiMesaj.getZaman();
                        mViewModel.sonMsjDbKaydi(sohbetID, SonMesaj, SonMesajSaat);
                    }else{
                        SonMesaj =" ";
                        SonMesajSaat =System.currentTimeMillis();
                        mViewModel.sonMsjDbKaydi(sohbetID, SonMesaj, SonMesajSaat);
                    }
                }
            };
            adapter.setListenersil(arayuzSilme);
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

            mViewModel.MesajBorcistekleriDbCek(sohbetID,AcilmaZamani);

            mViewModel.tumMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                if (adapter == null) {
                    adapter = new MesajAdapterKisi(new ArrayList<>(), requireContext());
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
                if (mesajList != null && !mesajList.isEmpty()) {
                    ilkmsjSaati=mesajList.get(0).getZaman();
                    adapter.eskiMesajlariBasaEkle(mesajList);
                    isLoading = false;
                }
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
            mViewModel.guncellenen().observe(getViewLifecycleOwner(),mesaj->{
                adapter.mesajGuncelle(mesaj);
            });
            mViewModel.silinen().observe(getViewLifecycleOwner(),mesaj->{
                if (mesaj != null) {
                    adapter.MesajSilme(mesaj);
                }
            });

        } else {
            arayuzum=new CevapGeldi() {
                @Override
                public void onCevapGeldi(String cvpverenid, String mesajID,String cvpverenad,String icerik) {
                    mViewModel.GelenCevabiKaydetme(sohbetIdAdptr,mesajID,cvpverenid,cvpverenad,icerik);
                }
            };
            adapter.setListenercvp(arayuzum);
            arayuzSilme=new MesajSilmeGuncellemeKisi() {
                @Override
                public void onSilmeislemi(Mesaj mesaj) {
                    mViewModel.MesajSilme(sohbetIdAdptr,mesaj);
                }
                @Override
                public void onSonMesajSilme(Mesaj oncekiMesaj) {
                    if(oncekiMesaj!=null) {
                        SonMesajadptr = oncekiMesaj.getMiktar() + " Tl borç isteği";
                        SonMesajSaatadptr = oncekiMesaj.getZaman();
                        mViewModel.sonMsjDbKaydi(sohbetIdAdptr, SonMesajadptr, SonMesajSaatadptr);
                    }else{
                        SonMesajadptr =" ";
                        SonMesajSaatadptr =System.currentTimeMillis();
                        mViewModel.sonMsjDbKaydi(sohbetIdAdptr, SonMesajadptr, SonMesajSaatadptr);
                    }
                }

                @Override
                public void onMesajGuncelleme(Mesaj mesaj) {
                    mViewModel.MesajGuncelleme(sohbetIdAdptr,mesaj);
                }

                @Override
                public void onSonMesajGuncelleme(Mesaj mesaj) {
                    SonMesajadptr=mesaj.getMiktar()+" Tl borç isteği";
                    SonMesajSaatadptr=mesaj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetIdAdptr,SonMesajadptr,SonMesajSaatadptr);
                }
            };
            adapter.setListenersil(arayuzSilme);
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
            mViewModel.MesajBorcistekleriDbCek(sohbetIdAdptr,AcilmaZamaniadptr);

            mViewModel.tumMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
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
                if (mesajList != null && !mesajList.isEmpty()) {
                    ilkmsjSaatiadptr=mesajList.get(0).getZaman();
                    adapter.eskiMesajlariBasaEkle(mesajList);
                    isLoading = false;
                }
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
            mViewModel.guncellenen().observe(getViewLifecycleOwner(),mesaj->{
                adapter.mesajGuncelle(mesaj);
            });
            mViewModel.silinen().observe(getViewLifecycleOwner(),mesaj->{
                if (mesaj != null) {
                    adapter.MesajSilme(mesaj);
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
}


