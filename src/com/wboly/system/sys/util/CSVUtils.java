package com.wboly.system.sys.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVUtils {
	
	
	
	/**
     * 导入 
     * @param file csv文件(路径+文件)
     * @return
     */
    public static Map<String,List<String>> importCsv(String filepath){
    	
        //List<Map<String,List<String>>> dataList=new ArrayList<Map<String,List<String>>>();
        Map<String,List<String>> map = new HashMap<String,List<String>>();
        BufferedReader br=null;
        try { 
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"GBK"));
            String line = ""; 
            int i = 0;
            while ((line = br.readLine()) != null) { 
                //dataList.add(line);
                List<String> l =fromCSVLinetoArray(line);
                
                if(i==0){
                	for (int j = 0; j < l.size(); j++) {
						
					}
                	map.put("fields", l);
                }else{                	
                	map.put(l.get(0), l);
                }
                //dataList.add(map);
                i++;
            }
            
        }catch (Exception e) {
        }finally{
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
 
        //return dataList;
        return map;
    }
    
    /** 
     * 把CSV文件的一行转换成字符串数组。不指定数组长度。 
     */  
    public static ArrayList<String> fromCSVLinetoArray(String source) {  
        if (source == null || source.length() == 0) {  
            return new ArrayList<String>();  
        }  
        int currentPosition = 0;  
        int maxPosition = source.length();  
        int nextComma = 0;  
        ArrayList<String> rtnArray = new ArrayList<String>();  
        while (currentPosition < maxPosition) {  
            nextComma = nextComma(source, currentPosition);  
            rtnArray.add(nextToken(source, currentPosition, nextComma));  
            currentPosition = nextComma + 1;  
            if (currentPosition == maxPosition) {  
                rtnArray.add("");  
            }  
        }  
        return rtnArray;  
    }  
    
    /** 
     * 查询下一个逗号的位置。 
     * 
     * @param source 文字列 
     * @param st  检索开始位置 
     * @return 下一个逗号的位置。 
     */  
    private static int nextComma(String source, int st) {  
        int maxPosition = source.length();  
        boolean inquote = false;  
        while (st < maxPosition) {  
            char ch = source.charAt(st);  
            if (!inquote && ch == ',') {  
                break;  
            } else if ('"' == ch) {  
                inquote = !inquote;  
            }  
            st++;  
        }  
        return st;  
    }  
  
    /** 
     * 取得下一个字符串 
     */  
    private static String nextToken(String source, int st, int nextComma) {  
        StringBuffer strb = new StringBuffer();  
        int next = st;  
        while (next < nextComma) {  
            char ch = source.charAt(next++);  
            if (ch == '"') {  
                if ((st + 1 < next && next < nextComma) && (source.charAt(next) == '"')) {  
                    strb.append(ch);  
                    next++;  
                }  
            } else {  
                strb.append(ch);  
            }  
        }  
        return strb.toString();  
    }  
    
    @SuppressWarnings("resource")
	public static void CSVRead(String filepath) {  
    	   
            try {  
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"GBK"));//换成你的文件名 
                reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉 
                String line = null;  
                while((line=reader.readLine())!=null){  
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分 
                      
                    String last = item[item.length-1];//这就是你要的数据了 
                    //int value = Integer.parseInt(last);//如果是数值，可以转化为数值 
                    System.out.println(last);  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
       
     
    }
    
    public static void main(String[] args) {
    	importCsv("C:\\Users\\Administrator\\Desktop\\1.csv");
	}
    
    
}
