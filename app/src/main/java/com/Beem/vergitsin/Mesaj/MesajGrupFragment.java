package com.Beem.vergitsin.Mesaj;

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

public class MesajGrupFragment extends Fragment implements CevapGeldiGrup{
    private String SonMesaj;
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
    }

    private void listeyiGuncelle(ArrayList<Mesaj> guncelMesajListesi) {
        if (adapter != null) {
            adapter.setMesajList(guncelMesajListesi);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new MesajAdapterGrup(guncelMesajListesi, requireContext());
            mesajGrupRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_mesaj_grup, container, false);
        uyarimesaji=new UyariMesaj(requireContext(),false);
        String kaynak = null;
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
        adapter = new MesajAdapterGrup(bosListe, requireContext());
        mesajGrupRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mesajGrupRecyclerView.setAdapter(adapter);

        if ("mainactivity".equals(kaynak)) {
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
            mViewModel.tamamlandimsj().observe(getViewLifecycleOwner(), msj -> {
                if (msj != null) {
                    adapter.mesajEkle(msj);
                    mesajGrupRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            });
            Observe.observeOnce(mViewModel.tumMesajlar(), getViewLifecycleOwner(), mesajList -> {
                mViewModel.IddenGonderenAdaUlasma(mesajList);
            });

            mViewModel.tamamlandi().observe(getViewLifecycleOwner(), tamamlandiMi -> {
                if (Boolean.TRUE.equals(tamamlandiMi)) {
                    ArrayList<Mesaj> mesajList = mViewModel.tumMesajlar().getValue();
                    if (mesajList != null) {
                        listeyiGuncelle(mesajList);
                        istekEditTextViewLayout.setVisibility(View.VISIBLE);
                        istekTextViewLayout.setVisibility(View.GONE);
                    }
                }
            });
        } else {
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
                }
            });
            Observe.observeOnce(mViewModel.tumMesajlar(), getViewLifecycleOwner(), mesajList -> {
                mViewModel.IddenGonderenAdaUlasma(mesajList);
            });
            mViewModel.tamamlandi().observe(getViewLifecycleOwner(), tamamlandiMi -> {
                if (Boolean.TRUE.equals(tamamlandiMi)) {
                    ArrayList<Mesaj> mesajList = mViewModel.tumMesajlar().getValue();
                    if (mesajList != null) {
                        listeyiGuncelle(mesajList);
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
    @Override
    public void onCevapGeldiGrup(String cvp, String ID) {
        mViewModel.GelenCevabiKaydetme(sohbetIdAdptr,ID,cvp);
    }
}