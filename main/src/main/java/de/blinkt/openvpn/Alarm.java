package de.blinkt.openvpn;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.blinkt.openvpn.core.ProfileManager;

import java.util.Collection;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Collection<VpnProfile> allvpn = getPM(context).getProfiles();
        //OnBootReceiver.startVPN();
    }
    private ProfileManager getPM(Context context) {
        return ProfileManager.getInstance(context);
    }
    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 30, pi); // Millisec * Second * Minute
    }
}
