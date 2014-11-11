package com.zhntd.opsmanager.loader;

import com.zhntd.opsmanager.net.DataType;

/**
 * @author We can also put a callback in.
 * 
 */
public abstract class ThreadPoolTask implements Runnable {

	protected String uid;
	protected String packageName;
	protected DataType dataType;

	/**
	 * @param uid
	 * @param packageName
	 * @param dataType
	 */
	public ThreadPoolTask(String uid, String packageName, DataType dataType) {
		this.uid = uid;
		this.packageName = packageName;
		this.dataType = dataType;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();
}