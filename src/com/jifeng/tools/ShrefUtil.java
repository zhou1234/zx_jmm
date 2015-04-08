package com.jifeng.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 对底层SharedPreference（持久层数据操作的工具包）
 * 工具：必须进行测试
 *
 */
@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
public class ShrefUtil {
	
	//文件名不需要加.xml后缀
	
	public static final String fileName="data";
	private SharedPreferences shref=null;
	/**
	 * 创建.xml文件
	 * 构造方法直接创建
	 * 创建持久层的xml,文件，数据库需要上下文环境
	 */
	@SuppressWarnings("deprecation")
	public ShrefUtil(Context context,String fileName){
		/*
		 * 如果文件名是不存在的就会create创建新的，并且获得editor编辑器打开文件
		 * 如果文件存在直接返回编辑器，并打开文件
		 * 1.创建或者打开的文件名
		 * 2.模式(安全性考虑)
		 */
		
		shref=context.getSharedPreferences(fileName, Context.MODE_PRIVATE+Context.MODE_WORLD_READABLE
												+Context.MODE_WORLD_WRITEABLE);
	}
	/**
	 * 将数据写入文件
	 * @param key String类型
	 * @param value String类型
	 */
	public void write(String key,String value){
		//1.现获取编辑器editor
		//通过edit()方法获得编辑器接口
		SharedPreferences.Editor editor=shref.edit();
		
		//2.写入数据
		editor.putString(key, value);
		
		//3.提交数据(真正的写入文件)
		editor.commit();
	}
	//方法重载
	/**
	 * @param key String类型
	 * @param value String类型
	 */
	public void write(String key,int value){
		//1.现获取编辑器editor
		//通过edit()方法获得编辑器接口
		SharedPreferences.Editor editor=shref.edit();
		
		//2.写入数据
		editor.putInt(key, value);
		
		//3.提交数据(真正的写入文件)
		editor.commit();
	}
	
	
	/**
	 * 将数据从文件取出
	 * @param key String类型    根据key读取value
	 * @return 
	 * 
	 * @return String类型
	 */
	public String readString(String key){
		//直接通过shref读取（如果没有key返回第二个参数的默认值）
		return shref.getString(key, null);
	}
	//重载
	public int readInt(String key){
		//直接通过shref读取（如果没有key返回第二个参数的默认值）
		return shref.getInt(key, -1);
	}
}
