package com.zhntd.opsmanager.fragment;

import java.util.List;

import com.zhntd.opsmanager.loader.PermDetailListAdapter;
import com.zhntd.opsmanager.net.DataType;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.R;
import com.zhntd.opsmanager.loader.AppBean;
import com.zhntd.opsmanager.loader.OpsLoader;
import com.zhntd.opsmanager.loader.OpsLoader.AppLoaderCallback;

public class NetAccessDetialsFragment extends Fragment implements
		OpsLoader.AppLoaderCallback {

	/* data type of this page. */
	private DataType mDataType;
	private Context mContext;

	private View mRootView;
	private ListView mListView;
	private PermDetailListAdapter mPermDetailListAdapter;

	private ProgressDialog mProgressDialog;

	private AppOpsManager mAppOps;
	private PackageManager mPackageManager;

	private OpsTemplate mOtl;

	public NetAccessDetialsFragment(DataType mDataType, OpsTemplate otl) {
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
		mProgressDialog = ProgressDialog.show(getActivity(), "Loading...",
				"Loading");
	}

	@Override
	public void onAppsListLoadFinish(List<AppBean> apps, int count) {
		mProgressDialog.dismiss();
		mPermDetailListAdapter = new PermDetailListAdapter(apps, mContext,
				mAppOps, mOtl, mDataType);
		mListView.setAdapter(mPermDetailListAdapter);
	}
}
