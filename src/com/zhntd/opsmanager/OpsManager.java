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

import android.Manifest;
import android.R.integer;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.zhntd.opsmanager.R;
import com.zhntd.opsmanager.activity.BaseActivity;
import com.zhntd.opsmanager.activity.NetManagerActivity;
import com.zhntd.opsmanager.activity.OpsDetailsActivity;
import com.zhntd.opsmanager.loader.SummaryListAdapter;
import com.zhntd.opsmanager.loader.OpsLoader;
import com.zhntd.opsmanager.net.NetworkControlor;
import com.zhntd.opsmanager.utils.Logger;

public class OpsManager extends BaseActivity implements OnItemClickListener {

	private ListView mListView;

	private List<OpsTemplate> mOpsList;
	private String[] mOpsCate;
	private String[] mOpsLabels;

	public static String KEY_OTL = "com.zhntd.bundle.key.otl";

	/**
	 * @return OpsTemplates
	 */
	private void createOpsListAndStrs() {
		mOpsCate = getResources().getStringArray(R.array.app_ops_categories);
		mOpsLabels = getResources().getStringArray(R.array.app_ops_labels);
		mOpsList = new ArrayList<OpsTemplate>();
		// personal
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_READ_CONTACTS, true));
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_WRITE_CONTACTS));
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_READ_CALL_LOG));
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_WRITE_CALL_LOG));
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_READ_CALENDAR));
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_WRITE_CALENDAR));
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_READ_CLIPBOARD));
		mOpsList.add(new OpsTemplate(AppOpsState.PERSONAL_TEMPLATE,
				AppOpsManager.OP_WRITE_CLIPBOARD));

		// media
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_VIBRATE, true));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_CAMERA));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_RECORD_AUDIO));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_PLAY_AUDIO));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_TAKE_MEDIA_BUTTONS));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_TAKE_AUDIO_FOCUS));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_AUDIO_MASTER_VOLUME));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_AUDIO_VOICE_VOLUME));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_AUDIO_RING_VOLUME));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_AUDIO_MEDIA_VOLUME));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_AUDIO_ALARM_VOLUME));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_AUDIO_NOTIFICATION_VOLUME));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_AUDIO_BLUETOOTH_VOLUME));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_WIFI_CHANGE));
		mOpsList.add(new OpsTemplate(AppOpsState.MEDIA_TEMPLATE,
				AppOpsManager.OP_BLUETOOTH_CHANGE));

		// location
		mOpsList.add(new OpsTemplate(AppOpsState.LOCATION_TEMPLATE,
				AppOpsManager.OP_COARSE_LOCATION, true));
		mOpsList.add(new OpsTemplate(AppOpsState.LOCATION_TEMPLATE,
				AppOpsManager.OP_FINE_LOCATION));
		mOpsList.add(new OpsTemplate(AppOpsState.LOCATION_TEMPLATE,
				AppOpsManager.OP_GPS));
		mOpsList.add(new OpsTemplate(AppOpsState.LOCATION_TEMPLATE,
				AppOpsManager.OP_WIFI_SCAN));
		mOpsList.add(new OpsTemplate(AppOpsState.LOCATION_TEMPLATE,
				AppOpsManager.OP_MONITOR_LOCATION));
		mOpsList.add(new OpsTemplate(AppOpsState.LOCATION_TEMPLATE,
				AppOpsManager.OP_MONITOR_HIGH_POWER_LOCATION));

		// message
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_READ_SMS, true));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_WRITE_SMS));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_RECEIVE_SMS));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_RECEIVE_EMERGECY_SMS));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_RECEIVE_MMS));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_RECEIVE_WAP_PUSH));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_SEND_SMS));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_SEND_MMS));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_READ_MMS));
		mOpsList.add(new OpsTemplate(AppOpsState.MESSAGING_TEMPLATE,
				AppOpsManager.OP_WRITE_MMS));

		// device
		mOpsList.add(new OpsTemplate(AppOpsState.DEVICE_TEMPLATE,
				AppOpsManager.OP_POST_NOTIFICATION, true));
		mOpsList.add(new OpsTemplate(AppOpsState.DEVICE_TEMPLATE,
				AppOpsManager.OP_WRITE_SETTINGS));
		mOpsList.add(new OpsTemplate(AppOpsState.DEVICE_TEMPLATE,
				AppOpsManager.OP_SYSTEM_ALERT_WINDOW));
		mOpsList.add(new OpsTemplate(AppOpsState.DEVICE_TEMPLATE,
				AppOpsManager.OP_ACCESS_NOTIFICATIONS));
		mOpsList.add(new OpsTemplate(AppOpsState.DEVICE_TEMPLATE,
				AppOpsManager.OP_WAKE_LOCK));
		mOpsList.add(new OpsTemplate(AppOpsState.DEVICE_TEMPLATE,
				AppOpsManager.OP_CALL_PHONE));

		// bootup
		mOpsList.add(new OpsTemplate(AppOpsState.BOOTUP_TEMPLATE,
				AppOpsManager.OP_BOOT_COMPLETED, true));

		// data connection
		mOpsList.add(new OpsTemplate(AppOpsState.DATA_TEMPLATE,
				AppOpsState.DATA_PERMISSION, true));

		// put a name for every permmission
		for (int i = 0; i < mOpsList.size(); i++) {
			OpsTemplate otl = mOpsList.get(i);
			otl.setPermName(mOpsLabels[i]);
			// for a test
			// otl.setPermName(Manifest.permission.INTERNET);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListView = (ListView) getLayoutInflater().inflate(R.layout.perm_list,
				null);
		setContentView(mListView);
		inflateList();
		// prepare
		NetworkControlor.prepare(this);
	}

	/**
	 * time to show sth.
	 */
	private void inflateList() {
		createOpsListAndStrs();
		SummaryListAdapter mOpsListAdapter = new SummaryListAdapter(mOpsList,
				mOpsCate, mOpsLabels, this);
		mListView.setAdapter(mOpsListAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		OpsTemplate otl = mOpsList.get(position);
		if (otl != null) {
			// No need to show anything ?
			if (otl.getAppsCount() <= 0) {
				// TODO make it in res.
				// No, We'd better tell user sth...
				Toast.makeText(OpsManager.this, "no such app found...",
						Toast.LENGTH_SHORT).show();
				return;
			}
			// Okay to show sth now.
			final Class<?> clz = otl.getPermLabel() == AppOpsState.DATA_PERMISSION ? NetManagerActivity.class
					: OpsDetailsActivity.class;
			final Intent intent = new Intent(OpsManager.this, clz);
			final Bundle bundle = new Bundle();
			bundle.putSerializable(KEY_OTL, otl);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			Logger.logger("Got a null OTL, Why?");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void cleanUp() {
		this.mListView = null;
		this.mOpsCate = null;
		this.mOpsLabels = null;
		this.mOpsList = null;
	}

	@Override
	public void applySettingsForAll(int mode) {
		// TODO Auto-generated method stub

	}
}
