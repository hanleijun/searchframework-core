package cn.focus.dc.focussearch.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadUtil {

	/**
	 * 初始化线程池
	 */
	public static ScheduledExecutorService NORMAL_THREAD_EXECUTOR = new ScheduledThreadPoolExecutor(50,
			new BasicThreadFactory.Builder().namingPattern("focus-search-pool-%d").daemon(true).build());
}
