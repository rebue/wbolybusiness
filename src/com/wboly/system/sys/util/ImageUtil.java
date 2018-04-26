package com.wboly.system.sys.util;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageUtil {

	/**
	 * @author Sea
	 * 改变图片的宽高
	 * @return
	 */
	public static void  resizeImage(String imagePath,String iamgeSave,int newwidth,int newheight)throws Exception{

		BufferedImage image = null; 


		File file = new File(imagePath);  
		if (file.exists()) {  

			BufferedImage im = ImageIO.read(file);

			/* 调整后的图片的宽度和高度 */  
			int toWidth = 0;  
			int toHeight= 0;

			/* 原始图像的宽度和高度 */
			int Width = im.getWidth();
			int Height = im.getHeight();
			
			Double width = (double) Width;
			Double height = (double) Height;
			Double newWidth = (double) newwidth;
			Double newHeight = (double) newheight;
			Double share1 = (double) (width / newWidth);
			Double share2 = (double) (height / newHeight);
			if (share1 > share2) {
				toWidth = (int) (width / share1);
				toHeight = (int) (height / share1);
			} else {
				toWidth = (int) (width / share2);
				toHeight = (int) (height / share2);
			}
			/* 新生成结果图片 */  
			image = new BufferedImage(toWidth, toHeight,  
					BufferedImage.TYPE_INT_RGB); 

			image.getGraphics().drawImage(  
					im.getScaledInstance(toWidth, toHeight,  
							java.awt.Image.SCALE_SMOOTH), 0, 0, null); 



			File savefile=new File(iamgeSave);
			ImageIO.write(image, "JPEG", savefile);

		}

	};
	
	
	/**
	 * @author Sea
	 * 改变图片的宽高
	 * @return
	 */
	public static void  resizeImageByWidth(String imagePath,String iamgeSave,double newWidth)throws Exception{

		BufferedImage image = null; 


		File file = new File(imagePath);  
		if (file.exists()) {  

			BufferedImage im = ImageIO.read(file);

			/* 调整后的图片的宽度和高度 */  
			int toWidth = 0;  
			int toHeight= 0;

			/* 原始图像的宽度和高度 */
			int Width = im.getWidth();
			int Height = im.getHeight();
			
			double width = (double) Width;
			double height = (double) Height;
			double newHeight = height/width*newWidth;
			
			toWidth=(int) newWidth;
			toHeight=(int) newHeight;
			
			/* 新生成结果图片 */  
			image = new BufferedImage(toWidth, toHeight,  
					BufferedImage.TYPE_INT_RGB); 

			image.getGraphics().drawImage(  
					im.getScaledInstance(toWidth, toHeight,  
							java.awt.Image.SCALE_SMOOTH), 0, 0, null); 

			File savefile=new File(iamgeSave);
			ImageIO.write(image, "JPEG", savefile);

		}

	};
	
	
	/**
	 * @author Sea
	 * 改变图片的宽高
	 * @return
	 */
	public static void  resizeImageByHeight(String imagePath,String iamgeSave,double newHeight)throws Exception{

		BufferedImage image = null; 


		File file = new File(imagePath);  
		if (file.exists()) {  

			BufferedImage im = ImageIO.read(file);

			/* 调整后的图片的宽度和高度 */  
			int toWidth = 0;  
			int toHeight= 0;

			/* 原始图像的宽度和高度 */
			int Width = im.getWidth();
			int Height = im.getHeight();
			
			double width = (double) Width;
			double height = (double) Height;
			double newWidth = width/height*newHeight;
			
			toWidth=(int) newWidth;
			toHeight=(int) newHeight;
			
			/* 新生成结果图片 */  
			image = new BufferedImage(toWidth, toHeight,  
					BufferedImage.TYPE_INT_RGB); 

			image.getGraphics().drawImage(  
					im.getScaledInstance(toWidth, toHeight,  
							java.awt.Image.SCALE_SMOOTH), 0, 0, null); 

			File savefile=new File(iamgeSave);
			ImageIO.write(image, "JPEG", savefile);

		}

	};



	/**
	 * @author Sea
	 * 改变图片的宽高
	 * @return
	 */
	public static void resizeImage(String imagePath,String iamgeSave,Double share)throws Exception{

		BufferedImage image = null; 


		File file = new File(imagePath);  
		if (file.exists()) {  


			BufferedImage im = ImageIO.read(file);

			/* 原始图像的宽度和高度 */  
			int width = im.getWidth();  
			int height = im.getHeight();

			/* 调整后的图片的宽度和高度 */  
			int toWidth = (int) (width*share);  
			int toHeight= (int) (height*share);



			/* 新生成结果图片 */  
			image = new BufferedImage(toWidth, toHeight,  
					BufferedImage.TYPE_INT_RGB); 

			image.getGraphics().drawImage(  
					im.getScaledInstance(toWidth, toHeight,  
							java.awt.Image.SCALE_SMOOTH), 0, 0, null); 


			File savefile=new File(iamgeSave);
			ImageIO.write(image, "JPEG", savefile);
		}
	};


	
	public static void main(String [] args)throws Exception{
		BufferedImage bi = ImageIO.read(new File("D:/vbldc.sql"));

		if(bi == null){ 
		    System.out.println("此文件不为图片文件！");

		}else{
			System.out.println("是图片！");
		}
		
		
		
	}



}
