package com.example.d308vacation.UI;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import com.example.d308vacation.R;

public class VacationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String vacationTitle = intent.getStringExtra("vacationTitle");


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "vacationChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Vacation Alert")
                .setContentText(vacationTitle + " is starting/ending!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


}
