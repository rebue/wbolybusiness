package com.wboly.system.sys.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

public class AfficheCodeUtil {
	
	
	private ByteArrayInputStream image;// 图像
	private String str;// 验证

	private AfficheCodeUtil() {
		init();// 初始化属
	}

	/*
	 * 取得RandomNumUtil实例
	 */
	public static AfficheCodeUtil Instance() {
		return new AfficheCodeUtil();
	}

	/*
	 * 取得验证码图片
	 */
	public ByteArrayInputStream getImage() {
		return this.image;
	}

	/*
	 * 取得图片的验证码
	 */
	public String getString() {
		return this.str.toLowerCase();
	}

	private void init() {
		// 在内存中创建图象
		int width = 60, height = 20;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		// 获取图形上下�?
		Graphics g = image.getGraphics();
		// 生成随机�?
		Random random = new Random();
		// 设定背景�?
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		// 设定字体
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测�?
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 300; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		 String[] code = {"A","B","C","D","E","F","G","H","I","J","K"
				    ,"L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d",
				      "e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","1","2","3","4","5","6","7","8","9","0"};
		// 取随机产生的认证�?4位数�?
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			String rand = code[random.nextInt(code.length-1)];
			sRand += rand;
			// 将认证码显示到图象中
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生�?
			g.drawString(rand, 14 * i + 6, 16);
		}
		// 
		this.str = sRand;
		// 图象生效
		g.dispose();
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ImageOutputStream imageOut = ImageIO
					.createImageOutputStream(output);
			ImageIO.write(image, "JPEG", imageOut);
			imageOut.close();
			input = new ByteArrayInputStream(output.toByteArray());
		} catch (Exception e) {
			//System.out.println("验证码图片产生出现错误：" + e.toString());
		}
		this.image = input;/* 赋�?图像 */
		System.out.println(input);
	}

	/*
	 * 给定范围获得随机颜色
	 */
	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}