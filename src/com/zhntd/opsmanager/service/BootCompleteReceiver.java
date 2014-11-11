package com.zhntd.opsmanager.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhntd.opsmanager.utils.Logger;

public class BootCompleteReceiver extends BroadcastReceiver {
		
		public BootCompleteReceiver() {
			// empty
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.logger("Boot complete bcst received!");
			startPermService(context);
		}

		private void startPermService(Context context) {
			final Intent intent = new Intent();
			intent.setClass(context, PermManagerService.class);
			intent.setAction(PermManagerService.CMD_START_SERVICE);
			context.startService(intent);
		}
	}