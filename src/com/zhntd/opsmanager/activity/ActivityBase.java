
package com.zhntd.opsmanager.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class ActivityBase extends Activity {
    public static final String LOGTAG = "com.nick.tweak.logtag";
    private final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        styleActionbar(getActionBar());
    }

    void styleActionbar(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
    
    final void LogOutput(String log) {
        if (DEBUG) Log.i(LOGTAG, log);
    }
}
