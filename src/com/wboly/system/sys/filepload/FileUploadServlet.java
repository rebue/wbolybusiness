package com.wboly.system.sys.filepload;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.ImageUtil;
import com.wboly.system.sys.util.OrderKeyRandom;
import com.wboly.system.sys.util.TimeUtil;

public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		//this.servletContext = config.getServletContext();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获取客户端回调函数名
		response.setContentType("text/html;charset=UTF-8");
		defaultProcessFileUpload(request, response);
		if("onerror".equals(request.getParameter("testcase")))
		throw new IOException();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 生成保存上传文件的磁盘路径
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFilePath(String drive,String ext) {
		String filepath=TimeUtil.getNowTime("yyyy/MM/dd/")+OrderKeyRandom.GenerateOrderNo()+ ext;
		String path=drive+filepath;
		createFolder(path);
		return filepath;
	}
	
	private void defaultProcessFileUpload(HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream stream = null;
		BufferedOutputStream bos = null;
		
		String path="";
		try {
			String configName=request.getParameter("configName");
			if(configName!=null){
				String configValue=SysContext.CONFIGMAP.get(configName);
				
				if(configValue!=null){
					ServletFileUpload upload = new ServletFileUpload();
					upload.setHeaderEncoding("UTF-8");
					
					JSONObject job=new JSONObject();
					
					if (ServletFileUpload.isMultipartContent(request)) {
						FileItemIterator iter = upload.getItemIterator(request);
						while (iter.hasNext()) {
							FileItemStream item = iter.next();
							stream = item.openStream();
							if (!item.isFormField()) {

								// 得到文件的扩展名(无扩展名时将得到全名)
								String ext = item.getName().substring(item.getName().lastIndexOf("."));
								String filepath= getFilePath(configValue,ext);
								path= configValue+filepath;
										
								File file=	new File(path);
								bos = new BufferedOutputStream(new FileOutputStream(file));
								byte[] imgBufTemp = new byte[102401];
								int length;
								while ((length = stream.read(imgBufTemp)) != -1) {
									bos.write(imgBufTemp, 0, length);
								}
								
								job.put("drive", configValue);
								job.put("filepath",filepath);
							}
						}
					}
					
					if (stream != null) {
						stream.close();
					}
					if (bos != null) {
						bos.close();
					}
					
					BufferedImage im = ImageIO.read(new File(path));
					/* 原始图像的宽度和高度 */
					int width = im.getWidth();
					int height = im.getHeight();
					
					job.put("width", width);
					job.put("height",height);
					
					response.getWriter().write(job.toString());
				}else{
					response.getWriter().write("{}");
				}
			}else{
				response.getWriter().write("{}");
			}
			if("orderImgSavePath".equals(configName)){
				 
				String savefilepath=path.substring(0,path.lastIndexOf("."))+"_W215.jpg";
				ImageUtil.resizeImageByWidth(path, savefilepath, 215);
			}
		} catch (Exception e) {
			response.getWriter().write("{}");
		}finally {
			if (stream != null) {
				stream.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}
	
	/**
	 * 创建文件夹
	 * @author Sea
	 * @param path
	 */
	public  void createFolder(String filePath){	
		filePath=filePath.replace("/", "\\");
		File file=null;
		if(-1!=filePath.indexOf(".")){ 
        	filePath=filePath.substring(0,filePath.lastIndexOf("\\"));
        	file=new File(filePath);
         }
		if (!file.exists()) {  
			//创建文件夹
			 file.mkdirs();  
        }
	}
}
