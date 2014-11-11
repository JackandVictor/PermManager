package com.zhntd.opsmanager.loader;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.R.integer;
import android.app.AppOpsManager;

import com.zhntd.opsmanager.AppOpsState;
import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.R;
import com.zhntd.opsmanager.loader.ViewHolderList.ItemSelectedListener;
import com.zhntd.opsmanager.net.DataType;
import com.zhntd.opsmanager.net.NetworkControlor;
import com.zhntd.opsmanager.utils.Logger;

/**
 * @author zhntd A adapter to display application related to the perm.
 */
public class PermDetailListAdapter extends BaseAdapter implements
		ItemSelectedListener {

	private List<AppBean> mApps;
	private LayoutInflater mInflater;

	private AppOpsManager mAppOps;
	private OpsTemplate mOtl;
	private Context mContext;

	private NetworkControlor mNetworkControlor;

	/* for net manager */
	private DataType mDataType;

	/**
	 * @param mApps
	 * @param context
	 * @param appOps
	 * @param otl
	 */
	public PermDetailListAdapter(List<AppBean> mApps, Context context,
			AppOpsManager appOps, OpsTemplate otl) {
		this.mApps = mApps;
		this.mContext = context;
		this.mAppOps = appOps;
		this.mOtl = otl;
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * @param mApps
	 * @param context
	 * @param appOps
	 * @param otl
	 * @param dataType
	 */
	public PermDetailListAdapter(List<AppBean> mApps, Context context,
			AppOpsManager appOps, OpsTemplate otl, DataType dataType) {
		this(mApps, context, appOps, otl);
		this.mDataType = dataType;
	}

	@Override
	public int getCount() {
		if (mApps == null)
			return 0;
		return mApps.size();
	}

	@Override
	public Object getItem(int position) {
		return mApps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolderList viewHolder;
		final AppBean app = mApps.get(position);
		if (convertView == null) {
			// TODO Should the root parent be null?
			convertView = mInflater.inflate(R.layout.apps_list_item, null);
			viewHolder = new ViewHolderList(app, convertView, mContext, this);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolderList) convertView.getTag();
		}
		// update spinner selection.
		viewHolder.setCurrentMode(app.getMode());
		// important! do not forget to update position for holder.
		viewHolder.setPositionInList(position);
		// app name.
		viewHolder.titleView.setText(app.getDisplayName());
		// icon here.
		viewHolder.iconView.setImageDrawable(app.getDisplayIcon());

		return convertView;
	}

	@Override
	public void onSpinnerItemSelect(ViewHolderList viewHolder, int item,
			int positionInList) {

		if (positionInList < 0) {
			Logger.logger("Error, Did you call set list position in viewholder?");
			return;
		}

		// For data permission control.
		if (AppOpsState.DATA_PERMISSION == mOtl.getPermLabel()) {
			if (mNetworkControlor == null) {
				mNetworkControlor = NetworkControlor.prepare(mContext);
			}
			int mode = NetworkControlor.MODE_ASK;
			switch (item) {
			case 0:
				mode = NetworkControlor.MODE_ASK;
				break;

			case 1:
				mode = NetworkControlor.MODE_DENIED;
				break;

			case 2:
				mode = NetworkControlor.MODE_ALLOWED;
				break;

			default:
				mode = NetworkControlor.MODE_ASK;
				break;
			}
			// It is ready to save something.
			final AppBean appBean = mApps.get(positionInList);
			final int uid = appBean.getUid();
			final String packageName = appBean.getPackageName();
			// Do not forget to update mode to this app in the list(data).
			appBean.setMode(mode);
			mNetworkControlor.setCurrentModeAsync(uid, packageName, mode,
					mDataType, mContext);
			return;
		}

		// Other permissions, OpsManager can give full control.
		final int switchOp = AppOpsManager.opToSwitch(mOtl.getPermLabel());
		final AppBean appBean = mApps.get(positionInList);
		// default mode is to ask.
		int mode = AppOpsManager.MODE_ASK;

		// Do a ugly check...
		switch (item) {
		case 0:
			mode = AppOpsManager.MODE_ASK;
			break;

		case 1:
			mode = AppOpsManager.MODE_IGNORED;
			break;

		case 2:
			mode = AppOpsManager.MODE_ALLOWED;
			break;

		default:
			mode = AppOpsManager.MODE_ASK;
			break;
		}
		// Let OpsManager to control.
		mAppOps.setMode(switchOp, appBean.getUid(), appBean.getPackageName(),
				mode);
		appBean.setMode(mode);
	}
}
