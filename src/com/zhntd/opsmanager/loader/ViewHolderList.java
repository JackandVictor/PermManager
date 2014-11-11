package com.zhntd.opsmanager.loader;

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
import com.zhntd.opsmanager.net.DataType;
import com.zhntd.opsmanager.net.NetworkControlor;
import com.zhntd.opsmanager.utils.Logger;

import android.R.integer;
import android.app.AppOpsManager;

/**
 * @author zhntd
 * @date Nov 6, 2014
 * @time 2:11:22 PM
 */
public class ViewHolderList {

	public TextView titleView;
	public ImageView iconView;
	public Spinner spinner;
	/* position in listview */
	private int positionInList = -1;
	private int currentSpinnerPosision = -1;

	public ViewHolderList(AppBean app, View convertView, Context c,
			final ItemSelectedListener callback) {
		// find views.
		titleView = (TextView) convertView.findViewById(R.id.title);
		iconView = (ImageView) convertView.findViewById(R.id.icon);
		spinner = (Spinner) convertView.findViewById(R.id.spinner);

		final String[] mStringArray = c.getResources().getStringArray(
				R.array.app_ops_mode);
		final ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(c,
				android.R.layout.simple_spinner_item, mStringArray);

		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(mAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				/*
				 * only if the position changed, that means the click is frm a
				 * user.
				 */
				if (currentSpinnerPosision != position) {
					callback.onSpinnerItemSelect(ViewHolderList.this, position,
							positionInList);
					Logger.logger("Spinner: User changed the mode, starttring call back...");
				} else {
					Logger.logger("Spinner: the position has not change, will not call back...");
				}
				setCurrentSpinnerPosision(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// NOTHING to do.
			}
		});
	}

	/**
	 * @param mode
	 *            current mode.
	 */
	public void setCurrentMode(int mode) {
		int selection = 0;
		// The mode(integer) defines in OpsManager and NetworkControlor are
		// totally the same, So we ignore the type of the perm.
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
			selection = 0;
			break;
		}
		if (spinner != null) {
		}
		// update current position in spinner.
		setCurrentSpinnerPosision(selection);
		spinner.setSelection(selection);
	}

	/**
	 * Should be call in adapter.
	 * 
	 * @param positionInList
	 */
	public void setPositionInList(int positionInList) {
		this.positionInList = positionInList;
	}

	/**
	 * @return current position in spinner.
	 */
	public int getCurrentSpinnerPosision() {
		return currentSpinnerPosision;
	}

	/**
	 * @param currentSpinnerPosision
	 */
	public void setCurrentSpinnerPosision(int currentSpinnerPosision) {
		this.currentSpinnerPosision = currentSpinnerPosision;
	}

	/**
	 * @author Nick Should be implement.
	 */
	public interface ItemSelectedListener {
		void onSpinnerItemSelect(ViewHolderList viewHolder, int item,
				int positionInList);
	}
}
