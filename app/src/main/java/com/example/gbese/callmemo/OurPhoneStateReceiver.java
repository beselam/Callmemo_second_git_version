package com.example.gbese.callmemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * This is our class for intercepting the incoming calls and inform the state accordingly.
 */

public class OurPhoneStateReceiver extends BroadcastReceiver {

    private final String CHANEL_ID = "notifyme";
    private static final int THEID = 45612;

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context,"tkmsg","press to put your callnotes  ","Callmemo");

    }
    public void createNotification(Context context,String tkmsg,String msgtext,String appname){

        createnotify(context,"notify","includeAll");
        PendingIntent pendingintent= PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0);
        NotificationCompat.Builder notification =  new NotificationCompat.Builder(context,CHANEL_ID);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.notify);
        notification.setTicker(tkmsg);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(msgtext);
        notification.setContentText(appname);
        notification.setContentIntent(pendingintent);
        notification.setDefaults(NotificationCompat.DEFAULT_SOUND);
        NotificationManager mn=(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mn.notify(THEID,notification.build());


    }
    public void createnotify(Context context, String Notifications, String IncludeAll){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            CharSequence name= Notifications;
            String description = IncludeAll;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANEL_ID,name,importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
    }

