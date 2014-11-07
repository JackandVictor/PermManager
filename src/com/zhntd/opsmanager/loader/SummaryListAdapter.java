
package com.zhntd.opsmanager.loader;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.R;

import android.app.AppOpsManager;

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
    public SummaryListAdapter(List<OpsTemplate> opsList, String[] opsCate, String[] opsLabels,
            Context context) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = mInflater.inflate(R.layout.ops_list_item, null);
        TextView lebelTextView = (TextView) rootView.findViewById(R.id.ops_label);
        lebelTextView.setText(mOpsLabels[position]);
        final TextView countTextView = (TextView) rootView.findViewById(R.id.ops_count);
        TextView headerTextView = (TextView) rootView.findViewById(R.id.ops_cate);

        final OpsTemplate otl = mOpsList.get(position);
        if (otl.isAhead()) {
            headerTextView.setText(mOpsCates[mOpsList.get(position).getPermCategorie()]);
            headerTextView.setVisibility(View.VISIBLE);
        } else {
            headerTextView.setVisibility(View.GONE);
        }

        /* load the count in a loader async.. */
        OpsLoader.getInstance().buildAppList(ctx, mAppOps, mPackageManager, otl,
                new OpsLoader.AppLoaderCallback() {

                    @Override
                    public void onAppsListLoadFinish(List<AppBean> apps, int count) {
                        // TODO make it in res.
                        if (countTextView != null)
                            countTextView.setText(count + "");
                        // should update the count now.
                        if (otl != null)
                            otl.setAppsCount(count);
                    }

                }, OpsLoader.FLAG_GET_COUNT);

        return rootView;
    }

}
