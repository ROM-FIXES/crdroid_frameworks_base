/*
 * Copyright (C) 2015 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.qs.tiles;

import android.content.Intent;
import android.provider.Settings;
import android.provider.Settings.System;
import android.service.quicksettings.Tile;

import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.qs.LineageSystemSetting;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.R;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import lineageos.providers.LineageSettings;

import javax.inject.Inject;

/** Quick settings tile: Navbar **/
public class NavBarTile extends QSTileImpl<BooleanState> {

    private final Icon mIcon = ResourceIcon.get(R.drawable.ic_qs_navbar);

    private static final Intent NAVBAR_SETTINGS =
            new Intent("android.settings.NAVBAR_SETTINGS");

    private final LineageSystemSetting mSetting;

    @Inject
    public NavBarTile(QSHost host) {
        super(host);

        mSetting = new LineageSystemSetting(mContext, mHandler, LineageSettings.System.FORCE_SHOW_NAVBAR) {
            @Override
            protected void handleValueChanged(int value, boolean observedChange) {
                handleRefreshState(value);
            }
        };
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    protected void handleClick() {
        setEnabled(!mState.value);
        refreshState();
    }

    @Override
    public Intent getLongClickIntent() {
        return NAVBAR_SETTINGS;
    }

    private void setEnabled(boolean enabled) {
        LineageSettings.System.putInt(mContext.getContentResolver(),
                LineageSettings.System.FORCE_SHOW_NAVBAR,
                enabled ? 1 : 0);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        final int value = arg instanceof Integer ? (Integer) arg : mSetting.getValue();
        final boolean enabled = value != 0;
        state.value = enabled;
        state.label = mContext.getString(R.string.quick_settings_navbar);
        state.icon = mIcon;
        if (enabled) {
            state.contentDescription =  mContext.getString(
                    R.string.quick_settings_navbar_on);
            state.state = Tile.STATE_ACTIVE;
        } else {
            state.contentDescription =  mContext.getString(
                    R.string.quick_settings_navbar_off);
            state.state = Tile.STATE_INACTIVE;
        }
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_navbar);
    }

    @Override
    protected String composeChangeAnnouncement() {
        if (mState.value) {
            return mContext.getString(R.string.quick_settings_navbar_changed_on);
        } else {
            return mContext.getString(R.string.quick_settings_navbar_changed_off);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CRDROID_SETTINGS;
    }

    @Override
    public void handleSetListening(boolean listening) {
        // Do nothing
    }
}
