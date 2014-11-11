package com.zhntd.opsmanager.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * A base styled activity.
 * 
 * @author zhntd
 */
public abstract class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		styleActionbar(getActionBar());
	}

	/**
	 * Follow the Settings style.
	 * 
	 * @param actionBar
	 */
	private void styleActionbar(ActionBar actionBar) {
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
	}

	/**
	 * Set title of the action bar
	 * 
	 * @param title
	 */
	protected void setTitle(String title) {
		getActionBar().setTitle(title);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

	public abstract void cleanUp();
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanUp();
	}
}
