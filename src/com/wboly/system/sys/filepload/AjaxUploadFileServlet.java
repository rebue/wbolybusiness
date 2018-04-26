package com.wboly.system.sys.filepload;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件上传数据接收类
 * 
 * @author chengqi
 *
 */
public class AjaxUploadFileServlet extends HttpServlet {

	/** 日志对象*/
	private Log logger = LogFactory.getLog(this.getClass());

	private static final long serialVersionUID = 1L;

	/** 上传临时文件存储目录*/
	private static final String tempFolderName = "tempFiles";
	
	/** 上传文件最大为500k*/ 
	private static final Long fileMaxSize1 = 500000L; 

	
	/** 允许上传图片的扩展名*/
	private static final String [] extensionPermit1 = {"csv","bmp", "png", "gif", "jpg", "jpeg", "pjpeg"};

	/** 统一的编码格式*/
	private static final String encode = "UTF-8";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("UploadFileServlet#doPost() start");
		try {
			String curProjectPath = this.getServletContext().getRealPath("/");
			String tempDirectoryPath = curProjectPath + "/" + tempFolderName;
			File tempDirectory = new File(tempDirectoryPath);
			logger.debug("Project real path [" + tempDirectory.getAbsolutePath() + "]");
			//上传时产生的临时文件的默认保存目录
			logger.debug("Temp files default save path [" + System.getProperty("java.io.tmpdir") + "]");
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//DiskFileItemFactory中DEFAULT_SIZE_THRESHOLD=10240表示如果上传文件大于10K则会产生上传临时文件
			//上传临时文件的默认目录为java.io.tmpdir中保存的路径，根据操作系统的不同会有区别
			if(!tempDirectory.exists()) {
				tempDirectory.mkdir();
			}
			//重新设置临时文件保存目录
			factory.setRepository(tempDirectory);

			//设置文件清除追踪器，文件上传过程中产生的临时文件会在
			FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(this.getServletContext());
			factory.setFileCleaningTracker(fileCleaningTracker);

			ServletFileUpload upload = new ServletFileUpload(factory);

			// 设置文件上传的大小限制
			upload.setFileSizeMax(fileMaxSize1);

			// 设置文件上传的头编码，如果需要正确接收中文文件路径或者文件名
			// 这里需要设置对应的字符编码，为了通用这里设置为UTF-8
			upload.setHeaderEncoding(encode);

			//解析请求数据包
			List<FileItem> fileItems = upload.parseRequest(request);
			//遍历解析完成后的Form数据和上传文件数据
			for (Iterator<FileItem> iterator = fileItems.iterator(); iterator.hasNext();) {
				FileItem fileItem = iterator.next();
				String name = fileItem.getName();
				//如果为上传文件数据
				if(!fileItem.isFormField()) {
					if(fileItem.getSize() > 0) {
						String fileExtension = FilenameUtils.getExtension(name);
						if(!ArrayUtils.contains(extensionPermit1, fileExtension)) {
							throw new NoSupportExtensionException("No Support extension.");
						}
						String fileName = FilenameUtils.getName(name);
						FileUtils.copyInputStreamToFile(fileItem.getInputStream(), 
								new File(tempDirectory, fileName));
					}
				} else { //Form表单数据
					//String value = fileItem.getString(encode);
				}
			}
			responseMessage(response, State.OK);
		} catch(FileSizeLimitExceededException e) { 
			responseMessage(response, State.OVER_FILE_LIMIT);
		} catch(NoSupportExtensionException e) { 
			responseMessage(response, State.NO_SUPPORT_EXTENSION);
		} catch(Exception e) {
			responseMessage(response, State.ERROR);
		}
	}

	public enum State {
		OK(200, "上传成功"),
		ERROR(500, "上传失败"),
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
		response.setContentType("text/html; charset=" + encode);
		Writer writer = null;
		try {
			writer = response.getWriter();
			writer.write("{\"code\":" + state.getCode() +",\"message\":\"" + state.getMessage()+ "\"}");
			writer.flush();
			writer.close();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
}
