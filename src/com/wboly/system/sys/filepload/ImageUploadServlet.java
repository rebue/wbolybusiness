package com.wboly.system.sys.filepload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.FileUtil;
import com.wboly.system.sys.util.ImageUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.OrderKeyRandom;
import com.wboly.system.sys.util.TimeUtil;

/**
 * 文件上传数据接收类
 * 
 * @author chengqi
 *
 */
public class ImageUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/** 上传临时文件存储目录*/
	private static final String tempFolderName = SysContext.CONFIGMAP.get("goodsImgSavePath");
	
	private static final String [] extensionPermit = {"bmp", "png", "gif","jpg","jpeg","pjpeg"};
	/** 上传文件最大为500k*/ 
	private static final Long fileMaxSize = 512000L; 

	/** 统一的编码格式*/
	private static final String encode = "UTF-8";
	
	//二维码颜色
	private static final int BLACK = 0xFF000000;//0xFFFF0000，红色0xFF000000
	//二维码背景色
	private static final int WHITE = 0xFFFFFFFF;//0xFF0000FF，蓝色
	
	//二维码格式参数
	private static final EnumMap<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type=request.getParameter("imgt");
		if(type.equals("1")){//商品图片
			goodsImg(request, response);
		}else if(type.equals("2")){
			planarImage(request, response);
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void goodsImg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String callUrl=request.getParameter("callUrl");
			String tempDirectoryPath = tempFolderName;
			
			File tempDirectory = new File(tempDirectoryPath);
			//上传时产生的临时文件的默认保存目录
			DiskFileItemFactory factory = new DiskFileItemFactory();
			
			if(!tempDirectory.exists()) {
				tempDirectory.mkdir();
			}
			//重新设置临时文件保存目录
			factory.setRepository(tempDirectory);

			ServletFileUpload upload = new ServletFileUpload(factory);

			// 设置文件上传的大小限制
			upload.setFileSizeMax(fileMaxSize);
			
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			
			// 设置文件上传的头编码，如果需要正确接收中文文件路径或者文件名
			// 这里需要设置对应的字符编码，为了通用这里设置为UTF-8
			upload.setHeaderEncoding(encode);

			//解析请求数据包
			List<FileItem> fileItems = upload.parseRequest(request);
			//遍历解析完成后的Form数据和上传文件数据
			String img="";
			for (Iterator<FileItem> iterator = fileItems.iterator(); iterator.hasNext();) {
				FileItem fileItem = iterator.next();
				String name = fileItem.getName();
				//如果为上传文件数据
				if(!fileItem.isFormField()){
					if(fileItem.getSize() > 0) {
						String fileExtension = FilenameUtils.getExtension(name);
						if(!ArrayUtils.contains(extensionPermit, fileExtension)) {
							throw new NoSupportExtensionException("No Support extension.");
						}
						String fileName = FilenameUtils.getName(name);
						fileName = OrderKeyRandom.GenerateOrderNo()+fileName.substring(fileName.lastIndexOf("."));
						String folder = TimeUtil.getNowTime("yyyy/MM/dd/");
						String imagePath=tempDirectoryPath+folder+fileName;
						FileUtils.copyInputStreamToFile(fileItem.getInputStream(),new File(imagePath));
						fileItem.delete();
						
						img = folder+fileName;
						ImageUtil.resizeImage(imagePath, imagePath+"_278_278.jpg", 278, 278);
						ImageUtil.resizeImage(imagePath, imagePath+"_187_187.jpg", 187, 187);
						Map<String,Object> msg=new HashMap<String,Object>();
						msg.put("path", img);
						msg.put("path1", img+"_187_187.jpg");
						list.add(msg);
					}
				}
			}
			response.setCharacterEncoding(encode);
			response.setContentType("text/html; charset=" + encode);			
			response.addHeader("Cache-Control", "no-cache");
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Headers", "x-requested-with");
			String url = callUrl+"?msg=" + URLEncoder.encode(JsonUtil.objectToJson(list));
			response.addHeader("Location", url);
			response.sendRedirect(url); 
		} catch(FileSizeLimitExceededException e) { 
			responseMessage(response, State.OVER_FILE_LIMIT);
		} catch(NoSupportExtensionException e) { 
			responseMessage(response, State.NO_SUPPORT_EXTENSION);
		} catch(Exception e) {
			responseMessage(response, State.ERROR);
		}
	}
	
	protected void planarImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String orderUrl = request.getParameter("orderUrl");
					
			String fileName = OrderKeyRandom.GenerateOrderNo()+".jpg";
			String folder = TimeUtil.getNowTime("yyyy/MM/dd/");
			fileName = "planar/"+folder+fileName;
			JSONObject msg=new JSONObject();
			String planarImage =  SysContext.CONFIGMAP.get("goodsImgSavePath")+fileName;
			planarImage = planarImage.replace("/", "\\");
			File img = new File(planarImage);
			FileUtil.createFolder(planarImage);
			writeToFile(orderUrl, "jpg", img, 500, 500);
			msg.put("planarImg", fileName);
			response.getWriter().write(msg.toString());
		}catch(NoSupportExtensionException e) { 
			responseMessage(response, State.NO_SUPPORT_EXTENSION);
		} catch(Exception e) {
			responseMessage(response, State.ERROR);
		}
	}
	
	/**
	 * 二维码输出到文件
	 * 	@param contents 二维码内容
	 * @param format 图片格式
	 * @param file 输出文件
	 * */
	public static void writeToFile(String contents,String format,File file,int width,int height){
		BufferedImage image = encodeImg(contents,width, height);
		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
		}
	}
	
	/**
	 * 绘制二维码定制宽高
	 * @param contents
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage encodeImg(String contents,int width,int height){
		BufferedImage image = null;
		try{
			BitMatrix matrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			int w = matrix.getWidth();  
            int h = matrix.getHeight();
			for(int x = 0; x < w; x++){
				for(int y =0;y < h; y++){
					image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
				}
			}
		}catch(Exception e){
		}
		return image;
	}

	public enum State {
		OK(200, "上传成功"),
		ERROR(500, "解析失败"),
		OVER_FILE_LIMIT(501, "超过上传大小限制"),
		NO_SUPPORT_EXTENSION(502, "不支持的扩展名");

		private int code;
		private String message;
		private State(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}
		public String getMessage() {
			return message;
		}
	}

	/**
	 * 返回结果函数
	 * @param response
	 * @param state
	 */
	private void responseMessage(HttpServletResponse response, State state) {
		response.setCharacterEncoding(encode);
		response.setContentType("application/json; charset=" + encode);
		Writer writer = null;
		try {
			writer = response.getWriter();
			writer.write("{\"code\":" + state.getCode() +",\"message\":\"" + state.getMessage()+ "\"}");
			writer.flush();
			writer.close();
		} catch(Exception e) {
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	/**
	 * 返回结果函数
	 * @param response
	 * @param state
	 */
	@SuppressWarnings("unused")
	private void responseMessage(HttpServletRequest request,HttpServletResponse response, String str) {
		response.setCharacterEncoding(encode);
		response.setContentType("text/html; charset=" + encode);System.out.println("------------------------"+str);
		response.addHeader("Access-Control-Allow-Origin", "*");  
		response.addHeader("Access-Control-Allow-Methods","POST");  
		response.addHeader("Access-Control-Max-Age","1000");
		Writer writer = null;
		try {
			writer = response.getWriter();
			writer.write(str.toString());
			writer.flush();
		} catch(Exception e) {
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
}
