/**
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.zhntd.opsmanager;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.zhntd.opsmanager.R;
import com.zhntd.opsmanager.activity.ActivityBase;
import com.zhntd.opsmanager.activity.OpsDetails;
import com.zhntd.opsmanager.loader.OpsListAdapter;
import com.zhntd.opsmanager.loader.OpsLoader;

public class OpsManager extends ActivityBase implements OnItemClickListener {

    private ListView mListView;

    private List<OpsTemplate> mOpsList;
    private String[] mOpsCate;
    private String[] mOpsLabels;

    public static String KEY_OTL = "com.nick.bundle.key.otl";

    /**
     * @return OpsTemplates
     */
    private List<OpsTemplate> createOpsListAndStrs() {
        mOpsCate = getResources().getStringArray(R.array.app_ops_categories);
        mOpsLabels = getResources().getStringArray(R.array.app_ops_labels);
        List<OpsTemplate> opsList = new ArrayList<OpsTemplate>();
        // personal
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_READ_CONTACTS,
                true));
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_WRITE_CONTACTS));
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_READ_CALL_LOG));
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_WRITE_CALL_LOG));
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_READ_CALENDAR));
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_WRITE_CALENDAR));
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_READ_CLIPBOARD));
        opsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE, AppOpsManager.OP_WRITE_CLIPBOARD));

        // media
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_VIBRATE,
                true));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_CAMERA));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_RECORD_AUDIO));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_PLAY_AUDIO));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_TAKE_MEDIA_BUTTONS));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_TAKE_AUDIO_FOCUS));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_AUDIO_MASTER_VOLUME));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_AUDIO_VOICE_VOLUME));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_AUDIO_RING_VOLUME));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_AUDIO_MEDIA_VOLUME));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_AUDIO_ALARM_VOLUME));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_AUDIO_NOTIFICATION_VOLUME));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_AUDIO_BLUETOOTH_VOLUME));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_WIFI_CHANGE));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_BLUETOOTH_CHANGE));
        opsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
                AppOpsManager.OP_DATA_CONNECT_CHANGE));

        mOpsList = opsList;

        return opsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = (ListView) getLayoutInflater().inflate(R.layout.perm_list, null);
        setContentView(mListView);
        inflateList();
    }

    /**
     * time to show sth.
     */
    void inflateList() {
        createOpsListAndStrs();
        OpsListAdapter opsListAdapter = new OpsListAdapter(mOpsList, mOpsCate,
                mOpsLabels, this);
        mListView.setAdapter(opsListAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OpsTemplate otl = mOpsList.get(position);
        if (otl != null) {
            // No need to show anything ?
            if (otl.getAppsCount() == 0) return;
            final Intent intent = new Intent(OpsManager.this, OpsDetails.class);
            final Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_OTL, otl);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
