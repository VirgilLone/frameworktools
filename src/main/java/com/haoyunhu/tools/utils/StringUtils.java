package com.haoyunhu.tools.utils;

import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class StringUtils extends org.apache.commons.lang3.StringUtils{

	/**
	 * 含有下划线转为驼峰字段
	 * @param column
	 * @return
	 */
	public static String columnToProperty(String column) {
		StringBuilder result = new StringBuilder();
		// 快速检查
		if (column == null || column.isEmpty()) {
			// 没必要转换
			return "";
		} else if (!column.contains("_")) {
			// 不含下划线，仅将首字母小写
			return column.substring(0, 1).toLowerCase() + column.substring(1);
		} else {
			// 用下划线将原始字符串分割
			String[] columns = column.split("_");
			for (String columnSplit : columns) {
				// 跳过原始字符串中开头、结尾的下换线或双重下划线
				if (columnSplit.isEmpty()) {
					continue;
				}
				// 处理真正的驼峰片段
				if (result.length() == 0) {
					// 第一个驼峰片段，全部字母都小写
					result.append(columnSplit.toLowerCase());
				} else {
					// 其他的驼峰片段，首字母大写
					result.append(columnSplit.substring(0, 1).toUpperCase()).append(columnSplit.substring(1).toLowerCase());
				}
			}
			return result.toString();
		}
	}

	/**
	 * 字符串驼峰转为下划线字符串
	 * @param property
	 * @return
	 */
	public static String propertyToColumn(String property){
		if (property == null || property.isEmpty()){
			return "";
		}
		StringBuilder column = new StringBuilder();
		column.append(property.substring(0,1).toLowerCase());
		for (int i = 1; i < property.length(); i++) {
			String s = property.substring(i, i + 1);
			// 在小写字母前添加下划线
			if(Character.isAlphabetic(s.charAt(0)) && s.equals(s.toUpperCase())){
				column.append("_");
			}
			// 其他字符直接转成小写
			column.append(s.toLowerCase());
		}

		return column.toString();
	}

	/**
	 * 转换第一个字母为大写
	 *
	 * @param str
	 * @return
	 */
	public static String toFirstUpperCase(String str) {
		if (isEmpty(str)) {
			return str;
		}

		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	public static String toFirstLowerCase(String str) {
		if (isEmpty(str)) {
			return str;
		}

		char[] chars = str.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * 组织成SQL中In的参数
	 * @param list
	 * @return
	 */
	public static String buildInSQLParams(List list){
		if(CollectionUtils.isEmpty(list)){
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for(Object o : list){
			if(o instanceof String){
				sb.append(",\'").append(o).append("\'");
			}else{
				sb.append(",").append(o);
			}
		}

		return sb.toString().replaceFirst(",","");
	}

	public static String defaultString(String value, String defaultValue) {
		if(StringUtils.isBlank(value)){
			return defaultValue;
		}
		return value;
	}

	/**
	 * 获取MD5加密后字符串
	 *
	 * @param
	 * @return
	 * @date ：2013-12-2
	 * @memo ：
	 */
	public static String getMD5Str(String original) {
		MessageDigest md5;
		String encodeStr = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			BASE64Encoder base64en = new BASE64Encoder();
			encodeStr = base64en.encode(md5.digest(original.getBytes("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encodeStr;
	}

	/**
	 * ****************************************************************************** @function : 将对象转化为String类型
	 *
	 * @param obj
	 * @return String
	 * ****************************************************************************** @creator ：majun
	 * @date ：2013-12-13
	 * @memo ：如果对象为空，则返回null
	 */
	public static String valueOf(Object obj) {
		return obj == null ? null : String.valueOf(obj);
	}

	/**
	 * 将字符串使用","进行拼接
	 *
	 * @param
	 * @return
	 * @date ：2013-12-27
	 * @memo ：
	 */
	public static String toAppend(List<String> list) {
		StringBuffer sb = new StringBuffer();
		if (null != list) {
			for (String obj : list) {
				if (null != obj && !"".equals(obj)) {
					sb.append(obj).append(",");
				}
			}
			return sb.deleteCharAt(sb.length() - 1).toString();
		}
		return null;
	}

	/**
	 * 将String串转化成List
	 *
	 * @param
	 * @return
	 * @date ：2013-12-27
	 * @memo ：
	 */
	public static List<String> toAddList(String obj) {
		if (null != obj && !"".equals(obj)) {
			List<String> list = new ArrayList<String>();
			String[] ob = obj.split(",");
			for (String o : ob) {
				list.add(o);
			}
			return list;
		}
		return null;
	}

	/**
	 * ****************************************************************************** @function : 判断字符串是否为空
	 *
	 * @param str
	 * @return ****************************************************************************** @creator ：majun
	 * @date ：2014-2-7
	 * @memo ：字符串为null或者为空字符串都返回true
	 */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str.trim())) {
			return false;
		}
		return true;
	}

	public static String toString(Object object, boolean includeSuperClassFields) {
		StringBuffer buffer = new StringBuffer();
		if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			buffer.append("[");
			int i = 0;
			for (Object obj : collection) {
				buffer.append(toString(obj, includeSuperClassFields));
				if (i < collection.size() - 1) {
					buffer.append(", ");
				}
				i++;
			}
			buffer.append("]");
		} else {
			if (object == null) {
				buffer.append(object);
				return buffer.toString();
			}
			if (String.class.equals(object.getClass()) || Long.class.equals(object.getClass())
					|| BigDecimal.class.equals(object.getClass()) || Integer.class.equals(object.getClass())
					|| Double.class.equals(object.getClass()) || double.class.equals(object.getClass())
					|| Date.class.equals(object.getClass())) {
				buffer.append(String.valueOf(object));
				return buffer.toString();
			}
			if (Date.class.equals(object.getClass())) {
				buffer.append(DateUtils.toYMDString((Date) object));
				return buffer.toString();
			}
			buffer.append(object.getClass().getSimpleName());
			buffer.append("(");
			List<Field> fields = new ArrayList<Field>();
			if (includeSuperClassFields) {
				for (Field field : object.getClass().getSuperclass().getDeclaredFields()) {
					fields.add(field);
				}
			}
			for (Field field : object.getClass().getDeclaredFields()) {
				fields.add(field);
			}
			for (int i = 0; i < fields.size(); i++) {
				try {
					Field field = fields.get(i);
					field.setAccessible(true);
					buffer.append(field.getName() + "=");
					Object value = field.get(object);
					buffer.append(toString(value, includeSuperClassFields));
					if (i < fields.size() - 1) {
						buffer.append(", ");
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			buffer.append(")");
		}
		return buffer.toString();
	}
}
