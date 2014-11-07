
package com.zhntd.opsmanager.loader;

import android.R.integer;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.zhntd.opsmanager.R;

import android.app.AppOpsManager;

/**
 * @author nick
 * @date Nov 6, 2014
 * @time 2:11:22 PM TODO fill out.
 */
public class ViewHolderList {

    public TextView titleView;
    public ImageView iconView;
    public Spinner spinner;

    private ArrayAdapter<String> mAdapter;
    private String[] mStringArray;

    public ViewHolderList(View convertView, Context c, final ItemSelectedListener callback, final int positionInList) {
        titleView = (TextView) convertView.findViewById(R.id.title);
        iconView = (ImageView) convertView.findViewById(R.id.icon);
        spinner = (Spinner) convertView.findViewById(R.id.spinner);

        mStringArray = c.getResources().getStringArray(R.array.app_ops_mode);
        mAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item,
                mStringArray);

        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callback.onSpinnerItemSelect(position, positionInList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // EMPTY
            }
        });
    }

    public void setCurrentMode(int mode) {
        int selection = 0;
        switch (mode) {
            case AppOpsManager.MODE_ASK:
                selection = 0;
                break;
                
            case AppOpsManager.MODE_IGNORED:
                selection = 1;
                break;
                
            case AppOpsManager.MODE_ALLOWED:
                selection = 2;
                break;

            default:
                break;
        }
        spinner.setSelection(selection);
    }

    public interface ItemSelectedListener {
        void onSpinnerItemSelect(int item, int positionInList);
    }
}
