package com.Beem.vergitsin.Mesaj;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Beem.vergitsin.Grup;
import com.Beem.vergitsin.Gruplar.GrupOnizlemeBottomSheet;
import com.Beem.vergitsin.Gruplar.GruplarYonetici;
import com.Beem.vergitsin.Kullanici.Kullanici;
import com.Beem.vergitsin.Kullanici.Observe;
import com.Beem.vergitsin.MainActivity;
import com.Beem.vergitsin.Profil.DigerProfilFragment;
import com.Beem.vergitsin.Profil.ProfilYonetici;
import com.Beem.vergitsin.R;
import com.Beem.vergitsin.UyariMesaj;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private EditText miktaredit;
    private EditText aciklamaedit;
    private ImageButton gonderButton2edit;
    private EditText odemeTarihiedit;
    private EditText ibanEdit;

    private LinearLayout istekTextViewLayout;
    private TextView gonderenadiview;
    private TextView miktartext;
    private TextView aciklamatext;
    private ImageButton gonderButtontext;
    private TextView odemeTarihitext;
    private TextView ibantext;

    private String SonMesaj;
    private Long SonMesajSaat;
    private String SonMesajadptr;
    private Long SonMesajSaatadptr;

    private LinearLayout grupAdiLinear;

    private Grup grup;
    private GrupOnizlemeBottomSheet bottomSheet;

    private boolean ilkMesajAlindi = false;
    private boolean ilkMesajAlindiadptr = false;
    private Long ilkmsjSaati;
    private Long ilkmsjSaatiadptr;
    private boolean isLoading = false;
    private CevapGeldiGrup arayuzum;
    private MesajSilmeGuncellemeGrup arayuzSilme;
    private Long AcilmaZamani;
    private Long CikilmaZamani;
    private Long AcilmaZamaniadptr;
    private String ibani;
    private LinearLayout borcIstegiYollaBtn;
    private LinearLayout mesaj_gonderGrup_layout;

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
                AcilmaZamaniadptr=getArguments().getLong("acilmaZamani");
                GrupOnizlemeAc(sohbetIdAdptr);
            } else if (kaynak.equals("mainactivity")) {
                PP=getArguments().getString("pp");
                istekatilanAd = getArguments().getString("istekatilanAdi");
                miktari = getArguments().getString("miktar").trim();
                aciklamasi = getArguments().getString("aciklama").trim();
                odemeTarihi = getArguments().getString("odemeTarihi").trim();
                ibani=getArguments().getString("iban").trim();
                sohbetID=getArguments().getString("sohbetId");
                AcilmaZamani=getArguments().getLong("acilmaZamani");
                GrupOnizlemeAc(sohbetID);
            }
            else if(kaynak.equals("cikilmis")){
                sohbetIdAdptr= getArguments().getString("sohbetId");
                sohbetedilenAd=getArguments().getString("sohbetedilenAd");
                sohbetEdilenPP=getArguments().getString("sohbetEdilenPP");
                AcilmaZamaniadptr=getArguments().getLong("acilmaZamani");
                CikilmaZamani = getArguments().getLong("CikilmaZaman");
                System.out.println(CikilmaZamani);
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
        borcIstegiYollaBtn=view.findViewById(R.id.borcIstegiYollaBtn);

        istekEditTextViewLayout=view.findViewById(R.id.istekEditTextViewLayout);
        miktaredit=view.findViewById(R.id.miktaredit);
        aciklamaedit=view.findViewById(R.id.aciklamaedit);
        gonderButton2edit=view.findViewById(R.id.gonderButton2edit);
        odemeTarihiedit=view.findViewById(R.id.odemeTarihiedit);
        ibanEdit=view.findViewById(R.id.ibanEdit);

        grupAdiLinear=view.findViewById(R.id.grupAdiLinear);

        istekTextViewLayout=view.findViewById(R.id.istekTextViewLayout);
        gonderenadiview=view.findViewById(R.id.gonderenadiview);
        miktartext=view.findViewById(R.id.miktar);
        aciklamatext=view.findViewById(R.id.aciklama);
        gonderButtontext=view.findViewById(R.id.gonderButton2);
        odemeTarihitext=view.findViewById(R.id.odemeTarihi);
        ibantext=view.findViewById(R.id.ibantext);

        ArrayList<Mesaj> bosListe = new ArrayList<>();

        mesaj_gonderGrup_layout=view.findViewById(R.id.mesaj_gonderGrup_layout);
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
                        System.out.println(ilkmsjSaati+"-girdim*-"+AcilmaZamani);
                        mViewModel.EskiMesajlariYukle(sohbetID,ilkmsjSaati,AcilmaZamani);
                    }
                    else if("cikilmis".equals(kaynak)){
                        mViewModel.EskiMesajlariYukle(sohbetIdAdptr,ilkmsjSaatiadptr,AcilmaZamaniadptr);
                    }else if("SohbetAdapter".equals(kaynak)){
                        System.out.println(ilkmsjSaatiadptr+"-girdim****-"+AcilmaZamaniadptr);
                        mViewModel.EskiMesajlariYukle(sohbetIdAdptr,ilkmsjSaatiadptr,AcilmaZamaniadptr);
                    }

                }
            }
        });

        // bunu ben koydum aşkım bottomsheeti aciyor
        grupAdiLinear.setOnClickListener(v->{ bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag()); });
        adapter.setProfilGec((id)->{
            ProfileGec(id);
        });


        if ("mainactivity".equals(kaynak)) {
            arayuzum=new CevapGeldiGrup() {
                @Override
                public void onCevapGeldiGrup(String cvpverenid, String mesajID,String cvpverenad,String icerik) {
                    mViewModel.GelenCevabiKaydetme(sohbetID,mesajID,cvpverenid,cvpverenad,icerik);
                }

                @Override
                public void onEveteBastiGrup(String cvpverenid, Mesaj mesaj) {
                    mViewModel.AlinanlarVerilenlerKayit(cvpverenid,mesaj.getIstegiAtanId(),mesaj.getAciklama(),mesaj.getMiktar(),mesaj.getOdenecekTarih(),mesaj.getIban());
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

                @Override
                public void onSonMesajGuncelleme(Mesaj msj) {
                    SonMesaj=msj.getIstekAtanAdi()+" "+msj.getMiktar()+" TL borç isteği";
                    SonMesajSaat=msj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetID,SonMesaj,SonMesajSaat);
                }
            };
            adapter.setListenersil(arayuzSilme);
            mViewModel.KacKisiCevrimiciGrup(sohbetID);
            mViewModel.kackisicevrimici().observe(getViewLifecycleOwner(),kisisayisi-> {
                kackisicevrimici.setText(kisisayisi.toString());
            });

            borcIstegiYollaBtn.setVisibility(View.GONE);
            istekEditTextViewLayout.setVisibility(View.GONE);
            istekTextViewLayout.setVisibility(View.VISIBLE);

            miktartext.setText(miktari);
            aciklamatext.setText(aciklamasi);
            odemeTarihitext.setText(odemeTarihi);
            ibantext.setText(ibani);

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
            mViewModel.MesajBorcistekleriDbCek(sohbetID,AcilmaZamani);
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
                        borcIstegiYollaBtn.setVisibility(View.VISIBLE);
                        Kaydirma();
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

        } else if (kaynak.equals("cikilmis")) {
            adapter.setCikilmisMi(true);
            grupAdi.setText(sohbetedilenAd);
            grupAdiLinear.setClickable(false);
            if (sohbetEdilenPP != null) {
                int resId = requireContext().getResources().getIdentifier(
                        sohbetEdilenPP, "drawable", requireContext().getPackageName());
                grup_fotosu.setImageResource(resId);
            }
            else{
                int resId = requireContext().getResources().getIdentifier(
                        "user", "drawable", requireContext().getPackageName());
                grup_fotosu.setImageResource(resId);
            }
            System.out.println("mgf de girdim");
            mViewModel.GruptanCikilmisMesajlar(sohbetIdAdptr,AcilmaZamaniadptr,CikilmaZamani);
            mViewModel.tumMesajlar().observe(getViewLifecycleOwner(), mesajList -> {
                mViewModel.IddenGonderenAdaUlasma(mesajList,"mesajlar");
                adapter.guncelleMesajListesi(mesajList);
            });
            mViewModel.tamamlandi().observe(getViewLifecycleOwner(), tamamlandiMi -> {
                if (Boolean.TRUE.equals(tamamlandiMi)) {
                    ArrayList<Mesaj> mesajList = mViewModel.tumMesajlar().getValue();
                    if (mesajList != null) {
                        if (!ilkMesajAlindiadptr) {
                            ilkmsjSaatiadptr = mesajList.get(0).getZaman();
                            ilkMesajAlindiadptr = true;
                        }
                        adapter.guncelleMesajListesi(mesajList);
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

        } else {
            arayuzum=new CevapGeldiGrup(){
                @Override
                public void onCevapGeldiGrup(String cvpverenid, String mesajID,String cvpverenad,String icerik) {
                    mViewModel.GelenCevabiKaydetme(sohbetIdAdptr,mesajID,cvpverenid,cvpverenad,icerik);
                }

                @Override
                public void onEveteBastiGrup(String cvpverenid, Mesaj mesaj) {
                    mViewModel.AlinanlarVerilenlerKayit(cvpverenid,mesaj.getIstegiAtanId(),mesaj.getAciklama(),mesaj.getMiktar(),mesaj.getOdenecekTarih(),mesaj.getIban());
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
                        mViewModel.sonMsjDbKaydi(sohbetIdAdptr, SonMesajadptr, SonMesajSaatadptr);
                    }
                }

                @Override
                public void onMesajGuncelleme(Mesaj mesaj) {
                    mViewModel.MesajGuncelleme(sohbetIdAdptr,mesaj);
                }

                @Override
                public void onSonMesajGuncelleme(Mesaj msj) {
                    SonMesajadptr=msj.getIstekAtanAdi()+" "+msj.getMiktar()+" TL borç isteği";
                    SonMesajSaatadptr=msj.getZaman();
                    mViewModel.sonMsjDbKaydi(sohbetIdAdptr,SonMesajadptr,SonMesajSaatadptr);
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
            mViewModel.MesajBorcistekleriDbCek(sohbetIdAdptr,AcilmaZamaniadptr);

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
            borcIstegiYollaBtn.setVisibility(View.VISIBLE);
            istekEditTextViewLayout.setVisibility(View.GONE);
            istekTextViewLayout.setVisibility(View.GONE);

            Kaydirma();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            odemeTarihiedit.setInputType(InputType.TYPE_NULL);
            odemeTarihiedit.setFocusable(false);

            odemeTarihiedit.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int yil = calendar.get(Calendar.YEAR);
                int ay = calendar.get(Calendar.MONTH);
                int gun = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        (view2, year, month, dayOfMonth) -> {
                            String secilenTarih = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                    dayOfMonth, month + 1, year);
                            try {
                                Date girilenTarih = sdf.parse(secilenTarih);
                                Date bugun = sdf.parse(sdf.format(new Date()));

                                if (girilenTarih.before(bugun)) {
                                    Toast.makeText(getContext(), "Geçmiş bir tarih seçemezsiniz!", Toast.LENGTH_SHORT).show();
                                } else {
                                    odemeTarihiedit.setText(secilenTarih);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        yil, ay, gun
                );
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            });

            gonderButton2edit.setOnClickListener(b->{
                String miktar=miktaredit.getText().toString().trim();
                String aciklama=aciklamaedit.getText().toString().trim();
                String odemetarihi=odemeTarihiedit.getText().toString().trim();
                String iban=ibanEdit.getText().toString().trim();
                Long mesajZamani=System.currentTimeMillis();

                if (miktar.isEmpty()) {
                    Toast.makeText(getContext(), "Miktar boş olamaz", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (odemetarihi.isEmpty()) {
                    Toast.makeText(getContext(), "Tarih boş olamaz", Toast.LENGTH_SHORT).show();
                    return;
                }
                    Timestamp tarih=stringToTimestamp(odemetarihi);
                        mViewModel.BorcIstekleriDb(uyarimesaji,MainActivity.kullanicistatic.getKullaniciId(),sohbetIdAdptr,miktar,aciklama,tarih,iban,MainActivity.kullanicistatic.getKullaniciAdi(),sohbetIdAdptr,mesajZamani);
                        miktaredit.setText("");
                        aciklamaedit.setText("");
                        odemeTarihiedit.setText("");
                        ibanEdit.setText("");

                borcIstegiYollaBtn.setVisibility(View.VISIBLE);
                istekEditTextViewLayout.setVisibility(View.GONE);
                istekTextViewLayout.setVisibility(View.GONE);
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

    private void GrupOnizlemeAc(String sohbetID){
        GruplarYonetici yonetici = new GruplarYonetici();
        if(grup==null) grup = new Grup();
        grup.setGrupId(sohbetID);
        yonetici.GrupNesneKontrolu(grup,()->{
            bottomSheet = new GrupOnizlemeBottomSheet(grup,()->{
                // bu kısım da gruptan başarılı şekilde çıkıldıktan sonra çalışıyor

            },()->{
                /// bu kısımı elleme askim yada sen bilirsin adapter ile ilgiili güncelleme varsa kullanabilirsin

            });
        });
    }
    public void Kaydirma() {
        mesaj_gonderGrup_layout.setOnTouchListener(new View.OnTouchListener() {
            float startY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        float endY = event.getRawY();
                        float deltaY = startY - endY;

                        if (deltaY > 30) {
                            // Yukarı kaydırma → formu aç
                            istekEditTextViewLayout.setVisibility(View.VISIBLE);
                            istekEditTextViewLayout.setAlpha(0f);
                            istekEditTextViewLayout.setTranslationY(-50);
                            istekEditTextViewLayout.animate()
                                    .translationY(0)
                                    .alpha(1f)
                                    .setDuration(500)
                                    .start();
                            borcIstegiYollaBtn.setVisibility(View.GONE);

                        } else if (deltaY < -30) {
                            // Aşağı kaydırma → formu kapat
                            istekEditTextViewLayout.setVisibility(View.GONE);
                            borcIstegiYollaBtn.setVisibility(View.VISIBLE);
                        }
                        return true;
                }
                return false;
            }
        });
    }



    private void ProfileGec(String id){
        Kullanici k1 = new Kullanici();
        k1.setKullaniciId(id);
        ProfilYonetici yonetici = new ProfilYonetici(k1);
        yonetici.ProfilDoldur(()->{
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DigerProfilFragment profilFragment = new DigerProfilFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("kullanici", k1);
            profilFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.konteynir, profilFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        },()->{});
    }
}