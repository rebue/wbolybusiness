package com.wboly.system.sys.filepload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 下载文件
 * @author Sea
 *
 */
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filepath = request.getParameter("filepath");
		if (filepath!=null&&filepath.indexOf("\\")!=-1) {
			
			File file=new File(filepath);
			if(file.exists()){
				String fileName = filepath.substring(filepath.lastIndexOf("\\")+1,filepath.length());
				
				downloadFile(response,file, fileName);
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Web文件下载
	 * @author Sea
	 * @param response
	 * @param filePath
	 * @param fileName
	 */
	public void downloadFile(HttpServletResponse response,File file,String fileName){
		try {
			fileName=new String(fileName.getBytes("GBK"), "ISO8859-1");
			BufferedInputStream bis = null;  
			BufferedOutputStream bos = null;  
			long fileLength = file.length();  
			response.setContentType("application/octet-stream");  
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);  
			response.setHeader("Content-Length", String.valueOf(fileLength));  
  
			bis = new BufferedInputStream(new FileInputStream(file));  
			bos = new BufferedOutputStream(response.getOutputStream());  
			byte[] buff = new byte[2048];  
			int bytesRead;  
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
			    bos.write(buff, 0, bytesRead);  
			}  
			bos.flush();
			bos.close();
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	public static void main(String[]args){
		String filepath="D:\\myeclipseproject\\vbldc\\src\\main\\webapp\\upload\\201509210937098261771326870\\wuye.apk";
		String fileName = filepath.substring(filepath.lastIndexOf("\\")+1,filepath.length());
		System.out.println(fileName);
	}
}
