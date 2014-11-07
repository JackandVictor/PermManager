
package com.zhntd.opsmanager.activity;

import java.util.List;

import android.app.AppOpsManager;

import com.zhntd.opsmanager.OpsManager;
import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.loader.AppBean;
import com.zhntd.opsmanager.loader.OpsDetailListAdapter;
import com.zhntd.opsmanager.loader.OpsLoader;
import com.zhntd.opsmanager.loader.OpsLoader.AppLoaderCallback;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

/**
 * @author nick
 * @date Nov 6, 2014
 * @time 2:32:24 PM TODO
 */
public class OpsDetails extends ActivityBase implements AppLoaderCallback {

    private AppOpsManager mAppOps;
    private PackageManager mPackageManager;
    private ListView mListView;
    private OpsTemplate otl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        otl = (OpsTemplate) bundle.getSerializable(OpsManager.KEY_OTL);
        // TODO get real name
        getActionBar().setTitle(otl.getPermCategorie() + "");
        mListView = new ListView(this);
        setContentView(mListView);
        initLoader();
        inflateListAsync(otl);

    }

    /**
     * initial && mess things 
     */
    private void initLoader() {
        mAppOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        mPackageManager = this.getPackageManager();
    }

    /**
     * @param otl
     */
    private void inflateListAsync(OpsTemplate otl) {
        OpsLoader.getInstance().buildAppList(this, mAppOps, mPackageManager, otl, this, OpsLoader.FLAG_GET_LIST);
    }

    @Override
    public void onDetailListLoadFinish(List<AppBean> apps, int count) {
        OpsDetailListAdapter adapter = new OpsDetailListAdapter(apps, this, mAppOps, otl);
        mListView.setAdapter(adapter);
    }

}
