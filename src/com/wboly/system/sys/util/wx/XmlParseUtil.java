package com.wboly.system.sys.util.wx;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
* @Name: xml 解析 工具
* @Author: knick
*/
public class XmlParseUtil {

	

	/**
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map doXMLParse(String strxml){
		try {
			if (null == strxml || "".equals(strxml)) {
				return null;
			}

			Map m = new HashMap();
			
			InputStream in = String2Inputstream(strxml);
			
			SAXBuilder builder = new SAXBuilder();
			
			Document doc = builder.build(in);
			
			Element root = doc.getRootElement();
			
			List list = root.getChildren();
			
			Iterator it = list.iterator();
			
			while (it.hasNext()) {
				
				Element e = (Element) it.next();
				
				String k = e.getName();
				
				String v = "";
				
				List children = e.getChildren();
				
				if (children.isEmpty()) {
					
					v = e.getTextNormalize();
				} else {
					
					v = getChildrenText(children);
				}

				m.put(k, v);
			}

			// 关闭流
			in.close();

			return m;
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取子结点的xml
	 * 
	 * @param children
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	public static String getChildrenText(List children) {
		
		StringBuffer sb = new StringBuffer();
		
		if (!children.isEmpty()) {
			
			Iterator it = children.iterator();
			
			while (it.hasNext()) {
				
				Element e = (Element) it.next();
				
				String name = e.getName();
				
				String value = e.getTextNormalize();
				
				List list = e.getChildren();
				
				sb.append("<" + name + ">");
				
				if (!list.isEmpty()) {
					
					sb.append(getChildrenText(list));
				}
				
				sb.append(value);
				
				sb.append("</" + name + ">");
			}
		}

		return sb.toString();
	}
	
	
	/**
	 * 解析返回的值
	 * @param is
	 * @param charset
	 * @return
	 */
	public static String getContent(InputStream is, String charset) {
		
		String pageString = null;
		
		InputStreamReader isr = null;
		
		BufferedReader br = null;
		
		StringBuffer sb = null;
		
		try {
			
			isr = new InputStreamReader(is, charset);
			
			br = new BufferedReader(isr);
			
			sb = new StringBuffer();
			
			String line = null;
			
			while ((line = br.readLine()) != null) {
				
				sb.append(line + "\n");
			}
			
			pageString = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null){
					is.close();
				}
				if(isr!=null){
					isr.close();
				}
				if(br!=null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			sb = null;
		}
		return pageString;
	}
	
	
	public static InputStream String2Inputstream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}
	
	
}
