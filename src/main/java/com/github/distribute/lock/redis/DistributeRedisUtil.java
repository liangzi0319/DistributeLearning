package com.github.distribute.lock.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributeRedisUtil {
	
	public static void main(String[] args) {
		
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		int clientNum = 30;// ģ��ͻ���Ŀ
		for (int i = 0; i < clientNum; i++) {
			cachedThreadPool.execute(new PessThread(i));
		}
		cachedThreadPool.shutdown();

		while (true) {
			if (cachedThreadPool.isTerminated()) {
				System.out.println("���е��̶߳������ˣ�");
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}


