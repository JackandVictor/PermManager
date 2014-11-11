package com.zhntd.opsmanager.loader;

import android.graphics.drawable.Drawable;

/**
 * Describe an application with some tags.
 * 
 * @author zhntd
 */
public class AppBean {

	private String displayName;
	private Drawable displayIcon;
	private String appSummary;
	private int uid;
	private String packageName;
	/* ALLOWED/DENIED/ASK */
	private int currentMode;

	public AppBean() {
		// EMPTY we use getter and setter.
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return currentMode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(int mode) {
		this.currentMode = mode;
	}

	/**
	 * @param packageName
	 *            the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the displayIcon
	 */
	public Drawable getDisplayIcon() {
		return displayIcon;
	}

	/**
	 * @param displayIcon
	 *            the displayIcon to set
	 */
	public void setDisplayIcon(Drawable displayIcon) {
		this.displayIcon = displayIcon;
	}

	/**
	 * @return the appSummary
	 */
	public String getAppSummary() {
		return appSummary;
	}

	/**
	 * @param appSummary
	 *            the appSummary to set
	 */
	public void setAppSummary(String appSummary) {
		this.appSummary = appSummary;
	}

	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}
}
