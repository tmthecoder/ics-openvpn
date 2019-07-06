package de.blinkt.openvpn;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.blinkt.openvpn.core.ProfileManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<VpnProfile> allvpn = (ArrayList<VpnProfile>) getPM(context).getProfiles();
        for (VpnProfile vpn: allvpn) {
            if (vpn == ProfileManager.getConnectedProfile()) {
                allvpn.remove(vpn);
                break;
            }
        }
        Random rand = new Random();
        OnBootReceiver.startVPN(allvpn.get(rand.nextInt(allvpn.size())), context);
    }
    private ProfileManager getPM(Context context) {
        return ProfileManager.getInstance(context);
    }
    public void setAlarm(Context context) {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 30, pi); // Millisec * Second * Minute
    }
    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
