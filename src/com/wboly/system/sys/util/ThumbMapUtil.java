package com.wboly.system.sys.util;

public class ThumbMapUtil {
	
	
	  /**
	   * 获取文件的后缀包含"."号
	   * @param imageUrl--图片路径
	   * @return
	   */
	  public static String GetFileExtName(String imageUrl)
      {	  
		  String ext = "";
          if(imageUrl!=null&&!"".equals(imageUrl)){
        	  ext =  imageUrl.substring(imageUrl.lastIndexOf("."));
          }
          
          return ext;
      }
	  
	  
	  /**
	   * 获取晒单缩略图
	   * @param imageUrl--图片路径
	   * @param size--缩略尺寸
	   * @return
	   */
	  public static String getShareOrderThumb(String imageUrl,int size){
		  String newImageUrl=imageUrl.substring(0,imageUrl.lastIndexOf("."));
		  if(imageUrl!=null&&!"".equals(imageUrl)){
			  String prefix = GetFileExtName(imageUrl);
			  newImageUrl += String.format("_W%s%s",size,prefix);
		  }	
		  return newImageUrl;
	  }
	  
	  /**
	   * 获取商品缩略图(都是正方形)
	   * @param imageUrl--图片路径
	   * @param size--缩略尺寸
	   * @return
	   */
	  public static String getGoodsThumb(String imageUrl,int size){
		  String newImageUrl="";
		  if(imageUrl!=null&&!"".equals(imageUrl)){
			  String prefix = GetFileExtName(imageUrl);
			  newImageUrl += String.format(imageUrl+"_%s_%s%s",size,size,prefix);
		  }
		  return newImageUrl;
	  }
	  
	  
	  public static void main(String[] args) {
		  System.out.println(getShareOrderThumb("2016/02/02/1542353174302.jpg", 215));
	  }


}
