
package com.zhntd.opsmanager.loader;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.app.AppOpsManager;
import com.zhntd.opsmanager.OpsTemplate;
import com.zhntd.opsmanager.R;
import com.zhntd.opsmanager.loader.ViewHolderList.ItemSelectedListener;

public class OpsDetailListAdapter extends BaseAdapter implements ItemSelectedListener {

    private List<AppBean> mApps;
    private ViewHolderList mViewHolder;
    private LayoutInflater mInflater;

    private AppOpsManager mAppOps;
    private OpsTemplate mOtl;
    private Context mContext;

    /**
     * @param mApps
     * @param context
     */
    public OpsDetailListAdapter(List<AppBean> mApps, Context context, AppOpsManager appOps,
            OpsTemplate otl) {
        this.mApps = mApps;
        this.mContext = context;
        this.mAppOps = appOps;
        this.mOtl = otl;
        mInflater = LayoutInflater.from(context);

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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.apps_list, null);
            mViewHolder = new ViewHolderList(convertView, mContext, this, position);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolderList) convertView.getTag();
        }
        AppBean app = mApps.get(position);
        mViewHolder.setCurrentMode(app.getMode());
        mViewHolder.titleView.setText(app.getDisplayName());
        mViewHolder.iconView.setImageDrawable(app.getDisplayIcon());

        return convertView;
    }

    @Override
    public void onSpinnerItemSelect(int item, int positionInList) {
        // deny?
        final int switchOp = AppOpsManager.opToSwitch(mOtl.getPermLabel());
        final AppBean appBean = mApps.get(positionInList);
        int mode = AppOpsManager.MODE_ASK;

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
                break;
        }

        mAppOps.setMode(switchOp, appBean.getUid(),
                appBean.getPackageName(), mode);
    }
}
