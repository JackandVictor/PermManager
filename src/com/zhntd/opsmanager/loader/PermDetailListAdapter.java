
package com.zhntd.opsmanager.loader;

import java.util.List;

import android.content.Context;
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
import com.zhntd.opsmanager.net.NetworkControlor;

/**
 * @author zhntd A adapter to display application related to the perm.
 */
public class PermDetailListAdapter extends BaseAdapter implements ItemSelectedListener {

    private List<AppBean> mApps;
    private ViewHolderList mViewHolder;
    private LayoutInflater mInflater;

    private AppOpsManager mAppOps;
    private OpsTemplate mOtl;
    private Context mContext;

    private NetworkControlor mNetworkControlor;

    /**
     * @param mApps
     * @param context
     */
    public PermDetailListAdapter(List<AppBean> mApps, Context context, AppOpsManager appOps,
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
            // TODO Should the root parent be null?
            convertView = mInflater.inflate(R.layout.apps_list, null);
            mViewHolder = new ViewHolderList(convertView, mContext, this, position);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolderList) convertView.getTag();
        }
        final AppBean app = mApps.get(position);
        mViewHolder.setCurrentMode(app.getMode());
        mViewHolder.titleView.setText(app.getDisplayName());
        mViewHolder.iconView.setImageDrawable(app.getDisplayIcon());

        return convertView;
    }

    @Override
    public void onSpinnerItemSelect(int item, int positionInList) {

        // For data permission control.
        if (AppOpsState.DATA_PERMISSION == mOtl.getPermLabel()) {
            if (mNetworkControlor == null) {
                mNetworkControlor = new NetworkControlor(mContext);
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
            mNetworkControlor.setCurrentMode(uid, packageName, mode);
            
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
        // Let the OpsManager to control.
        mAppOps.setMode(switchOp, appBean.getUid(),
                appBean.getPackageName(), mode);
    }
}
