
package com.zhntd.opsmanager;

import java.io.Serializable;

/**
 * @author nick
 * @date Nov 6, 2014
 * @time 10:29:59 AM TODO par??
 */
public class OpsTemplate implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 100001L;
    private int permCategory;
    private int permLabel;
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
        super();
        this.permCategory = permCategory;
        this.permLabel = permLabel;
        this.isAhead = isAhead;
    }

    /**
     * @return the appsCount
     */
    public int getAppsCount() {
        return appsCount;
    }

    /**
     * @param appsCount the appsCount to set
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
