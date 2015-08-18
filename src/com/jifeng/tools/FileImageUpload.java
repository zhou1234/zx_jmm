package com.jifeng.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FileImageUpload {

	private static String end = "\r\n";
	private static String twoHyphens = "--";
	private static String boundary = "*****";

	// 普通字符串数据
	@SuppressWarnings("deprecation")
	private static void writeStringParams(Map<String, String> params,
			DataOutputStream ds) throws Exception {
		Set<String> keySet = params.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			String value = params.get(name);
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name
					+ "\"" + end);
			ds.writeBytes(end);
			ds.writeBytes(URLEncoder.encode(value) + end);
		}
	}

	// 文件数据
	@SuppressWarnings("deprecation")
	private static void writeFileParams(Map<String, File> params,
			DataOutputStream ds) throws Exception {
		Set<String> keySet = params.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			File value = params.get(name);
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name
					+ "\"; filename=\"" + URLEncoder.encode(value.getName())
					+ "\"" + end);
			ds.writeBytes("Content-Type: " + getContentType(value) + end);
			ds.writeBytes(end);
			ds.write(getBytes(value));
			ds.writeBytes(end);
		}
	}

	// 获取文件的上传类型，图片格式为image/png,image/jpg等。非图片为application/octet-stream
	private static String getContentType(File f) throws Exception {
		return "application/octet-stream";
	}

	// 把文件转换成字节数组
	private static byte[] getBytes(File f) throws Exception {
		FileInputStream in = new FileInputStream(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = in.read(b)) != -1) {
			out.write(b, 0, n);
		}
		in.close();
		return out.toByteArray();
	}

	// 添加结尾数据
	private static void paramsEnd(DataOutputStream ds) throws Exception {
		ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
		ds.writeBytes(end);
	}

	public static String upUserBitmap(String seversString, String path,
			String id) {
		try {
			URL url = new URL(seversString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");

			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			HashMap<String, File> fileMap = new HashMap<String, File>();
			fileMap.put("path", new File(path));
			writeFileParams(fileMap, ds);

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("UserId", id);
			writeStringParams(params, ds);

			paramsEnd(ds);

			ds.flush();
			/* 取得Response内容 */

			InputStream is = con.getInputStream();

			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/*
			 * // 关闭DataOutputStream
			 */
			ds.close();
			return b.toString().trim();

		} catch (Exception e) {
			return "";
		}
	}

	public static String upShaiDanBitmap(String seversString, String path,
			String id, String content, String tag) {
		try {
			URL url = new URL(seversString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");

			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			HashMap<String, File> fileMap = new HashMap<String, File>();
			fileMap.put("path", new File(path));
			writeFileParams(fileMap, ds);

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("UserId", id);
			params.put("Content", content);
			params.put("Tag", tag);
			writeStringParams(params, ds);

			paramsEnd(ds);

			ds.flush();
			/* 取得Response内容 */

			InputStream is = con.getInputStream();

			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/*
			 * // 关闭DataOutputStream
			 */
			ds.close();
			return b.toString().trim();

		} catch (Exception e) {
			return "";
		}

	}

}
