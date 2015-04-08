package com.jifeng.tools;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DaoJiShi {
	long between;
	TextView textView1, textView2, textView3,textView4;
	LinearLayout layout;
	public DaoJiShi(long between,TextView textView1,TextView textView2,TextView textView3,TextView textView4,LinearLayout layout){
		this.between=between;
		this.textView1=textView1;
		this.textView2=textView2;
		this.textView3=textView3;
		this.textView4=textView4;
		this.layout=layout;
	}
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 获取所有数据
			case 0x01:
				if (between > 0) {
					long day = between / (24 * 3600);
					//textView1.setText(String.valueOf(day));
					long hour = between / 3600 - day * 24;
					textView1.setText(String.valueOf(hour));
					long minute = between / 60 - day * 24 * 60 - hour * 60;
					textView2.setText(String.valueOf(minute));
					long second = between - day * 24 * 3600 - hour * 3600
							- minute * 60;
					textView3.setText(String
							.valueOf(second));
					between = between - 1;
					handler.sendEmptyMessageDelayed(0x01, 1000);
				} else {
					handler.removeMessages(0x01);
					layout.setVisibility(View.GONE);
					textView4.setVisibility(View.VISIBLE); 
				}
				break;
			}
		}
	}; 
	public void sendMessage(){
		handler.sendEmptyMessage(0x01);
	}
}
