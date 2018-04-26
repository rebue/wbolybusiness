package com.wboly.system.sys.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.wboly.system.sys.system.SysContext;

/**
 * 读取系统所有配置文件
 * 
 * @author Sea
 *
 */
public class PropertiesUtil {

	/**
	 * 读取单个properties文件
	 * 
	 * @param classpath
	 * @return
	 */
	public static Map<String, String> getProperties(String filepath) {

		Map<String, String> map = new HashMap<String, String>();

		try {
			// 获得类加载器，然后把文件作为一个流获取
			InputStream in = new FileInputStream(new File(filepath));
			// 创建Properties实例
			Properties prop = new Properties();
			// 将Properties和流关联
			prop.load(in);
			// 获取所有的名称
			Enumeration<?> allName = prop.propertyNames();
			// 遍历
			while (allName.hasMoreElements()) {
				// 获得每一个名称
				String key = (String) allName.nextElement();
				// 利用已得到的名称通过Properties对象获得相应的值
				String value = (String) prop.get(key);
				map.put(key, value);
			}
			// 关闭资源
			in.close();
			return map;
		} catch (IOException e) {
			// System.out.println("================读取properties文件出错！================");
			return null;
		}

	}

	/**
	 * 获取所有的配置文件信息
	 * 
	 * @return Map<String , Map<String ,String>>
	 */
	public static Map<String, Map<String, String>> getAllProperties() {

		if (SysContext.PROPERTIES == null) {

			String filepath = PropertiesUtil.class.getResource("/").getPath();
			// filepath = filepath.substring(1, filepath.indexOf("WEB-INF") - 1).replaceAll("%20", " ");
			System.out.println(filepath);
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			// 获取路径下的所有properties文件
			List<String> list = FileUtil.findAllFile(filepath);
			for (String f : list) {
				if (f.indexOf(".properties") > 0) {
					String filename = f.substring(f.lastIndexOf("/") + 1, f.length());
					Map<String, String> properties = getProperties(f);
					map.put(filename, properties);

				}
			}
			SysContext.PROPERTIES = map;

		}

		return SysContext.PROPERTIES;

	}

	/**
	 * 获取Properties的值
	 * 
	 * @param propertiesName
	 * @param key
	 * @return Object
	 */
	public static Object getPropertiesValue(String propertiesName, String key) {
		return PropertiesUtil.getAllProperties().get(propertiesName).get(key);

	}

	/*
	 * public static void main(String[] args){
	 * 
	 * Map<String , Map<String ,String>> map= getAllProperties();
	 * 
	 * Iterator it = map.keySet().iterator(); while(it.hasNext()){ String key =
	 * (String) it.next(); System.out.println(key); } }
	 */

}
