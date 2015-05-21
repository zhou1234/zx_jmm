package com.jifeng.tools;
 

import android.content.Context;
import android.os.Handler;


/**
 * 用于倒计时的线程,传递的Handler可以更新UI
 * */
public class TimeThread extends Thread {
	private boolean threadState = true;
	private Handler handler; 
	private int num;
	Context context;
	public TimeThread(Context context,Handler handler,int num) {
		 
		this.context=context;
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
