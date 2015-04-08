package com.jifeng.tools;

import java.util.Stack;

import android.app.Activity;

public class TasckActivity {
	private static Stack<Activity> activityStack;
	private static TasckActivity instance;

	public TasckActivity() {
	}

	public static TasckActivity getScreenManager() {
		if (instance == null) {
			instance = new TasckActivity();
		}
		return instance;
	}

	public void popActivity() {
		Activity activity = activityStack.lastElement();
		if (activity != null) {
			activity.finish();
			activity = null;
		}
	}
	  //退出栈顶Activity
	public void popActivity(Activity activity) {
		if (activity != null) {
			   //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}
	 //获得当前栈顶Activity
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}
	   //将当前Activity推入栈中
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	 //退出栈中所有Activity
	public void popAllActivityExceptOne(Class cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}
}
