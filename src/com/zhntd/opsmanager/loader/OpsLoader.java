
package com.zhntd.opsmanager.loader;

import java.util.ArrayList;
import java.util.List;

import com.zhntd.opsmanager.OpsTemplate;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.app.AppOpsManager;

public class OpsLoader {

    public static final int FLAG_GET_LIST = 0;
    public static final int FLAG_GET_COUNT = 1;

    private static final OpsLoader mLoader = new OpsLoader();

    public static OpsLoader getInstance() {
        return mLoader;
    }

    public interface AppLoaderCallback {
        void onDetailListLoadFinish(List<AppBean> apps, int count);
    }

    /**
     * @param context
     * @param appOps
     * @param packageManager
     * @param otl
     * @param callback Must not be null.
     * @param flag What you want?
     */
    public void buildAppList(Context context, AppOpsManager appOps,
            PackageManager packageManager, OpsTemplate otl, AppLoaderCallback callback, int flag) {
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
    public static class SimpleAppListLoader extends AsyncTask<Void, Void, Integer> {

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
                count = pkgs.size();
                return count;
            }
            return count;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return buildAppListCount(otl);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (mAppLoaderCallback != null)
                mAppLoaderCallback.onDetailListLoadFinish(null, result);
        }
    }

    /**
     * A custom Loader that loads all of the very perm applications.
     */
    public static class AppListLoader extends AsyncTask<Void, Void, List<AppBean>> {

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

                    if (info != null) {
                        // ignore system apps.
                        // if (info.flags == ApplicationInfo.)
                        // continue;
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
        protected void onPostExecute(List<AppBean> result) {
            super.onPostExecute(result);
            if (mAppLoaderCallback != null)
                mAppLoaderCallback.onDetailListLoadFinish(result, result.size());
        }
    }
}
