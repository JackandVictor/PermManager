package com.zhntd.opsmanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.zhntd.opsmanager.net.DataType;
import com.zhntd.opsmanager.net.NetworkControlor;
import com.zhntd.opsmanager.utils.Logger;

/**
 * @author zhntd
 * @date Nov 9, 2014
 * @time 4:57:06 PM
 */
public class PermManagerService extends Service {

	private NetworkControlor mNetworkControlor;
	public static final String CMD_START_SERVICE = "com.zhntd.action.startService";

	private void chkPermAccess() {
		Logger.logger("Now starting chk and set permission for apps");
		if (mNetworkControlor.applySavedIptablesRules(this, true)) {
			Logger.logger("Successfully Apply Iptable Rules!");
		} else {
			Logger.logger("Apply Iptable Rules failed, Please chk the log...");
		}
	}

	//private IPermManagerService.Stub mBinderStub = new ServiceStub();

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			final String cmd = intent.getAction();
			if (CMD_START_SERVICE.equals(cmd)) {
				initilize();
				chkPermAccess();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void initilize() {
		mNetworkControlor = NetworkControlor.prepare(this);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * Implements
	 */
	/*final class ServiceStub extends IPermManagerService.Stub {

		@Override
		public void notifyOperation(int mode, int uid, String packageName,
				boolean remember, DataType dataType) throws RemoteException {
			// handle notify.
		}
	}*/
}
