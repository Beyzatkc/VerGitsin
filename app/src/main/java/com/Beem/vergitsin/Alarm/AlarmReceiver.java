package com.Beem.vergitsin.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm tetiklendi!");
        // Veya Toast (kullanıcıya görünür)
        Toast.makeText(context, "Alarm çaldı!", Toast.LENGTH_SHORT).show();

        // AlarmActivity başlatma kodu
        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtras(intent); // extra verileri geç
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }


}
