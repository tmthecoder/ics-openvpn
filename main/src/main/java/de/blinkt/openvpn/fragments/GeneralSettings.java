/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/ics-openvpn-license.txt
 * Edited by Tejas Mehta, Connor McDermid, Frank Gomes to add hopping functionality
 */

package de.blinkt.openvpn.fragments;
import java.io.File;
import java.util.Collection;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceClickListener;

import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import de.blinkt.openvpn.BuildConfig;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.activities.OpenSSLSpeed;
import de.blinkt.openvpn.api.ExternalAppDatabase;
import de.blinkt.openvpn.core.ProfileManager;


public class GeneralSettings extends PreferenceFragment implements OnPreferenceClickListener, OnClickListener, Preference.OnPreferenceChangeListener {

	private ExternalAppDatabase mExtapp;
	private ListPreference mAlwaysOnVPN;
	private boolean randOn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.general_settings);


        PreferenceCategory devHacks = (PreferenceCategory) findPreference("device_hacks");
		mAlwaysOnVPN = (ListPreference) findPreference("alwaysOnVpn");
        mAlwaysOnVPN.setOnPreferenceChangeListener(this);

      	//Pref for interval if needed
		EditTextPreference nonRandInput = (EditTextPreference) findPreference("nonRandInterval");
		checkInt(nonRandInput, nonRandInput.getText());
		nonRandInput.setOnPreferenceChangeListener((preference, newValue) -> {
            EditTextPreference pref = (EditTextPreference) preference;
            checkInt(pref, newValue.toString());
            return true;
        });
		//Pref for rand Switch
        SwitchPreference switchRand = (SwitchPreference) findPreference("randHopperPref");
        switchRand.setSwitchTextOff(R.string.rand_off);
        //Change text based on on or off
        switchRand.setSwitchTextOn(R.string.rand_on);
        randOn = switchRand.isChecked();
        if (randOn) {
        	nonRandInput.setEnabled(false);
		}
        //Change listener to show or hide the editText above
        switchRand.setOnPreferenceChangeListener((preference, newValue) -> {
			 randOn = ((SwitchPreference) preference).isChecked();
			 if (randOn) {
				 nonRandInput.setEnabled(true);
			 } else {
				 nonRandInput.setEnabled(false);
			 }
			return true;
		});

        Preference loadtun = findPreference("loadTunModule");
		if(!isTunModuleAvailable()) {
			loadtun.setEnabled(false);
            devHacks.removePreference(loadtun);
        }

        CheckBoxPreference cm9hack = (CheckBoxPreference) findPreference("useCM9Fix");
        if (!cm9hack.isChecked() && (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            devHacks.removePreference(cm9hack);
        }

        CheckBoxPreference useInternalFS = (CheckBoxPreference) findPreference("useInternalFileSelector");
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
		{
			devHacks.removePreference(useInternalFS);
		}

		mExtapp = new ExternalAppDatabase(getActivity());
		Preference clearapi = findPreference("clearapi");
		clearapi.setOnPreferenceClickListener(this);

		findPreference("osslspeed").setOnPreferenceClickListener(this);

        if(devHacks.getPreferenceCount()==0)
            getPreferenceScreen().removePreference(devHacks);

        if (!BuildConfig.openvpn3) {
            PreferenceCategory appBehaviour = (PreferenceCategory) findPreference("app_behaviour");
			CheckBoxPreference ovpn3 = (CheckBoxPreference) findPreference("ovpn3");
			ovpn3.setEnabled(false);
			ovpn3.setChecked(false);
        }


		setClearApiSummary();
	}

	//Method to check the
	private void checkInt(EditTextPreference editText, String textVal) {
		if (!textVal.matches("[0-9]+")) {
			//If no entries, show according summary
			editText.setSummary(R.string.summary_no_num_edit_text);
		} else {
			if (Integer.parseInt(textVal) < 5) {
				editText.setSummary(R.string.summary_low_num_edit_text);
			} else {
				CharSequence summary = "Your set hopping interval is: " + textVal + " minutes";
				editText.setSummary(summary);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();




        VpnProfile vpn = ProfileManager.getAlwaysOnVPN(getActivity());
		StringBuffer sb = new StringBuffer(getString(R.string.defaultvpnsummary));
		sb.append('\n');
        if (vpn== null)
            sb.append(getString(R.string.novpn_selected));
        else
           sb.append(getString(R.string.vpnselected, vpn.getName()));
		mAlwaysOnVPN.setSummary(sb.toString());

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference== mAlwaysOnVPN) {
            VpnProfile vpn = ProfileManager.get(getActivity(), (String) newValue);
            mAlwaysOnVPN.setSummary(vpn.getName());
        }
        return true;
    }

    private void setClearApiSummary() {
		Preference clearapi = findPreference("clearapi");

		if(mExtapp.getExtAppList().isEmpty()) {
			clearapi.setEnabled(false);
			clearapi.setSummary(R.string.no_external_app_allowed);
		} else { 
			clearapi.setEnabled(true);
			clearapi.setSummary(getString(R.string.allowed_apps,getExtAppList(", ")));
		}
	}

	private String getExtAppList(String delim) {
		ApplicationInfo app;
		PackageManager pm = getActivity().getPackageManager();

		String applist=null;
		for (String packagename : mExtapp.getExtAppList()) {
			try {
				app = pm.getApplicationInfo(packagename, 0);
				if (applist==null)
					applist = "";
				else
					applist += delim;
				applist+=app.loadLabel(pm);

			} catch (NameNotFoundException e) {
				// App not found. Remove it from the list
				mExtapp.removeApp(packagename);
			}
		}

		return applist;
	}

	private boolean isTunModuleAvailable() {
		// Check if the tun module exists on the file system
        return new File("/system/lib/modules/tun.ko").length() > 10;
    }

	@Override
	public boolean onPreferenceClick(Preference preference) { 
		if(preference.getKey().equals("clearapi")){
			Builder builder = new AlertDialog.Builder(getActivity());
			builder.setPositiveButton(R.string.clear, this);
			builder.setNegativeButton(android.R.string.cancel, null);
			builder.setMessage(getString(R.string.clearappsdialog,getExtAppList("\n")));
			builder.show();
		} else if (preference.getKey().equals("osslspeed")) {
			startActivity(new Intent(getActivity(), OpenSSLSpeed.class));
		}
			
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if( which == Dialog.BUTTON_POSITIVE){
			mExtapp.clearAllApiApps();
			setClearApiSummary();
		}
	}


}