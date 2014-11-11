package com.zhntd.opsmanager.loader;

/**
 * @author We can also put a callback in.
 * 
 */
public abstract class ThreadPoolTask implements Runnable {

	protected String uid;
	protected String packageName;
	protected int dataType;

	/**
	 * @param uid
	 * @param packageName
	 * @param dataType
	 */
	public ThreadPoolTask(String uid, String packageName, int dataType) {
		this.uid = uid;
		this.packageName = packageName;
		this.dataType = dataType;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();
}