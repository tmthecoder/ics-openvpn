/*
 * Copyright (c) 2012-2015 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/ics-openvpn-license.txt
 */

package de.blinkt.openvpn.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.RestrictionsManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.view.Window;
import de.blinkt.openvpn.api.AppRestrictions;

public class BaseActivity extends Activity {
    private boolean isAndroidTV() {
        final UiModeManager uiModeManager = (UiModeManager) getSystemService(Activity.UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isAndroidTV()) {
            requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
