package com.Beem.vergitsin.Alarm;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.Beem.vergitsin.R;

public class AlarmActivity extends AppCompatActivity {
    private Button alarmikapat;
    private MediaPlayer mediaPlayer;
    private TextView borc_miktr;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        String adi = getIntent().getStringExtra("adi");
        String miktar=getIntent().getStringExtra("miktar");

        if ("alinanborc".equals(adi)) {
            setContentView(R.layout.activity_alarm_alinan);
        }else if("verilenborc".equals(adi)){
            setContentView(R.layout.activity_alarm_verilen);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mediaPlayer = MediaPlayer.create(this, R.raw.alarmsounds);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        alarmikapat=findViewById(R.id.btnAlarmKapat);
        borc_miktr=findViewById(R.id.borc_miktr);
        borc_miktr.setText(miktar);
        alarmikapat.setOnClickListener(b->{
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            finish();
        });

    }
}