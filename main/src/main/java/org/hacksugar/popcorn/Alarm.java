/*
 * Copyright (c) 2019 Tejas Mehta, Frank Gomes, Connor McDermid
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/ics-openvpn-license.txt
 */

package org.hacksugar.popcorn;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.blinkt.openvpn.OnBootReceiver;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ProfileManager;

import java.util.ArrayList;
import java.util.Random;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Receive method for alarm, main VPN switching happens here when called by startAlarm
        ArrayList<VpnProfile> allvpn = new ArrayList<>(getPM(context).getProfiles());
        //Make an ArrayList of all profiles and delete the one in current use
        for (VpnProfile vpn: allvpn) {
            if (vpn == ProfileManager.getConnectedProfile()) {
                allvpn.remove(vpn);
                break;
            }
        }
        Random rand = new Random();
        //Make a random and get a random profile from remaining profiles in ArrayList
        OnBootReceiver.startVPN(allvpn.get(rand.nextInt(allvpn.size())), context);
    }
    //Method to access profile manager(used to get profile info)
    private ProfileManager getPM(Context context) {
        return ProfileManager.getInstance(context);
    }
    //Method to set the alarm, but for a random time interval
    public void setAlarm(Context context) {
        Random rand = new Random();
        int interval = rand.nextInt(90) + 30;
        startAlarm(context, interval);
    }
    //Method to set alarm from a given interval
    public void setAlarm(Context context, int interval) {
        startAlarm(context, interval);
    }
    //Method to cancel the alarm(used upon disconnect of VPN)
    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    //Method to star alarm, called from both setAlarm methods. Used to set the recursive hopper based on random or given interval
    private void startAlarm(Context context, int interval) {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * interval, pi); // Millisec * Second * Minute
    }
}
