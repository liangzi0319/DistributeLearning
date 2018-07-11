package com.github.distribute.lock.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;

public class DistributeRedisUtil2 {
	
	public static void main(String[] args) {
		
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		int clientNum = 30;// 模拟客户数目
		for (int i = 0; i < clientNum; i++) {
			cachedThreadPool.execute(new PessThread(i));
		}
		cachedThreadPool.shutdown();

		while (true) {
			if (cachedThreadPool.isTerminated()) {
				System.out.println("所有的线程都结束了！");
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