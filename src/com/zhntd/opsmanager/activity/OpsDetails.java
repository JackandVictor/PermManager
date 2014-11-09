
package com.zhntd.opsmanager.activity;

import java.util.List;

import android.app.AppOpsManager;

import com.zhntd.opsmanager.OpsManager;
import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.loader.AppBean;
import com.zhntd.opsmanager.loader.PermDetailListAdapter;
import com.zhntd.opsmanager.loader.OpsLoader;
import com.zhntd.opsmanager.loader.OpsLoader.AppLoaderCallback;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

/**
 * @author nick
 * @date Nov 6, 2014
 * @time 2:32:24 PM
 */
public class OpsDetails extends BaseActivity implements AppLoaderCallback {

    private AppOpsManager mAppOps;
    private PackageManager mPackageManager;
    private ListView mListView;
    private OpsTemplate mOtl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mOtl = (OpsTemplate) bundle.getSerializable(OpsManager.KEY_OTL);
        if (mOtl.getPermName() != null)
            setTitle(mOtl.getPermName());
        mListView = new ListView(this);
        setContentView(mListView);
        init();
        StartLoading(mOtl);
    }

    /**
     * initial && mess things
     */
    private void init() {
        mAppOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        mPackageManager = this.getPackageManager();
    }

    /**
     * @param otl
     */
    private void StartLoading(OpsTemplate otl) {
        OpsLoader.getInstance().buildAppList(this, mAppOps, mPackageManager, otl, this,
                OpsLoader.FLAG_GET_LIST);
    }

    @Override
    public void onAppsListLoadFinish(List<AppBean> apps, int count) {
        PermDetailListAdapter adapter = new PermDetailListAdapter(apps, this, mAppOps, mOtl);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onListPreLoad() {
        // EMPTY maybe we call show a progress bar?
    }
}
