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

import com.Beem.vergitsin.Kullanici.Observe;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.R;
import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MesajGrupFragment extends Fragment{
    String kaynak;
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
    private MesajGrupViewModel mViewModel;
    private ImageView grup_fotosu;
    private TextView grupAdi;
    private TextView kackisicevrimici;
    private RecyclerView mesajGrupRecyclerView;
    private MesajAdapterGrup adapter;

    private LinearLayout istekEditTextViewLayout;
    private TextView gonderenadi;
    private EditText miktaredit;
    private EditText aciklamaedit;
    private ImageButton gonderButton2edit;
    private EditText odemeTarihiedit;

    private LinearLayout istekTextViewLayout;
    private TextView gonderenadiview;
    private TextView miktartext;
    private TextView aciklamatext;
    private ImageButton gonderButtontext;
    private TextView odemeTarihitext;

    private String SonMesaj;
    private Long SonMesajSaat;
    private String SonMesajadptr;
    private Long SonMesajSaatadptr;

    private boolean ilkMesajAlindi = false;
    private boolean ilkMesajAlindiadptr = false;
    private Long ilkmsjSaati;
    private Long ilkmsjSaatiadptr;
    private boolean isLoading = false;
    private CevapGeldiGrup arayuzum;
    private MesajSilmeGuncellemeGrup arayuzSilme;

    public static MesajGrupFragment newInstance() {
        return new MesajGrupFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MesajGrupViewModel.class);

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
        View view =inflater.inflate(R.layout.fragment_mesaj_grup, container, false);
        uyarimesaji=new UyariMesaj(requireContext(),false);
         kaynak = null;
        if (getArguments() != null) {
            kaynak = getArguments().getString("kaynak");
        }
        mesajGrupRecyclerView=view.findViewById(R.id.mesajGrupRecyclerView);
        grupAdi=view.findViewById(R.id.grupAdi);
        grup_fotosu=view.findViewById(R.id.grup_fotosu);
        kackisicevrimici=view.findViewById(R.id.kackisicevrimici);

        istekEditTextViewLayout=view.findViewById(R.id.istekEditTextViewLayout);
        gonderenadi=view.findViewById(R.id.gonderenadi);
        miktaredit=view.findViewById(R.id.miktaredit);
        aciklamaedit=view.findViewById(R.id.aciklamaedit);
        gonderButton2edit=view.findViewById(R.id.gonderButton2edit);
        odemeTarihiedit=view.findViewById(R.id.odemeTarihiedit);

        istekTextViewLayout=view.findViewById(R.id.istekTextViewLayout);
        gonderenadiview=view.findViewById(R.id.gonderenadiview);
        miktartext=view.findViewById(R.id.miktar);
        aciklamatext=view.findViewById(R.id.aciklama);
        gonderButtontext=view.findViewById(R.id.gonderButton2);
        odemeTarihitext=view.findViewById(R.id.odemeTarihi);

        ArrayList<Mesaj> bosListe = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        mesajGrupRecyclerView.setLayoutManager(layoutManager);
        adapter = new MesajAdapterGrup(bosListe, requireContext());
        mesajGrupRecyclerView.setAdapter(adapter);

        mesajGrupRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        });

        if ("mainactivity".equals(kaynak)) {
            arayuzum=new CevapGeldiGrup() {
                @Override
                public void onCevapGeldiGrup(String cvpverenid, String mesajID,String cvpverenad,String icerik) {
                    mViewModel.GelenCevabiKaydetme(sohbetID,mesajID,cvpverenid,cvpverenad,icerik);
                }
            };
            adapter.setListenercvp(arayuzum);
            arayuzSilme=new MesajSilmeGuncellemeGrup() {
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

                @Override
                public void onMesajGuncelleme(Mesaj mesaj) {
                    mViewModel.MesajGuncelleme(sohbetID,mesaj);
                }
            };
            adapter.setListenersil(arayuzSilme);
            mViewModel.KacKisiCevrimiciGrup(sohbetID);
            mViewModel.kackisicevrimici().observe(getViewLifecycleOwner(),kisisayisi-> {
                kackisicevrimici.setText(kisisayisi.toString());
            });
            istekEditTextViewLayout.setVisibility(View.GONE);
            istekTextViewLayout.setVisibility(View.VISIBLE);
            miktartext.setText(miktari);
            aciklamatext.setText(aciklamasi);
            odemeTarihitext.setText(odemeTarihi);

            grupAdi.setText(istekatilanAd);
            if (PP != null) {
                int resId = requireContext().getResources().getIdentifier(
                        PP, "drawable", requireContext().getPackageName());
                grup_fotosu.setImageResource(resId);
            } else {
                int resId = requireContext().getResources().getIdentifier(
                        "user", "drawable", requireContext().getPackageName());
                grup_fotosu.setImageResource(resId);
            }
            gonderenadi.setText(MainActivity.kullanicistatic.getKullaniciAdi());
            mViewModel.MesajBorcistekleriDbCek(sohbetID);

            mViewModel.eklenen().observe(getViewLifecycleOwner(), mesaj -> {
                if (mesaj != null) {
                    mViewModel.IddenGonderenAdaUlasmaTekKisi(mesaj);
                }
            });
            mViewModel.guncellenen().observe(getViewLifecycleOwner(),mesaj->{
                adapter.mesajGuncelle(mesaj);
            });
            mViewModel.tamamlandimsj().observe(getViewLifecycleOwner(), msj -> {
                if (msj != null) {
                    adapter.mesajEkle(msj);
                    mesajGrupRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    SonMesaj=msj.getIstekAtanAdi()+" "+msj.getMiktar()+" TL borç isteği";
                    SonMesajSaat=msj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetID,SonMesaj,SonMesajSaat);
                }
            });
            Observe.observeOnce(mViewModel.tumMesajlar(), getViewLifecycleOwner(), mesajList -> {
                mViewModel.IddenGonderenAdaUlasma(mesajList,"mesajlar");
            });
            mViewModel.silinen().observe(getViewLifecycleOwner(),mesaj->{
                if (mesaj != null) {
                    adapter.MesajSilme(mesaj);
                }
            });
            mViewModel.tamamlandi().observe(getViewLifecycleOwner(), tamamlandiMi -> {
                if (Boolean.TRUE.equals(tamamlandiMi)) {
                    ArrayList<Mesaj> mesajList = mViewModel.tumMesajlar().getValue();
                    if (mesajList != null) {
                        if(!ilkMesajAlindi){
                            ilkmsjSaati=mesajList.get(0).getZaman();
                            ilkMesajAlindi=true;
                        }
                        adapter.guncelleMesajListesi(mesajList);
                        istekEditTextViewLayout.setVisibility(View.VISIBLE);
                        istekTextViewLayout.setVisibility(View.GONE);
                        SonMesaj=mesajList.get(mesajList.size()-1).getIstekAtanAdi()+" "+mesajList.get(mesajList.size()-1).getMiktar()+" Tl borç isteği";
                        SonMesajSaat=mesajList.get(mesajList.size()-1).getZaman();
                        mViewModel.sonMsjDbKaydi(sohbetID,SonMesaj,SonMesajSaat);
                        mesajGrupRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
            });

            mViewModel.eskiMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                mViewModel.setGeciciEskiMesajListesi(mesajList);
                  mViewModel.IddenGonderenAdaUlasma(mesajList,"eskimsjlar");
            });

            mViewModel.tamamlandieski().observe(getViewLifecycleOwner(), tamamlandiMi -> {
                if (Boolean.TRUE.equals(tamamlandiMi)) {
                    ArrayList<Mesaj> mesajList = mViewModel.getGeciciEskiMesajListesi();
                    if (mesajList != null && !mesajList.isEmpty()) {
                        ilkmsjSaati=mesajList.get(0).getZaman();
                        adapter.eskiMesajlariBasaEkle(mesajList);
                        isLoading = false;
                    }
                }
            });

        } else {
            arayuzum=new CevapGeldiGrup(){
                @Override
                public void onCevapGeldiGrup(String cvpverenid, String mesajID,String cvpverenad,String icerik) {
                    mViewModel.GelenCevabiKaydetme(sohbetIdAdptr,mesajID,cvpverenid,cvpverenad,icerik);
                }
            };
            adapter.setListenercvp(arayuzum);
            arayuzSilme=new MesajSilmeGuncellemeGrup() {
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
                        mViewModel.sonMsjDbKaydi(sohbetIdAdptr, SonMesaj, SonMesajSaat);
                    }
                }

                @Override
                public void onMesajGuncelleme(Mesaj mesaj) {
                    mViewModel.MesajGuncelleme(sohbetIdAdptr,mesaj);
                }
            };
            adapter.setListenersil(arayuzSilme);
            mViewModel.KacKisiCevrimiciGrup(sohbetIdAdptr);
            mViewModel.kackisicevrimici().observe(getViewLifecycleOwner(),kisisayisi-> {
                kackisicevrimici.setText(kisisayisi.toString());
            });
            grupAdi.setText(sohbetedilenAd);
            if (sohbetEdilenPP != null) {
                int resId = requireContext().getResources().getIdentifier(
                        sohbetEdilenPP, "drawable", requireContext().getPackageName());
                grup_fotosu.setImageResource(resId);
            } else {
                int resId = requireContext().getResources().getIdentifier(
                        "user", "drawable", requireContext().getPackageName());
                grup_fotosu.setImageResource(resId);
            }
            gonderenadiview.setText(MainActivity.kullanicistatic.getKullaniciAdi());
            mViewModel.MesajBorcistekleriDbCek(sohbetIdAdptr);

            mViewModel.eklenen().observe(getViewLifecycleOwner(), mesaj -> {
                if (mesaj != null) {
                    mViewModel.IddenGonderenAdaUlasmaTekKisi(mesaj);
                }
            });
            mViewModel.tamamlandimsj().observe(getViewLifecycleOwner(), msj -> {
                if (msj != null) {
                    adapter.mesajEkle(msj);
                    mesajGrupRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    SonMesajadptr=msj.getIstekAtanAdi()+" "+msj.getMiktar()+" TL borç isteği";
                    SonMesajSaatadptr=msj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetIdAdptr,SonMesajadptr,SonMesajSaatadptr);
                }
            });
            mViewModel.guncellenen().observe(getViewLifecycleOwner(),mesaj->{
                adapter.mesajGuncelle(mesaj);
            });
            Observe.observeOnce(mViewModel.tumMesajlar(), getViewLifecycleOwner(), mesajList -> {
                mViewModel.IddenGonderenAdaUlasma(mesajList,"mesajlar");
            });
            mViewModel.silinen().observe(getViewLifecycleOwner(),mesaj->{
                if (mesaj != null) {
                    adapter.MesajSilme(mesaj);
                }
            });
            mViewModel.tamamlandi().observe(getViewLifecycleOwner(), tamamlandiMi -> {
                if (Boolean.TRUE.equals(tamamlandiMi)) {
                    ArrayList<Mesaj> mesajList = mViewModel.tumMesajlar().getValue();
                    if (mesajList != null) {
                        if(!ilkMesajAlindiadptr){
                            ilkmsjSaatiadptr=mesajList.get(0).getZaman();
                            ilkMesajAlindiadptr=true;
                        }
                        adapter.guncelleMesajListesi(mesajList);
                        SonMesajadptr=mesajList.get(mesajList.size()-1).getIstekAtanAdi()+" "+mesajList.get(mesajList.size()-1).getMiktar()+" Tl borç isteği";
                        SonMesajSaatadptr=mesajList.get(mesajList.size()-1).getZaman();
                        mViewModel.sonMsjDbKaydi(sohbetIdAdptr,SonMesajadptr,SonMesajSaatadptr);
                        mesajGrupRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
            });
            mViewModel.eskiMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                mViewModel.setGeciciEskiMesajListesi(mesajList);
                mViewModel.IddenGonderenAdaUlasma(mesajList,"eskimsjlar");
            });

            mViewModel.tamamlandieski().observe(getViewLifecycleOwner(), tamamlandiMi -> {
                if (Boolean.TRUE.equals(tamamlandiMi)) {
                    ArrayList<Mesaj> mesajList = mViewModel.getGeciciEskiMesajListesi();
                    if (mesajList != null && !mesajList.isEmpty()) {
                        ilkmsjSaatiadptr=mesajList.get(0).getZaman();
                        adapter.eskiMesajlariBasaEkle(mesajList);
                        isLoading = false;
                    }
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
                        mViewModel.BorcIstekleriDb(uyarimesaji,MainActivity.kullanicistatic.getKullaniciId(),sohbetIdAdptr,miktar,aciklama,tarih,MainActivity.kullanicistatic.getKullaniciAdi(),sohbetIdAdptr,mesajZamani);
                        miktaredit.setText("");
                        aciklamaedit.setText("");
                        odemeTarihiedit.setText("");
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