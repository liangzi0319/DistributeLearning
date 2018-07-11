package com.github.distribute.lock.redis;

import redis.clients.jedis.Jedis;

/**
 * 顾客线程
 * 
 * @author linbingwen
 *
 */
public class PessThread implements Runnable {
	String key = "prdNum";// 商品主键
	String clientList = "clientList";// // 抢购到商品的顾客列表主键
	String clientName;
	Jedis jedis = null;
	RedisBasedDistributedLock redisBasedDistributedLock = null; 
	public PessThread(int num) {
		clientName = "编号=" + num;
		init();
	}

	public void init() {
		jedis = RedisUtil.getInstance().getJedis();
		redisBasedDistributedLock = new RedisBasedDistributedLock(jedis, "lock.lock", 5*1000);
	}

	public void run() {
		try {
			Thread.sleep((int) (Math.random() * 5000));// 随机睡眠一下
		} catch (InterruptedException e1) {
		}
		
		
		
		while (true) {
			
			//先判断缓存是否有商品
			if(Integer.valueOf(jedis.get(key))<= 0) {
				break;
			}
			
			//缓存还有商品，取锁，商品数目减去1
			System.out.println("顾客:" + clientName + "开始抢商品");
			
			
			if(redisBasedDistributedLock.tryLock()) {//获取分布式锁
				
				int prdNum = Integer.valueOf(jedis.get(key)); //再次取得商品缓存数目
				if (prdNum > 0) {
					jedis.decr(key);//商品数减1
					jedis.sadd(clientList, clientName);// 抢到商品记录一下
					System.out.println(Thread.currentThread().getName()+",好高兴，顾客:" + clientName + "抢到商品:"+jedis.get(key));
				} else {
					System.out.println("悲剧了，库存为0，顾客:" + clientName + "没有抢到商品");
				}
				//释放分布式锁
				redisBasedDistributedLock.unlock();
				RedisUtil.returnResource(jedis);
				break;
				
			}else{
				System.out.println("悲剧，顾客:" + clientName + "本次未能获取分布式锁，接着抢----");
			}
				

			
		}
		
		
	}

}

