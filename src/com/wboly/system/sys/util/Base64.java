package com.wboly.system.sys.util;

/**
 * 
 * JAVA 版的BASE64加密
 * 
 * 标准的Base64加密后的字符串长度可以被4整出,比如:
 * 字符		加密后
 * a		YQ==
 * aa		YWE=
 * aaa		YWFh
 * aaab		YWFhYg==
 * aaabb	YWFhYmI=
 * aaabbb	YWFhYmJi
 * 
 * 一旦加密后的字符能被4整除，就用"="来补充
 * 
 * 本例是在网上找的，原本是标准的，遵照RFC2045～RFC2049规范。
 * 
 * PHP版的BASE64可以解密标准的BASE64密文，也可以解密不带后边"="的密文，为了能解密PHP的BASE64和AuthCode加密。
 * （PHP的AuthCode中也用到了BASE64）我将本类做了修改，使之也可以解密不带"="的BASE64密文。
 * 原理很简单，将密文对4取模（"密文"%4），看差即为就补几个"="，然后就得到一个标准的BASE64密文并解之。
 * 
 * @author by yue
 * @date 2012 11 13
 * 
 * 
 */

import java.io.UnsupportedEncodingException;
  
public class Base64 {

	private static char[] base64EncodeChars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
			5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26,
			27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1,
			-1, -1, -1 };

	/**
	 * 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String encode(byte[] data) {
		StringBuffer sb = new StringBuffer();
		int len = data.length;
		int i = 0;
		int b1, b2, b3;
		while (i < len) {
			b1 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
				sb.append("==");
				break;
			}
			b2 = data[i++] & 0xff;
			if (i == len) {
				sb.append(base64EncodeChars[b1 >>> 2]);
				sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
				sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
				sb.append("=");
				break;
			}
			b3 = data[i++] & 0xff;
			sb.append(base64EncodeChars[b1 >>> 2]);
			sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
			sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
			sb.append(base64EncodeChars[b3 & 0x3f]);
		}
		return sb.toString();
	}

	private static byte[] codes = new byte[256];

	static {
		for (int i = 0; i < 256; i++)
			codes[i] = -1;
		for (int i = 'A'; i <= 'Z'; i++)
			codes[i] = (byte) (i - 'A');
		for (int i = 'a'; i <= 'z'; i++)
			codes[i] = (byte) (26 + i - 'a');
		for (int i = '0'; i <= '9'; i++)
			codes[i] = (byte) (52 + i - '0');
		codes['+'] = 62;
		codes['/'] = 63;
	}

	public static byte[] decode(char[] data) {
		// as our input could contain non-BASE64 data (newlines,
		// whitespace of any sort, whatever) we must first adjust
		// our count of USABLE data so that...
		// (a) we don't misallocate the output array, and
		// (b) think that we miscalculated our data length
		// just because of extraneous throw-away junk

		int tempLen = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || codes[data[ix]] < 0)
				--tempLen; // ignore non-valid chars and padding
		}
		// calculate required length:
		// -- 3 bytes for every 4 valid base64 chars
		// -- plus 2 bytes if there are 3 extra base64 chars,
		// or plus 1 byte if there are 2 extra.

		int len = (tempLen / 4) * 3;
		if ((tempLen % 4) == 3)
			len += 2;
		if ((tempLen % 4) == 2)
			len += 1;

		byte[] out = new byte[len];

		int shift = 0; // # of excess bits stored in accum
		int accum = 0; // excess bits
		int index = 0;

		// we now go through the entire array (NOT using the 'tempLen' value)
		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : codes[data[ix]];

			if (value >= 0)// skip over non-code
			{
				accum <<= 6; // bits shift up by 6 each time thru
				shift += 6; // loop, with new bits being put in
				accum |= value; // at the bottom.
				if (shift >= 8)// whenever there are 8 or more shifted in,
				{
					shift -= 8; // write them out (from the top, leaving any
					out[index++] = // excess at the bottom for next iteration.
							(byte) ((accum >> shift) & 0xff);
				}
			}
			// we will also have skipped processing a padding null byte ('=')
			// here;
			// these are used ONLY for padding to an even length and do not
			// legally
			// occur as encoded data. for this reason we can ignore the fact
			// that
			// no index++ operation occurs in that special case: the out[] array
			// is
			// initialized to all-zero bytes to start with and that works to our
			// advantage in this combination.
		}

		// if there is STILL something wrong we just have to throw up now!
		if (index != out.length) {
			throw new Error("Miscalculated data length (wrote " + index + " instead of " + out.length + ")");
		}

		return out;
	}

	/**
	 * 解密
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] decode(String str) throws UnsupportedEncodingException {
		int remainder = str.length() % 4;// 对4取模后的余数

		if (remainder == 2) {
			str = str + "==";
		} else if (remainder == 3) {
			str = str + "=";
		}

		StringBuffer sb = new StringBuffer();
		byte[] data = str.getBytes("US-ASCII");
		int len = data.length;
		int i = 0;
		int b1, b2, b3, b4;
		while (i < len) {

			do {
				b1 = base64DecodeChars[data[i++]];
			} while (i < len && b1 == -1);
			if (b1 == -1)
				break;

			do {
				b2 = base64DecodeChars[data[i++]];
			} while (i < len && b2 == -1);
			if (b2 == -1)
				break;
			sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));

			do {
				b3 = data[i++];
				if (b3 == 61)
					return sb.toString().getBytes("iso8859-1");
				b3 = base64DecodeChars[b3];
			} while (i < len && b3 == -1);
			if (b3 == -1)
				break;
			sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));

			do {
				b4 = data[i++];
				if (b4 == 61)
					return sb.toString().getBytes("iso8859-1");
				b4 = base64DecodeChars[b4];
			} while (i < len && b4 == -1);
			if (b4 == -1)
				break;
			sb.append((char) (((b3 & 0x03) << 6) | b4));
		}
		return sb.toString().getBytes("iso8859-1");
	}

}
