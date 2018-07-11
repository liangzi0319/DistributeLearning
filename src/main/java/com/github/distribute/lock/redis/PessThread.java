package com.github.distribute.lock.redis;

import redis.clients.jedis.Jedis;

/**
 * �˿��߳�
 * 
 * @author linbingwen
 *
 */
public class PessThread implements Runnable {
	String key = "prdNum";// ��Ʒ����
	String clientList = "clientList";// // ��������Ʒ�Ĺ˿��б�����
	String clientName;
	Jedis jedis = null;
	RedisBasedDistributedLock redisBasedDistributedLock = null; 
	public PessThread(int num) {
		clientName = "���=" + num;
		init();
	}

	public void init() {
		jedis = RedisUtil.getInstance().getJedis();
		redisBasedDistributedLock = new RedisBasedDistributedLock(jedis, "lock.lock", 5*1000);
	}

	public void run() {
		try {
			Thread.sleep((int) (Math.random() * 5000));// ���˯��һ��
		} catch (InterruptedException e1) {
		}
		
		
		
		while (true) {
			
			//���жϻ����Ƿ�����Ʒ
			if(Integer.valueOf(jedis.get(key))<= 0) {
				break;
			}
			
			//���滹����Ʒ��ȡ������Ʒ��Ŀ��ȥ1
			System.out.println("�˿�:" + clientName + "��ʼ����Ʒ");
			
			
			if(redisBasedDistributedLock.tryLock()) {//��ȡ�ֲ�ʽ��
				
				int prdNum = Integer.valueOf(jedis.get(key)); //�ٴ�ȡ����Ʒ������Ŀ
				if (prdNum > 0) {
					jedis.decr(key);//��Ʒ����1
					jedis.sadd(clientList, clientName);// ������Ʒ��¼һ��
					System.out.println(Thread.currentThread().getName()+",�ø��ˣ��˿�:" + clientName + "������Ʒ:"+jedis.get(key));
				} else {
					System.out.println("�����ˣ����Ϊ0���˿�:" + clientName + "û��������Ʒ");
				}
				//�ͷŷֲ�ʽ��
				redisBasedDistributedLock.unlock();
				RedisUtil.returnResource(jedis);
				break;
				
			}else{
				System.out.println("���磬�˿�:" + clientName + "����δ�ܻ�ȡ�ֲ�ʽ����������----");
			}
				

			
		}
		
		
	}

}

