package com.fro.handdevice.utils;

import android.util.Log;

import com.comm.ModelControl;
import com.device.serialport.SerialPort;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HelpUtil {
	/**
	 * 从硬件中读取的串口列表
	 * */
	public static List<String> serportlt = new ArrayList<String>();

	/**
	 * 串口初始化类
	 * */
	public static SerialPort sp;

	public static ModelControl mc = new ModelControl();

	/**
	 * 解析标签
	 * */
	public static final String getTagByCons(String data) {
		try {
			String[] tmp = data.split(" ");
			String tag = "";
			for (int i = 2; i < tmp.length; i++) {
				if (i < tmp.length - 9) {
					tag += tmp[i];
				}
			}
			return tag;
		} catch (Exception e) {
			if (e != null) {
				Log.e("解析标签", e.getMessage());
			}
			return null;
		}
	}

	/**
	 * 解析单卡模式下的卡
	 * */
	public static final String getTagByRead(String data) {
		try {
			String[] tmp = data.split(" ");
			String tag = "";
			for (int i = 1; i < tmp.length; i++) {
				if (i < tmp.length) {
					tag += tmp[i];
				}
			}
			return tag;
		} catch (Exception e) {
			if (e != null) {
				Log.e("解析标签", e.getMessage());
			}
			return null;
		}
	}

	public static String hexString = "0123456789ABCDEF";

	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		for (int i = 0; i < bytes.length(); i += 2) {
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		}
		return new String(baos.toByteArray());
	}

	public static String toStringHex2(String s) {
		byte[] bakeyword = new byte[s.length() / 2];
		for (int i = 0; i < bakeyword.length; i++) {
			try {
				bakeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
				s = new String(bakeyword, "utf-8");
			} catch (Exception e) {
			}
		}
		return s;
	}

	public static void printHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF) + " ";
			if (hex.length() == 2) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase());
		}
		System.out.println("\t\n");
	}

	/**
	 * 字符串转16进制字节数组  
	 * "0a0b"->{0x0a,0x0b}
	 * @param s
	 * @return
	 */
	public static byte[] toByteArray(String s) {
		byte[] buf = new byte[s.length() / 2];
		int j = 0;
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) ((Character.digit(s.charAt(j++), 16) << 4) | Character
					.digit(s.charAt(j++), 16));
		}
		return buf;
	}

	/**
	 * 字节数组转为16进制字符串
	 * {0x0a,0x0b}->"0a0b"
	 * @param b
	 * @param size
	 * @return
	 */
	public static String byte2hex(byte[] b, int size) {
		StringBuffer hs = new StringBuffer(size);
		String stmp = "";
		int len = size;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			String rstr = "";
			hs = hs.append(stmp);

			// if (stmp.length() == 1) {
			// hs = hs.append("0").append(stmp + "-");
			// } else {
			// hs = hs.append(stmp + "-");
			// }
		}
		return String.valueOf(hs);
	}

	/**
	 * 字节数组转为16进制字符串
	 * {0x0a,0x0b}->"0a 0b"
	 * @param b
	 * @param size
	 * @return
	 */
	public static String byte2hexno(byte[] b, int size) {

		StringBuffer hs = new StringBuffer(size);
		String stmp = "";
		for (int n = 0; n < size; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			// stmp = Short.toString(b[n]);
			String rstr = "";
			if (stmp.length() == 1) {
				hs = hs.append("0").append(stmp + " ");
			} else {
				hs = hs.append(stmp + " ");
			}
		}
		return String.valueOf(hs);
	}

	public static String byte2hex2serial(byte[] b, int size) {
		String stmp = "";
		int len = size;
		String ser[] = new String[10];
		String msg = "";
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1) {
				if (!stmp.equals(null)) {
					ser[n] = "0" + stmp;
				}
			} else {
				if (!stmp.equals(null)) {
					ser[n] = stmp;
				}
			}
		}
		msg = ser[8] + ser[7] + ser[6] + ser[5];
		return String.valueOf(msg);
	}

	/*
	 * 编码
	 */
	public static String encode(String str) {
		// 转换为字节数组
		byte[] b = str.getBytes();
		StringBuilder sb = new StringBuilder(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF) + " ";
			if (stmp.length() == 1) {
				sb = sb.append("0").append(stmp);
			} else if (stmp.length() == 2) {
				stmp = '0' + stmp;
			} else {
				sb = sb.append(stmp);
			}
		}
		return sb.toString();
	}

	/**
	 * 16进制数组转十进制
	 * @param packet
	 * @param start
	 * @param length
	 * @return
	 */
	public static int lBytesToInt(byte[] packet, int start, int length) {
		byte[] b = new byte[length];
		System.arraycopy(packet, start, b, 0, length);
		int le = length - 1;
		int s = 0;
		for (int i = 0; i < le; i++) {
			if (b[le - i] >= 0) {
				s = s + b[le - i];
			} else {
				s = s + 256 + b[le - i];
			}
			s = s * 256;
		}
		if (b[0] >= 0) {
			s = s + b[0];
		} else {
			s = s + 256 + b[0];
		}
		return s;
	}

	/**
	 * 10进制转16进制字节数组
	 * @param n
	 * @return
	 */
	public static byte[] toHH(int n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * Converts a byte array to hex string.
	 * 
	 * @param b
	 *            - the input byte array
	 * @return hex string representation of b.
	 */

	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(hexString.charAt(b[i] >>> 4 & 0x0F));
			sb.append(hexString.charAt(b[i] & 0x0F));
		}
		return sb.toString();
	}

	// 是不是正整数
	public static boolean isInt(String str) {
		if (str.matches("[1-9]\\d*"))
			return true;
		return false;
	}

	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];
		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}

	public static int byte2int(byte[] res) {
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
		int targets = ((res[1]) << 8 & 0xff00) | ((res[0]) & 0xff);
		return targets;
	}

	/** ascii to hex **/
	public static String convertStringToHex(String str) {

		char[] chars = str.toCharArray();

		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString(chars[i]));
		}

		return hex.toString();
	}

	/** 16 hex to ascii **/
	public static String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		// 49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}
}
