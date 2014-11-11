package com.zhntd.opsmanager.activity;

import com.zhntd.opsmanager.net.NetworkControlor;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.zhntd.opsmanager.R;

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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.net_manager, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			finish();
			break;

		case R.id.action_ask_all:
			applySettingsForAll(NetworkControlor.MODE_ASK);
			break;
		case R.id.action_deny_all:
			applySettingsForAll(NetworkControlor.MODE_DENIED);
			break;
		case R.id.action_allow_all:
			applySettingsForAll(NetworkControlor.MODE_ALLOWED);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public abstract void cleanUp();

	public abstract void applySettingsForAll(int mode);

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanUp();
	}
}
