package com.jifeng.tools;
 

import android.content.Context;
import android.os.Handler;


/**
 * ���ڵ���ʱ���߳�,���ݵ�Handler���Ը���UI
 * */
public class TimeThread extends Thread {
	private boolean threadState = true;
	private Handler handler; 
	private int num;
	public TimeThread(Context context,Handler handler,int num) {
		 
		this.handler=handler;
		this.num=num;
	}

	@Override
	public void run() {
		int i=num;
		while (threadState) {
			try {
				Thread.sleep(1000);
				i--;
				handler.sendEmptyMessage(i);
				if(i==0){
					handler.sendEmptyMessage(num);
					threadState = false; 
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	  
		System.out.println("thread is over");
	}

	public void setThreadState(boolean threadState) {
		this.threadState = threadState;
	}

	public boolean isThreadState() {
		return threadState;
	}
 
}
