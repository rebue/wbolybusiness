package com.wboly.system.sys.filepload;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.wboly.system.sys.pojo.Messge;

/**
 * 删除文件
 * @author Sea
 *
 */
public class FileDeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public  String DEFAULT_CONTENT_TYPE="text/plain;charset=UTF-8";
	protected final Log log =  LogFactory.getLog(FileDeleteServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String error="";
		response.setContentType("text/html;charset=UTF-8");
		String filepaths=request.getParameter("filepaths");
		if(filepaths!=null){
			String [] filePaths=filepaths.split(",");
			for(String filepath:filePaths){
				File file=new File(filepath);
				if(file.isFile() && file.exists()) {
					Boolean bool=file.delete();
					if(bool==false){
						error+=filepath+",";
					}
				}else{
					error+=filepath+",";
				}
			}
			if(error.length()>1){
				render(response,new Messge( false,"系统提示",error));
			}else{
				render(response,new Messge( true,"系统提示","文件删除成功！"));
			}
		}else{
			render(response,new Messge( false,"系统提示","文件路径为空！"));
		}	
	}
	
	protected void render(HttpServletResponse response,Object object){
		JSONObject jsonObject = new JSONObject(object,true);
		try {
			response.setContentType(DEFAULT_CONTENT_TYPE);
			response.getWriter().write(jsonObject.toString());
		} catch (IOException e) {
			this.log.error(e);
		}
	}
}
