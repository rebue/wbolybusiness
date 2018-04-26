package com.wboly.modules.controller.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.thrift.TException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.render.ReadRender;
import com.alibaba.simpleimage.render.ScaleParameter;
import com.alibaba.simpleimage.render.ScaleRender;
import com.alibaba.simpleimage.render.WriteRender;
import com.google.gson.Gson;
import com.wboly.system.sys.filepload.NoSupportExtensionException;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.OrderKeyRandom;
import com.wboly.system.sys.util.TimeUtil;

@Controller
public class FileUtilController extends SysController {

	/** 统一的编码格式 */
	private static final String encode = "UTF-8";

	private static final String[] extensionPermit = { "BMP", "PNG", "GIF", "JPG", "JPEG", "PJPEG" };

	@RequestMapping(value = "/app/Util/fileUpload", method = RequestMethod.POST)
	public void FileUpoad(HttpServletRequest request, HttpServletResponse response) throws TException {
		try {
			if (getFileUploadPath(request, response).equals("")) {
				return;
			}
			String tempDirectoryPath = getFileUploadPath(request, response);

			// 转型为 MultipartHttpRequest
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

			String flag = request.getParameter("flag");

			// 获取文件
			MultipartFile file = null;

			if (flag != null && !flag.equals("")) {
				// 获取文件的名称
				Iterator<String> iter = multipartRequest.getFileNames();

				while (iter.hasNext()) {
					// 获取文件
					file = multipartRequest.getFile(iter.next());
				}
			} else {
				file = multipartRequest.getFile("file_upload");
			}

			// 获取文件名
			String name = file.getOriginalFilename();

			// 获取文件的扩展名 ,不包括 '.'
			String fileExtension = FilenameUtils.getExtension(name);

			// 判断文件是否支持 已有的后缀名
			if (!ArrayUtils.contains(extensionPermit, fileExtension.toUpperCase())) {
				throw new NoSupportExtensionException("No Support extension.");
			}

			File tempDirectory = new File(tempDirectoryPath);
			// 上传时产生的临时文件的默认保存目录
			DiskFileItemFactory factory = new DiskFileItemFactory();

			// 判断文件夹是否存在,不存在创建
			if (!tempDirectory.exists()) {
				tempDirectory.setWritable(true, false);    //设置写权限，windows下不用此语句
				tempDirectory.mkdir();
			}
			// 重新设置临时文件保存目录
			factory.setRepository(tempDirectory);

			Map<String, Object> msg = new HashMap<String, Object>();

			// 获取文件的名称
			String fileName = FilenameUtils.getName(name);

			// 生成一个 新的文件名称
			fileName = OrderKeyRandom.GenerateOrderNo() + "." + fileExtension;

			String folder = TimeUtil.getNowTime("yyyy/MM/dd/");

			// 图片的路径地址
			String imagePath = tempDirectoryPath + folder + fileName;

			// 图片的地址
			String img = folder + fileName;

			// 上传图片 文件输入流 图片路径
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(imagePath));

			// 生成文件缩略图
			if (!request.getParameter("configName").equals("feedbackPath")) {
				ImgCompress(imagePath, imagePath + "_720_720." + fileExtension, 720, 720);
				ImgCompress(imagePath, imagePath + "_135_135." + fileExtension, 135, 135);
			}

			msg.put("path", img);
			msg.put("path1", img + "_135_135." + fileExtension);

			response.setCharacterEncoding(encode);
			String message = new Gson().toJson(msg).toString();
			response.getWriter().write(message);
		} catch (NoSupportExtensionException e) {
			e.printStackTrace();
			responseMessage(response, State.NO_SUPPORT_EXTENSION);
		} catch (Exception e) {
			e.printStackTrace();
			responseMessage(response, State.ERROR);
		}
	}

	@RequestMapping("/app/files/upload")
	public void addFile(HttpServletRequest request, HttpServletResponse response)
			throws IllegalStateException, IOException {
		String type = request.getParameter("configName");
		String usersId = request.getParameter("usersId");
		String tempDirectoryPath = "";
		if (type.equals("1")) {
			tempDirectoryPath = SysContext.CONFIGMAP.get("appraiseSavePath");
		} else if (type.equals("2")) {
			tempDirectoryPath = SysContext.CONFIGMAP.get("returnGoodsSavePath");
		} else if (type.equals("3")) {
			tempDirectoryPath = SysContext.CONFIGMAP.get("feedbackPath");
		} else {
			responseMessage(response, State.PARAM_ERROR);
		}
		String target = "Rate:" + usersId;
		String uid = request.getParameter("uid");// 获取uid
		String pid = request.getParameter("pid");// 获取jsp id参数
		System.out.println(uid);
		System.out.println(pid);
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file != null) {
					String myFileName = file.getOriginalFilename();
					if (myFileName.trim() != "") {
						String fileName = file.getOriginalFilename();
						String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
						SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/");
						String newFileName = df.format(new Date());
						String fileNames = newFileName + OrderKeyRandom.GenerateOrderNo() + "." + fileExt;
						String path = tempDirectoryPath + fileNames;// 上传路径
						String result = RediscaCheUtil.getKey(target);
						List<String> formatList = JsonUtil.formatList(result);
						if (formatList == null || formatList.size() == 0) {
							List<String> l = new ArrayList<String>();
							l.add(fileNames);
							Gson gson = new Gson();
							RediscaCheUtil.setKey(target, gson.toJson(l));
						} else {
							formatList = JsonUtil.formatList(result);
							formatList.add(fileNames);
							Gson gson = new Gson();
							RediscaCheUtil.setKey(target, gson.toJson(formatList));
						}
						File localFile = new File(path);
						if (!localFile.exists()) {// 如果文件夹不存在，自动创建
							localFile.mkdirs();
						}
						file.transferTo(localFile);

						try {
							ImgCompress(path, path + "_720_720." + fileExt, 720, 720);
							ImgCompress(path, path + "_135_135." + fileExt, 135, 135);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * @Name: 获取上传得文件目录
	 * @Author: nick
	 */
	private String getFileUploadPath(HttpServletRequest request, HttpServletResponse response) {
		String tempDirectoryPath = "";
		String configName = request.getParameter("configName");

		if (configName != null && !"".equals(configName)) {
			if (configName.equals("returnGoodsSavePath")) {
				tempDirectoryPath = SysContext.IMGPATH + "return/";
			}
		} else {
			responseMessage(response, State.PARAM_ERROR);

		}

		if (tempDirectoryPath.equals("") || tempDirectoryPath == null) {
			responseMessage(response, State.PARAM_ERROR);

		}
		return tempDirectoryPath;
	}

	/**
	 * 返回结果函数
	 * 
	 * @param response
	 * @param state
	 */
	private void responseMessage(HttpServletResponse response, State state) {
		response.setCharacterEncoding(encode);
		response.setContentType("application/json; charset=" + encode);
		Writer writer = null;
		try {
			writer = response.getWriter();
			writer.write("{\"code\":" + state.getCode() + ",\"message\":\"" + state.getMessage() + "\"}");
			writer.flush();
			writer.close();
		} catch (Exception e) {

		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public enum State {
		OK(200, "上传成功"), ERROR(500, "解析失败"), OVER_FILE_LIMIT(501, "上传文件超过上传大小500k限制"), NO_SUPPORT_EXTENSION(502,
				"不支持上传文件的扩展名"), PARAM_ERROR(503, "不支持上传文件的扩展名");

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

	private void ImgCompress(String oldPath, String newPath, int w, int h) {
		File in = new File(oldPath); // 原图片
		File out = new File(newPath); // 目的图片
		ScaleParameter scaleParam = new ScaleParameter(400, 400); // 将图像缩略到1024x1024以内，不足1024x1024则不做任何处理

		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		WriteRender wr = null;
		try {
			inStream = new FileInputStream(in);
			outStream = new FileOutputStream(out);
			ImageRender rr = new ReadRender(inStream);
			ImageRender sr = new ScaleRender(rr, scaleParam);
			wr = new WriteRender(sr, outStream);
			wr.render(); // 触发图像处理
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inStream); // 图片文件输入输出流必须记得关闭
			IOUtils.closeQuietly(outStream);

			if (wr != null) {
				try {
					wr.dispose(); // 释放simpleImage的内部资源
				} catch (SimpleImageException ignore) {

				}
			}
		}
	}
}
