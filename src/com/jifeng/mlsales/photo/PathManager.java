package com.jifeng.mlsales.photo;

import java.io.File;

/**
 * Created by sreay on 14-8-18.
 */
public class PathManager {
	//�Զ�������洢·����ͼƬ�������ú��ͼƬ������640*640��
	public static File getCropPhotoPath() {
		File photoFile = new File(getCropPhotoDir().getAbsolutePath() + System.currentTimeMillis() + ".jpeg");
		return photoFile;
	}

	//�洢���ú��ͼƬ���ļ���
	public static File getCropPhotoDir() {
		String path = FileUtil.getRootPath() + "/nimei/crop/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
}
