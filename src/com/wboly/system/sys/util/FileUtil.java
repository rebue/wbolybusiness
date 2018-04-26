package com.wboly.system.sys.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author
 *
 */
public class FileUtil {

	/**
	 * 查找所有文件
	 * 
	 * @author Sea
	 * @param filePath
	 * @throws Exception
	 */
	public static List<String> findAllFile(String filePath) {
		allFilePath = new ArrayList<String>();
		if (filePath == null && "".equals(filePath)) {
			return null;
		}
		scanFile(filePath);
		return allFilePath;
	}

	/**
	 * 查找文件夹下所有文件不包含子文件夹
	 * 
	 * @author Sea
	 * @param filePath
	 * @throws Exception
	 */
	public static List<String> findFiles(String filePath) {
		File[] fs = new File(filePath).listFiles();
		for (int i = 0; i < fs.length; i++) {
			System.out.println(fs[i].getAbsolutePath());
			if (fs[i].isDirectory()) {
				try {
					allFilePath.add(fs[i].getName());
				} catch (Exception e) {

				}
			}
		}
		return allFilePath;
	}

	private static List<String> allFilePath;

	public static void scanFile(String filePath) {

		File file = new File(filePath);

		for (String f : file.list()) {
			File files = new File(filePath + "/" + f);
			if (files.isDirectory()) {
				scanFile(filePath + "/" + f);
			} else if (!files.isDirectory()) {

				allFilePath.add(filePath + "/" + f);
			}
		}

	}

	/**
	 * Web下载文件
	 * 
	 * @author Sea
	 * @param response
	 * @param filePath
	 * @param fileName
	 */
	// public static void download(HttpServletResponse response,InputStream
	// inputStream){
	//
	// InputStream is=null;
	// OutputStream os = null;
	// try {
	// is=inputStream;
	// os=response.getOutputStream();
	// byte[] b=new byte[1024];
	// int length;
	// while((length=is.read(b))>0){
	// os.write(b,0,length);
	// }
	// } catch (UnsupportedEncodingException e) {
	//
	// e.printStackTrace();
	// } catch (FileNotFoundException e) {
	//
	// e.printStackTrace();
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// }finally{
	//
	//
	// if(os!=null){
	// try {
	// os.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// if(is!=null){
	// try {
	// is.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	//
	//
	// }
	// }

	/**
	 * Web文件下载
	 * 
	 * @author shi wu jian
	 * @param response
	 * @param filePath
	 * @param fileName(要加属性，例如：a.txt)
	 */
	public static void download(HttpServletResponse response, String path, String fileName) throws Exception {

		BufferedInputStream fis = null;
		BufferedOutputStream toClient = null;
		try {
			File file = new File(path);
			if (file.exists()) {

				response.setContentType("text/html;charset=UTF-8");
				response.setContentType("application/octet-stream");
				// response.setContentType("application/vnd.android.package-archive");//app
				response.addHeader("Content-Disposition",
						"attachment;filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
				response.addHeader("Content-Length", "" + file.length());
				toClient = new BufferedOutputStream(response.getOutputStream());
				fis = new BufferedInputStream(new FileInputStream(file));

				byte[] buffer = new byte[1024];
				int i = -1;

				while ((i = fis.read(buffer)) != -1) {
					toClient.write(buffer, 0, i);
				}
			}

		} catch (Exception ex) {
			System.gc();
		} finally {
			if (fis != null) {
				fis.close();
			}

			if (toClient != null) {
				toClient.close();
			}
		}

	}

	/**
	 * Web文件下载
	 * 
	 * @author Sea
	 * @param response
	 * @param filePath
	 * @param fileName(要加属性，例如：a.txt)
	 */
	// public static void download(HttpServletResponse response,String
	// filePath,String filename)throws Exception{
	//
	// BufferedInputStream bis = null;
	// BufferedOutputStream bos = null;
	// try {
	//
	// File file=new File(filePath);
	//
	// response.setContentType("text/html;charset=UTF-8");
	// long fileLength = file.length();
	// response.setContentType("application/octet-stream");
	// response.setHeader("Content-disposition", "attachment; filename=" +new
	// String(filename.getBytes("GBK"), "ISO8859-1"));
	// response.setHeader("Content-Length", String.valueOf(fileLength));
	//
	//
	// bis = new BufferedInputStream(new FileInputStream(file));
	// bos = new BufferedOutputStream(response.getOutputStream());
	// byte[] buff = new byte[1024];
	// int bytesRead;
	// while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
	// bos.write(buff, 0, bytesRead);
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }finally{
	// System.out.println("44444444444444");
	// if(bis!=null){
	// bis.close();
	// }
	//
	// if(bos!=null){
	// bos.close();
	// }
	// System.out.println("4444666666666666664444444444");
	//
	// }
	//
	//
	// }

	/**
	 * Web文件下载
	 * 
	 * @author Sea
	 * @param response
	 * @param filePath
	 * @param fileName(要加属性，例如：a.txt)
	 */
	public static void download(HttpServletResponse response, String filePath) throws Exception {

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {

			String fileName = filePath.substring(filePath.lastIndexOf("\\"), filePath.length());
			fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
			File file = new File(filePath);
			response.setContentType("text/html;charset=UTF-8");
			long fileLength = file.length();
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			response.setHeader("Content-Length", String.valueOf(fileLength));

			bis = new BufferedInputStream(new FileInputStream(file));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[1024];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}

		} catch (Exception e) {

		} finally {

			if (bis != null) {
				bis.close();
			}

			if (bos != null) {
				bos.close();
			}

		}

	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 */
	public static void delete(String filePath) {
		File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			file.delete();
		}

	}

	/**
	 * 创建文件夹
	 * 
	 * @author Sea
	 * @param path
	 */
	public static void createFolder(String filePath) {
		File file = new File(filePath);
		if (-1 != filePath.indexOf(".")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("\\"));
			file = new File(filePath);
		}
		if (!file.exists()) {
			// 创建文件夹
			file.mkdirs();
		}

	}

	/**
	 * @Name: 读取文件内容
	 * @Author: nick
	 */
	public static String readTxtFile(String filePath, String encoding) {
		String content = "";
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 读取文件
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					content += lineTxt + System.lineSeparator();
				}
				read.close();
			} else {
				System.err.println("文件不存在");
			}
		} catch (Exception e) {
			System.err.println("读取文件异常...");
			e.printStackTrace();
		}
		return content;
	}
}
