/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.systemui.quicksettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;

public class StayAwakeTile extends QuickSettingsTile {

    public static final int SCREEN_TIMEOUT_NEVERSLEEP = Integer.MAX_VALUE; // MAX_VALUE equates to approx 24 days
    private static final int SCREEN_TIMEOUT_MIN       =  15000;
    
    private int storedUserTimeout = SCREEN_TIMEOUT_MIN;

    public static QuickSettingsTile mInstance;

    public static QuickSettingsTile getInstance(Context context, LayoutInflater inflater,
            QuickSettingsContainerView container, final QuickSettingsController qsc, Handler handler, String id) {
        mInstance = null;
        mInstance = new StayAwakeTile(context, inflater, container, qsc);
        return mInstance;
    }

    public StayAwakeTile(Context context,
            LayoutInflater inflater, QuickSettingsContainerView container, QuickSettingsController qsc) {
        super(context, inflater, container, qsc);

        updateTileState();

        mOnClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleState();
            }
        };

        mOnLongClick = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent("android.settings.DISPLAY_SETTINGS");
                startSettingsActivity(intent);
                return true;
            }
        };
    }

    protected void updateTileState() {
        int screenTimeout = getScreenTimeout();
        if (screenTimeout == SCREEN_TIMEOUT_NEVERSLEEP) {
            mLabel = mContext.getString(R.string.quick_settings_stayawake);
            mDrawable = R.drawable.ic_qs_stayawake_on;
        } else {
            mLabel = mContext.getString(R.string.quick_settings_stayawake_off);
            mDrawable = R.drawable.ic_qs_stayawake_off;
        }
    }

    protected void toggleState() {
        int screenTimeout = getScreenTimeout();
        
        // Enable StayAwake
        if (screenTimeout != SCREEN_TIMEOUT_NEVERSLEEP) {
            storedUserTimeout = screenTimeout;
            screenTimeout = SCREEN_TIMEOUT_NEVERSLEEP;
            mDrawable = R.drawable.ic_qs_stayawake_on;
            mLabel = mContext.getString(R.string.quick_settings_stayawake);
        } 
        
        // Disable StayAwake
        else {
            screenTimeout = storedUserTimeout;            
            mDrawable = R.drawable.ic_qs_stayawake_off;
            mLabel = mContext.getString(R.string.quick_settings_stayawake_off);
        }

        Settings.System.putInt(
                mContext.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, screenTimeout);
        
        updateQuickSettings();
    }

    private int getScreenTimeout() {
        return Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 0);
    }
}
