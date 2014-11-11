package com.zhntd.opsmanager.loader;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhntd.opsmanager.AppOpsState;
import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.R;
import com.zhntd.opsmanager.net.NetworkControlor.DataType;

import android.app.AppOpsManager;

/**
 * @author zhntd Permission summary list adapter
 */
public class SummaryListAdapter extends BaseAdapter {

	private List<OpsTemplate> mOpsList;
	private String[] mOpsLabels;
	private String[] mOpsCates;
	private LayoutInflater mInflater;

	private PackageManager mPackageManager;
	private AppOpsManager mAppOps;
	private Context ctx;

	/**
	 * @param mOpsList
	 * @param opsCates
	 * @param opsLabels
	 * @param mContext
	 */
	public SummaryListAdapter(List<OpsTemplate> opsList, String[] opsCate,
			String[] opsLabels, Context context) {
		this.mOpsList = opsList;
		this.mOpsLabels = opsLabels;
		this.mOpsCates = opsCate;
		this.ctx = context;
		init(context);
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * @param ctx
	 */
	private void init(Context ctx) {
		mPackageManager = ctx.getPackageManager();
		mAppOps = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
	}

	@Override
	public int getCount() {
		if (mOpsList == null)
			return 0;
		return mOpsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mOpsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * place holder
	 */
	final static class ViewHolder {
		TextView lebelTextView;
		TextView countTextView;
		TextView headerTextView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.ops_list_item, null);
			viewHolder.lebelTextView = (TextView) convertView
					.findViewById(R.id.ops_label);
			viewHolder.countTextView = (TextView) convertView
					.findViewById(R.id.ops_count);
			viewHolder.headerTextView = (TextView) convertView
					.findViewById(R.id.ops_cate);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.lebelTextView.setText(mOpsLabels[position]);
		final OpsTemplate otl = mOpsList.get(position);
		// chk if we need to show a head title for this item.
		if (otl.isAhead()) {
			viewHolder.headerTextView.setText(mOpsCates[mOpsList.get(position)
					.getPermCategorie()]);
			viewHolder.headerTextView.setVisibility(View.VISIBLE);
		} else {
			viewHolder.headerTextView.setVisibility(View.GONE);
		}

		/* load the count in a loader async.. */
		if (otl.getPermLabel() == AppOpsState.DATA_PERMISSION) {
			OpsLoader.getInstance().buildAppList(ctx, mAppOps, mPackageManager,
					otl, new OpsLoader.AppLoaderCallback() {

						@Override
						public void onAppsListLoadFinish(List<AppBean> apps,
								int count) {
							// TODO make it in res.
							if (viewHolder.countTextView != null)
								viewHolder.countTextView.setText(count + "");
							// should update the count now.
							if (otl != null)
								otl.setAppsCount(count);
						}

						@Override
						public void onListPreLoad() {
							// TODO make it in res
							if (viewHolder.countTextView == null)
								return;
							if (otl.getAppsCount() >= 0) {
								// if we already has a count, no need to load.
								viewHolder.countTextView.setText(otl
										.getAppsCount() + "");
							} else {
								viewHolder.countTextView.setText("loading");
							}
						}
					}, OpsLoader.FLAG_GET_COUNT, DataType.MOBILE);
		} else {
			OpsLoader.getInstance().buildAppList(ctx, mAppOps, mPackageManager,
					otl, new OpsLoader.AppLoaderCallback() {

						@Override
						public void onAppsListLoadFinish(List<AppBean> apps,
								int count) {
							// TODO make it in res.
							if (viewHolder.countTextView != null)
								viewHolder.countTextView.setText(count + "");
							// should update the count now.
							if (otl != null)
								otl.setAppsCount(count);
						}

						@Override
						public void onListPreLoad() {
							// TODO make it in res
							if (viewHolder.countTextView == null)
								return;
							if (otl.getAppsCount() >= 0) {
								// if we already has a count, no need to load.
								viewHolder.countTextView.setText(otl
										.getAppsCount() + "");
							} else {
								viewHolder.countTextView.setText("loading");
							}
						}
					}, OpsLoader.FLAG_GET_COUNT);
		}

		return convertView;
	}

}
