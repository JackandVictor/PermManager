package com.zhntd.opsmanager.activity;

import java.util.List;

import android.app.AppOpsManager;
import android.app.ProgressDialog;

import com.zhntd.opsmanager.OpsManager;
import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.loader.AppBean;
import com.zhntd.opsmanager.loader.OpsLocalHelper;
import com.zhntd.opsmanager.loader.OpsLocalHelper.ApplyAllSettingsCallback;
import com.zhntd.opsmanager.loader.PermDetailListAdapter;
import com.zhntd.opsmanager.loader.OpsLoader;
import com.zhntd.opsmanager.loader.OpsLoader.AppLoaderCallback;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import com.zhntd.opsmanager.R;

/**
 * @author zhntd
 * @date Nov 6, 2014
 * @time 2:32:24 PM
 */
public class OpsDetailsActivity extends BaseActivity implements
		AppLoaderCallback, ApplyAllSettingsCallback {

	private AppOpsManager mAppOps;
	private PackageManager mPackageManager;
	private ListView mListView;
	private OpsTemplate mOtl;

	private ProgressDialog mProgressDialog;

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
	 * initial mess things here.
	 */
	private void init() {
		mAppOps = (AppOpsManager) this
				.getSystemService(Context.APP_OPS_SERVICE);
		mPackageManager = this.getPackageManager();
	}

	/**
	 * @param otl
	 */
	private void StartLoading(OpsTemplate otl) {
		OpsLoader.getInstance().buildAppList(this, mAppOps, mPackageManager,
				otl, this, OpsLoader.FLAG_GET_LIST);
	}

	@Override
	public void onAppsListLoadFinish(List<AppBean> apps, int count) {
		PermDetailListAdapter adapter = new PermDetailListAdapter(apps, this,
				mAppOps, mOtl);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onListPreLoad() {
		// EMPTY maybe we call show a progress bar?
	}

	@Override
	public void cleanUp() {
		this.mAppOps = null;
		this.mListView = null;
		this.mOtl = null;
		this.mPackageManager = null;
	}

	@Override
	public void applySettingsForAll(int mode) {

		new OpsLocalHelper().applyAllSettingsForAsync(getApplicationContext(),
				this, mAppOps, mOtl, mode);
	}

	@Override
	public void onPrepare() {
		mProgressDialog = ProgressDialog.show(this, getResources()
				.getString(R.string.dialog_title_handling), getResources()
				.getString(R.string.dialog_content_handling));
	}

	@Override
	public void onApplyCompleted() {
		mProgressDialog.dismiss();
		// give a tip to user.
		Toast.makeText(this,
				getResources().getString(R.string.work_done_success),
				Toast.LENGTH_SHORT).show();
		StartLoading(mOtl);
	}
}
