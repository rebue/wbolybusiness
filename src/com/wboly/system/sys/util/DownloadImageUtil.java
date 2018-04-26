package com.wboly.system.sys.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

/**
 * @Name: 使用URLConnection下载文件或图片并保存到本地。
 * @Author: nick
 */
public class DownloadImageUtil {
	
	@Test
	public void test(){
		try {
			download("http://wx.qlogo.cn/mmopen/Dd07UAibVR6CjjNbECo29SbW7NicwjzN3JU6BDG2UklWw3v0H1qrzRYryzV5q2oqSKV03AIRP2uoQOy2pbgO6skHXibZpibVPWfC/0", "toux.jpg", "D:/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* @Name: 根据图片地址保存该图片到指定路径当中
	* @Author: nick
	* @param urlstr   被下载的文件地址
	* @param filename 图片名称
	* @param savePath 保存路径
	*/
	public static void download(String urlstr, String filename, String savePath) throws Exception {
		// 构造URL
		URL url = new URL(urlstr);
		// 打开连接
		URLConnection con = url.openConnection();
		/*// 设置请求超时为10s
		con.setConnectTimeout(10 * 1000);*/
		// 输入流
		InputStream is = con.getInputStream();

		// 1K的数据缓冲
		byte[] bs = new byte[1024];
		// 读取到的数据长度
		int len;
		// 输出的文件流
		File sf = new File(savePath);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
		// 开始读取
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		// 完毕，关闭所有链接
		os.close();
		is.close();
	}

}
