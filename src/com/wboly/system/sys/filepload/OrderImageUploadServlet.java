package com.wboly.system.sys.filepload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderImageUploadServlet extends HttpServlet{
	
	private static final long serialVersionUID = 2791492764425910513L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 获取客户端回调函数名
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");  
		String path = "e:/orderuploads";
		File file = new File(path);
		
		if (!file.exists()) {  
		    file.mkdir();  
		}else{ 
			//DiskFileItemFactory：创建 FileItem 对象的工厂，在这个工厂类中可以配置内存缓冲区大小和存放临时文件的目录。
			DiskFileItemFactory factory = new DiskFileItemFactory();  
			factory.setRepository(new File(path));
			factory.setSizeThreshold(1024 * 1024);   // maximum size that will be stored in memory 
			ServletFileUpload sfu = new ServletFileUpload(factory);  // the location for saving data that is larger than getSizeThreshold()
			try {
				List<FileItem> _cache=sfu.parseRequest(request);  
				for(FileItem item :_cache) {  
					if(!item.isFormField()) {  //是否为input="type"输入域  
						String name=item.getFieldName();
						String filedValue=item.getName(); //上传文件的名称和完整路径  
						
						int start=filedValue.lastIndexOf("\\"); 
						int start1=filedValue.lastIndexOf("."); 
						
						String value=filedValue.substring(start+1);
						String ext=filedValue.substring(start1+1);  
						request.setAttribute(name, value);
						OutputStream os=new FileOutputStream(new File(path,value));  
						
						InputStream iis=item.getInputStream();
						BufferedImage bi=ImageIO.read(iis);
						
						int width=bi.getWidth();  
						int height=bi.getHeight();  
						
						JSONObject job=new JSONObject();
						job.put("width", width);
						job.put("height", height);
						
						System.out.println(">>>>"+job.toString());
						System.out.println(">>>>"+ext);
						
						iis.close();
						
						InputStream is=item.getInputStream();  
						byte[] buffer=new byte[500];  
						int length=0;   
						while((length=is.read(buffer))!=-1) {  
							os.write(buffer, 0, length);  
						}
						
						response.getWriter().write(job.toString());
						os.close();  
						is.close();
					}
				}
			} catch (FileUploadException e) {
				// TODO: handle exception
				System.out.println(e.getMessage());  
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
}







