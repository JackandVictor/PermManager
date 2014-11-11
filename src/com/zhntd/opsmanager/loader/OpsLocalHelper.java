package com.zhntd.opsmanager.loader;

import java.util.ArrayList;
import java.util.List;

import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.utils.Logger;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

/**
 * @author nick
 * 
 */
public class OpsLocalHelper {

	public interface ApplyAllSettingsCallback {
		void onPrepare();

		void onApplyCompleted();
	}

	public class ApplyAllSettingsHelper extends AsyncTask<Void, Void, Void> {

		private ApplyAllSettingsCallback mAllSettingsCallback;
		private AppOpsManager appOpsManager;
		private OpsTemplate otl;
		private int mode;
		private Context mContext;

		/**
		 * @param mAllSettingsCallback
		 * @param appOpsManager
		 * @param otl
		 * @param apps
		 * @param mode
		 */
		public ApplyAllSettingsHelper(Context context,
				ApplyAllSettingsCallback mAllSettingsCallback,
				AppOpsManager appOpsManager, OpsTemplate otl, int mode) {
			super();
			this.mAllSettingsCallback = mAllSettingsCallback;
			this.appOpsManager = appOpsManager;
			this.otl = otl;
			this.mode = mode;
			this.mContext = context;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			applyAllSettingsFor(mContext, appOpsManager, otl, mode);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mAllSettingsCallback.onApplyCompleted();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mAllSettingsCallback.onPrepare();
		}

	}

	/**
	 * @param appOpsManager
	 * @param otl
	 * @param apps
	 * @param mode
	 * @param permLabel
	 */
	private void applyAllSettingsFor(Context context,
			AppOpsManager appOpsManager, OpsTemplate otl, int mode) {

		final PackageManager packageManager = context.getPackageManager();

		final List<AppBean> apps = buildAppList(packageManager, appOpsManager,
				otl);

		for (AppBean app : apps) {
			final int switchOp = AppOpsManager.opToSwitch(otl.getPermLabel());
			// Let OpsManager to control.
			appOpsManager.setMode(switchOp, app.getUid(), app.getPackageName(),
					mode);
		}
	}

	private final List<AppBean> buildAppList(PackageManager packageManager,
			AppOpsManager appOps, OpsTemplate otl) {
		List<AppOpsManager.PackageOps> pkgs;
		List<AppBean> apps = new ArrayList<AppBean>();
		int[] otls = new int[] { otl.getPermLabel() };
		pkgs = appOps.getPackagesForOps(otls);
		if (pkgs != null) {
			for (int i = 0; i < pkgs.size(); i++) {
				AppOpsManager.PackageOps pkgOps = pkgs.get(i);
				ApplicationInfo info;
				try {
					info = packageManager.getApplicationInfo(
							pkgOps.getPackageName(),
							PackageManager.GET_UNINSTALLED_PACKAGES);
				} catch (NameNotFoundException e) {
					Logger.logger("Got a NameNotFoundExp when get application info...");
					continue;
				}
				// ignore system apps.
				if (info != null && filterApp(info)) {
					final AppBean appBean = new AppBean();
					final String label = info.loadLabel(packageManager)
							.toString();
					final String displayName = label != null ? label
							: info.packageName;
					appBean.setDisplayName(displayName);
					appBean.setDisplayIcon(info.loadIcon(packageManager));
					appBean.setUid(info.uid);
					appBean.setPackageName(info.packageName);
					appBean.setMode(AppOpsManager.MODE_ALLOWED);

					List<AppOpsManager.OpEntry> entries = pkgOps.getOps();
					if (entries != null) {
						for (int j = 0; j < entries.size(); j++) {
							AppOpsManager.OpEntry ope = entries.get(j);
							if (ope.getOp() == otl.getPermLabel()) {
								appBean.setMode(ope.getMode());
							}
						}
					}

					apps.add(appBean);
				}
			}
			return apps;
		}
		return apps;
	}

	/**
	 * We only need to get a thrid-party app.
	 * 
	 * @param info
	 * @return if this is a 3-rd party app.
	 */
	public static boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param callback
	 * @param appOpsManager
	 * @param otl
	 * @param apps
	 * @param mode
	 * @param permLabel
	 */
	public void applyAllSettingsForAsync(Context context,
			ApplyAllSettingsCallback callback, AppOpsManager appOpsManager,
			OpsTemplate otl, int mode) {
		new ApplyAllSettingsHelper(context, callback, appOpsManager, otl, mode)
				.execute();
	}

}
