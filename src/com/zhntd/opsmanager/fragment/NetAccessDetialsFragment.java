package com.zhntd.opsmanager.fragment;

import java.util.List;

import com.zhntd.opsmanager.loader.PermDetailListAdapter;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.R;
import com.zhntd.opsmanager.loader.AppBean;
import com.zhntd.opsmanager.loader.OpsLoader;
import com.zhntd.opsmanager.loader.OpsLoader.AppLoaderCallback;
import com.zhntd.opsmanager.net.NetworkControlor;
import com.zhntd.opsmanager.net.NetworkControlor.WorkFinishedCallback;

public class NetAccessDetialsFragment extends Fragment implements
		OpsLoader.AppLoaderCallback, WorkFinishedCallback {

	/* data type of this page. */
	private int mDataType;
	private Context mContext;

	private View mRootView;
	private ListView mListView;
	private PermDetailListAdapter mPermDetailListAdapter;

	private ProgressDialog mProgressDialog;

	private AppOpsManager mAppOps;
	private PackageManager mPackageManager;

	private OpsTemplate mOtl;

	private List<AppBean> mApps;

	public NetAccessDetialsFragment(int mDataType, OpsTemplate otl) {
		super();
		this.mDataType = mDataType;
		this.mOtl = otl;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_net_manager, null);
		return mRootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mContext == null)
			mContext = getActivity().getApplicationContext();
		initialzie();
		StartLoading(mOtl);
	}

	private void initialzie() {
		mListView = (ListView) mRootView
				.findViewById(R.id.net_access_detial_list);
	}

	/**
	 * @param otl
	 */
	private void StartLoading(OpsTemplate otl) {

		mPackageManager = mContext.getPackageManager();
		mAppOps = (AppOpsManager) mContext
				.getSystemService(Context.APP_OPS_SERVICE);

		OpsLoader.getInstance().buildAppList(mContext, mAppOps,
				mPackageManager, otl, this, OpsLoader.FLAG_GET_LIST, mDataType);
	}

	@Override
	public void onListPreLoad() {
		mProgressDialog = ProgressDialog.show(getActivity(), getResources()
				.getString(R.string.dialog_title_list_loading), getResources()
				.getString(R.string.dialog_content_list_loading));
	}

	@Override
	public void onAppsListLoadFinish(List<AppBean> apps, int count) {
		mProgressDialog.dismiss();
		mPermDetailListAdapter = new PermDetailListAdapter(apps, mContext,
				mAppOps, mOtl, mDataType);
		mListView.setAdapter(mPermDetailListAdapter);
		mApps = apps;
	}

	/**
	 * @param mode
	 */
	public void applySettingsForAll(int mode) {
		NetworkControlor networkControlor = NetworkControlor.get(mContext);
		networkControlor.applyRulesForAllAsync(this, mode, mDataType, mContext);
	}

	@Override
	public void onWorkDone() {
		mProgressDialog.dismiss();
		// give a tip to user.
		Toast.makeText(getActivity(),
				getResources().getString(R.string.work_done_success),
				Toast.LENGTH_SHORT).show();
		StartLoading(mOtl);
	}

	@Override
	public void onWorkPrepare() {
		mProgressDialog = ProgressDialog.show(getActivity(), getResources()
				.getString(R.string.dialog_title_handling), getResources()
				.getString(R.string.dialog_content_handling));
	}
}
