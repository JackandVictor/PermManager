package com.zhntd.opsmanager.loader;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {

	/** size of the pool */
	private int poolSize;
	private static final int MIN_POOL_SIZE = 1;
	private static final int MAX_POOL_SIZE = 10;

	/** pool */
	private ExecutorService threadPool;

	/** list */
	private LinkedList<ThreadPoolTask> asyncTasks;

	/** how it works */
	private int type;
	public static final int TYPE_FIFO = 0;
	public static final int TYPE_LIFO = 1;

	/** thread */
	private Thread poolThread;
	/** time */
	private static final int SLEEP_TIME = 200;

	public ThreadPoolManager(int type, int poolSize) {
		this.type = (type == TYPE_FIFO) ? TYPE_FIFO : TYPE_LIFO;

		if (poolSize < MIN_POOL_SIZE)
			poolSize = MIN_POOL_SIZE;
		if (poolSize > MAX_POOL_SIZE)
			poolSize = MAX_POOL_SIZE;
		this.poolSize = poolSize;

		threadPool = Executors.newFixedThreadPool(this.poolSize);

		asyncTasks = new LinkedList<ThreadPoolTask>();
	}

	/**
	 * add task.
	 * 
	 * @param task
	 */
	public void addAsyncTask(ThreadPoolTask task) {
		synchronized (asyncTasks) {
			asyncTasks.addLast(task);
		}
	}

	/**
	 * get task.
	 * 
	 * @return
	 */
	private ThreadPoolTask getAsyncTask() {
		synchronized (asyncTasks) {
			if (asyncTasks.size() > 0) {
				ThreadPoolTask task = (this.type == TYPE_FIFO) ? asyncTasks
						.removeFirst() : asyncTasks.removeLast();
				return task;
			}
		}
		return null;
	}

	/**
	 * start loop.
	 * 
	 * @return
	 */
	public void start() {
		if (poolThread == null) {
			poolThread = new Thread(new PoolRunnable());
			poolThread.start();
		}
	}

	/**
	 * the end.
	 */
	public void stop() {
		poolThread.interrupt();
		poolThread = null;
	}

	/**
	 * runnable to start loop.
	 */
	private class PoolRunnable implements Runnable {

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					ThreadPoolTask task = getAsyncTask();
					if (task == null) {
						try {
							Thread.sleep(SLEEP_TIME);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
						continue;
					}
					threadPool.execute(task);
				}
			} finally {
				threadPool.shutdown();
			}
		}
	}

}