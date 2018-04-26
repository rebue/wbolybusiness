package com.wboly.system.sys.util;

public class Base64EnOut {

	private static final String  EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final int[]  DecodeChars=new int[]{
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1,
        63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
        32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
        50, 51, -1, -1, -1, -1, -1
	};
	
	/**
	 * 编码
	 * @param text
	 * @return
	 */
	public static String Base64Encode(String text){
		int i=0;
        int len=text.length();
        int c1;
        int c2;
        int c3;
        
        char[] base64EncodeChars=EncodeChars.toCharArray();
        char[] c = text.toCharArray();
        
        StringBuffer sb=new StringBuffer(50);
          
        while(i<len){
            c1=((int)c[i++]) & 0xff;

            if(i==len){
                sb.append(base64EncodeChars[c1 >>2]);
                sb.append(base64EncodeChars[(c1 & 0x3) << 4]);
                sb.append( "==");
                 break;
            }
            c2 = ((int)c[i++]);
            if (i == len) {
                sb.append(base64EncodeChars[c1 >>2]);
                sb.append(base64EncodeChars[((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4)]);
                sb.append(base64EncodeChars[(c2 & 0xF) << 2]);
                sb.append( "=");
                break;
            }
             c3 =((int)c[i++]);
                sb.append(base64EncodeChars[c1 >>2]);
                sb.append(base64EncodeChars[((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4)]);

                sb.append(base64EncodeChars[((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6)]);
                sb.append(base64EncodeChars[c3 & 0x3F]);
        }
        return sb.toString();
	}
	/**
	 * 解码
	 * @param text
	 * @return
	 */
	public static String Base64Decode(String text){
		int c1;
        int c2;
        int c3;
        int c4;
        int i=0;
        int len=text.length();

        int [] base64DecodeChars=DecodeChars;
        StringBuffer sb=new StringBuffer();
        char[] c = text.toCharArray();
         
        while (i < len)
        {
            do
            {
                c1 = base64DecodeChars[((int)c[i++]) & 0xff];
            } while (i < len && c1 == -1);
            if (c1 == -1)
                break;
            /* c2 */
            do
            {
                c2 = base64DecodeChars[((int)c[i++]) & 0xff];
            } while (i < len && c2 == -1);
            if (c2 == -1)
                break;
            sb.append((char)((c1 << 2) | ((c2 & 0x30) >> 4)));
            /* c3 */
            do
            {
                c3 = ((int)c[i++]) & 0xff;
                if (c3 == 61)
                    return sb.toString();

                c3 = base64DecodeChars[c3];
            } while (i < len && c3 == -1);
            if (c3 == -1)
                break;
            sb.append((char)(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2)));
            /* c4 */
            do
            {
                c4 =((int)c[i++]) & 0xff;
                if (c4 == 61)
                    return sb.toString();
                c4 = base64DecodeChars[c4];
            } while (i < len && c4 == -1);
            if (c4 == -1)
                break;
            sb.append((char)(((c3 & 0x03) << 6) | c4));

        }
        return sb.toString();
	}
	/**
	 * 中文转成字符
	 * @param text
	 * @return
	 */
	public static String Utf16to8(String text){
		String out="";
		
		int len=text.length();
		char[] chars=text.toCharArray();
		int c;
		 
         for (int i = 0; i < len; i++) {
             c =(int)chars[i]; // str.charCodeAt(i);
             if ((c >= 0x0001) && (c <= 0x007F)) {
                 out += chars[i];
             } else if (c > 0x07FF) {
                 out +=(char)(0xE0 | ((c >> 12) & 0x0F));
                 out += (char)(0x80 | ((c >> 6) & 0x3F));
                 out += (char)(0x80 | ((c >> 0) & 0x3F));
             } else {
                 out +=(char)(0xC0 | ((c >> 6) & 0x1F));
                 out +=(char)(0x80 | ((c >> 0) & 0x3F));
             }
         }
		return out;
	}
	
	/**
	 * 字符转成中文
	 * @param text
	 * @return
	 */
	public static String Utf8to16(String text){
		String out="";
		int i=0;
		int len=text.length();
		char[] chars=text.toCharArray();
		int c;
		  
		int char2;
		int char3;
         //var char2, char3;
       
         while (i < len) {
             c = (int)chars[i++];
             switch (c >> 4) {
                 case 0:
                 case 1:
                 case 2:
                 case 3:
                 case 4:
                 case 5:
                 case 6:
                 case 7:
                     // 0xxxxxxx
                     out += chars[i - 1];
                     break;
                 case 12:
                 case 13:
                     // 110x xxxx 10xx xxxx
                     char2 = (int)chars[i++];
                     out += (char)(((c & 0x1F) << 6) | (char2 & 0x3F));
                     break;
                 case 14:
                     // 1110 xxxx 10xx xxxx 10xx xxxx
                     char2 =  (int)chars[i++];
                     char3 =  (int)chars[i++];
                     out += (char)(((c & 0x0F) << 12)
                             | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
                     break;
             }
         }
         return out;
	}
	
	/**
	 * 编码
	 * @param text
	 * @return
	 */
	public static String Encode(String text){
		return Base64Encode(Utf16to8(text));
	}
	
	/**
	 * 解码
	 * @param text
	 * @return
	 */
	public static String Decode(String text){
		return Utf8to16(Base64Decode(text));
	}
	
}