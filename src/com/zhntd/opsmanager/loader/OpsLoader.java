
package com.zhntd.opsmanager.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.utils.Logger;

import android.Manifest;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.R.color;
import android.app.AppOpsManager;

/**
 * @author zhntd
 * @date Nov 6, 2014
 * @time 5:01:12 PM
 */
public class OpsLoader {

    public static final int FLAG_GET_LIST = 0;
    public static final int FLAG_GET_COUNT = 1;

    private static final OpsLoader mLoader = new OpsLoader();

    public static OpsLoader getInstance() {
        return mLoader;
    }

    public interface AppLoaderCallback {
        void onListPreLoad();

        void onAppsListLoadFinish(List<AppBean> apps, int count);
    }

    /**
     * @param context
     * @param appOps
     * @param packageManager
     * @param otl
     * @param callback Must not be null.
     * @param flag What you want? full list or just a count?
     */
    public void buildAppList(Context context, AppOpsManager appOps,
            PackageManager packageManager, OpsTemplate otl, AppLoaderCallback callback, int flag) {

        // special part..such as: INTERNET
        if (AppOpsManager.OP_DATA_CONNECT_CHANGE == otl.getPermLabel()) {
            String permission = Manifest.permission.INTERNET;
            new SpecialPermissionAppsLoader(permission, context, packageManager, callback)
                    .execute();
            return;
        }

        // we use opsManager for this part.
        switch (flag) {
            case FLAG_GET_LIST:
                new AppListLoader(context, appOps, packageManager, otl, callback)
                        .execute();
                break;

            case FLAG_GET_COUNT:
                new SimpleAppListLoader(context, appOps, packageManager, otl, callback)
                        .execute();

            default:
                break;
        }

    }

    /**
     * A custom Loader that loads all of the very perm applications.
     */
    private static class SimpleAppListLoader extends AsyncTask<Void, Void, Integer> {

        private AppOpsManager mAppOps;
        private PackageManager mPackageManager;
        private OpsTemplate otl;
        private List<AppBean> mApps;
        private AppLoaderCallback mAppLoaderCallback;

        /**
         * @param context
         * @param mAppOps
         * @param mPackageManager
         * @param otl
         */
        public SimpleAppListLoader(Context context, AppOpsManager appOps,
                PackageManager packageManager, OpsTemplate otl, AppLoaderCallback callback) {
            this.mAppOps = appOps;
            this.mPackageManager = packageManager;
            this.otl = otl;
            this.mAppLoaderCallback = callback;
        }

        private final int buildAppListCount(OpsTemplate otl) {
            List<AppOpsManager.PackageOps> pkgs;
            int count = 0;
            int[] otls = new int[] {
                    otl.getPermLabel()
            };
            pkgs = mAppOps.getPackagesForOps(otls);
            if (pkgs != null) {
                for (int i = 0; i < pkgs.size(); i++) {
                    AppOpsManager.PackageOps pkgOps = pkgs.get(i);
                    ApplicationInfo info;
                    try {
                        info = mPackageManager.getApplicationInfo(
                                pkgOps.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    } catch (NameNotFoundException e) {
                        continue;
                    }
                    // ignore system apps.
                    if (info != null && filterApp(info)) {
                        count++;
                    }
                }
            }
            return count;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return buildAppListCount(otl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAppLoaderCallback.onListPreLoad();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (mAppLoaderCallback != null)
                mAppLoaderCallback.onAppsListLoadFinish(null, result);
            Logger.logger("load apps count finish got a result:-->" + result);
        }
    }

    /**
     * A custom Loader that loads all of the very perm applications.
     */
    private static class AppListLoader extends AsyncTask<Void, Void, List<AppBean>> {

        private AppOpsManager mAppOps;
        private PackageManager mPackageManager;
        private OpsTemplate otl;
        private List<AppBean> mApps;
        private AppLoaderCallback mAppLoaderCallback;

        /**
         * @param context
         * @param mAppOps
         * @param mPackageManager
         * @param otl
         */
        public AppListLoader(Context context, AppOpsManager appOps,
                PackageManager packageManager, OpsTemplate otl, AppLoaderCallback callback) {
            this.mAppOps = appOps;
            this.mPackageManager = packageManager;
            this.otl = otl;
            this.mAppLoaderCallback = callback;
        }

        private final List<AppBean> buildAppList(OpsTemplate otl) {
            List<AppOpsManager.PackageOps> pkgs;
            List<AppBean> apps = new ArrayList<AppBean>();
            int[] otls = new int[] {
                    otl.getPermLabel()
            };
            pkgs = mAppOps.getPackagesForOps(otls);
            if (pkgs != null) {
                for (int i = 0; i < pkgs.size(); i++) {
                    AppOpsManager.PackageOps pkgOps = pkgs.get(i);
                    ApplicationInfo info;
                    try {
                        info = mPackageManager.getApplicationInfo(
                                pkgOps.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
                    } catch (NameNotFoundException e) {
                        continue;
                    }
                    // ignore system apps.
                    if (info != null && filterApp(info)) {
                        AppBean appBean = new AppBean();
                        String label = info.loadLabel(mPackageManager).toString();
                        String displayName = label != null ? label : info.packageName;
                        appBean.setDisplayName(displayName);
                        appBean.setDisplayIcon(info.loadIcon(mPackageManager));
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

        @Override
        protected List<AppBean> doInBackground(Void... params) {
            return buildAppList(otl);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAppLoaderCallback.onListPreLoad();
        }

        @Override
        protected void onPostExecute(List<AppBean> result) {
            super.onPostExecute(result);
            if (mAppLoaderCallback != null)
                mAppLoaderCallback.onAppsListLoadFinish(result, result.size());
        }
    }

    private static class SpecialPermissionAppsLoader extends AsyncTask<Void, Void, List<AppBean>> {

        private PackageManager mPackageManager;
        private String mPermission;
        private Context mContext;
        private AppLoaderCallback mCallback;

        /**
         * @param mPermission What permission you wanna build apps for? such as:
         *            Manifest.permission.INTERNET
         * @param mContext
         */
        public SpecialPermissionAppsLoader(String mPermission, Context c,
                PackageManager packageManager, AppLoaderCallback callback) {
            this.mPermission = mPermission;
            this.mContext = c;
            this.mPackageManager = packageManager;
            this.mCallback = callback;
        }

        @Override
        protected List<AppBean> doInBackground(
                Void... params) {
            return buildGivenPermAppsList(mPermission, mPackageManager);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCallback.onListPreLoad();
        }

        @Override
        protected void onPostExecute(List<AppBean> result) {
            super.onPostExecute(result);
            if (mCallback != null)
                mCallback.onAppsListLoadFinish(result, result.size());
        }

        /**
         * Build app list for the given permission.
         * 
         * @param permissionm
         * @param packageManager
         */
        private List<AppBean> buildGivenPermAppsList(String permissionm,
                PackageManager packageManager) {

            final List<AppBean> apps = new ArrayList<AppBean>();
            final List<ApplicationInfo> installed = packageManager
                    .getInstalledApplications(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

            for (ApplicationInfo info : installed) {

                // ignore system apps
                if (!filterApp(info))
                    continue;

                if (PackageManager.PERMISSION_GRANTED != packageManager
                        .checkPermission(permissionm, info.packageName)) {
                    continue;
                }
                // let's go.
                final AppBean app = new AppBean();
                final String label = info.loadLabel(mPackageManager).toString();
                final String displayName = label != null ? label : info.packageName;
                app.setDisplayName(displayName);
                app.setDisplayIcon(info.loadIcon(mPackageManager));
                app.setUid(info.uid);
                app.setPackageName(info.packageName);

                apps.add(app);
            }
            return apps;
        }
    }

    /**
     * We only need to get a thrid-party app.
     * 
     * @param info
     * @return
     */
    public static boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }
}
