package com.zhntd.opsmanager;

import java.io.Serializable;

/**
 * @author zhntd
 * @date Nov 6, 2014
 * @time 10:29:59 AM
 */
public class OpsTemplate implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 100001L;
	/* describe the category of the permission */
	private int permCategory;
	/* see more in OpsManager.java */
	private int permLabel;
	/* name str of the permission */
	private String permName;
	/* if true, this will be see as a list aheader */
	private boolean isAhead;
	/* how many apps "wanna" access this op */
	private int appsCount = -1;

	/**
	 * @param permCategory
	 * @param permLabel
	 */
	public OpsTemplate(int permCategory, int permLabel) {
		this(permCategory, permLabel, false);
	}

	/**
	 * @param permCategory
	 * @param permLabel
	 * @param isAhead
	 */
	public OpsTemplate(int permCategory, int permLabel, boolean isAhead) {
		this(permCategory, permLabel, null, isAhead);
	}

	/**
	 * @param permCategory
	 * @param permLabel
	 * @param isAhead
	 */
	public OpsTemplate(int permCategory, int permLabel, String permName,
			boolean isAhead) {
		super();
		this.permCategory = permCategory;
		this.permLabel = permLabel;
		this.permName = permName;
		this.isAhead = isAhead;
	}

	/**
	 * @return the permName
	 */
	public String getPermName() {
		return permName;
	}

	/**
	 * @param permName
	 *            the permName to set
	 */
	public void setPermName(String permName) {
		this.permName = permName;
	}

	/**
	 * @return the appsCount
	 */
	public int getAppsCount() {
		return appsCount;
	}

	/**
	 * @param appsCount
	 *            the appsCount to set
	 */
	public void setAppsCount(int appsCount) {
		this.appsCount = appsCount;
	}

	/**
	 * @return the permCategorie
	 */
	public int getPermCategorie() {
		return permCategory;
	}

	/**
	 * @return the permLabel
	 */
	public int getPermLabel() {
		return permLabel;
	}

	/**
	 * @return the isAhead
	 */
	public boolean isAhead() {
		return isAhead;
	}

}
