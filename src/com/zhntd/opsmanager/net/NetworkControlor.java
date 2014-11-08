
package com.zhntd.opsmanager.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Control network activities of the given application.
 * 
 * @author zhntd
 * @date Nov 8, 2014
 * @time 3:56:47 PM
 */
public class NetworkControlor {

    private Editor mEditor;
    private SharedPreferences mPreferences;
    private Context mContext;

    /**
     * @param sharedPreferences
     * @param mContext
     */
    public NetworkControlor(SharedPreferences sharedPreferences, Context mContext) {
        this.mContext = mContext;
        build();
    }

    private void build() {
        mPreferences = mContext.getSharedPreferences(Api.PREFS_NAME, 0);
        this.mEditor = mPreferences.edit();
    }

    /**
     * @param uid
     * @param type
     * @return
     */
    public boolean writeToSpf(String uid, DataType type) {
        String uids = mPreferences.getString(Api.PREF_WIFI_UIDS, "");
        uids = uids + "|" + uid;
        if (DataType.MOBILE == type)
            return mEditor.putString(Api.PREF_WIFI_UIDS, uids).commit();
        else {
            return mEditor.putString(Api.PREF_3G_UIDS, uids).commit();
        }
    }

    public void showLog(Context ctx) {
        Api.showLog(ctx);
    }

    public void showIptablesRules(Context ctx) {
        Api.showIptablesRules(ctx);
    }

    public void setEnabled(Context ctx, boolean enabled) {
        Api.setEnabled(ctx, enabled);
    }

    public void saveRules(Context ctx) {
        Api.saveRules(ctx);
    }

    public boolean purgeIptables(Context ctx, boolean showErrors) {
        return Api.purgeIptables(ctx, showErrors);
    }

    public boolean isEnabled(Context ctx) {
        return Api.isEnabled(ctx);
    }

    public boolean applySavedIptablesRules(Context ctx, boolean showErrors) {
        return Api.applySavedIptablesRules(ctx, showErrors);
    }

    public boolean applyIptablesRules(Context ctx, boolean showErrors) {
        return Api.applyIptablesRules(ctx, showErrors);
    }

}
