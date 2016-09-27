package org.telegram.socialuser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecuter {

	private static BackgroundExecuter mInstance;
	private ExecutorService executorService;

	private BackgroundExecuter() {
		executorService = Executors.newFixedThreadPool(5);
	}

	public static BackgroundExecuter getInstance() {
		if (mInstance == null) {
			mInstance = new BackgroundExecuter();
		}
		return mInstance;
	}

	public void execute(Runnable runnable) {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(5);
		}
		executorService.submit(runnable);
	}
}
