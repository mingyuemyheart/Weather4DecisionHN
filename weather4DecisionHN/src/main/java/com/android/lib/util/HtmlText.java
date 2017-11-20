package com.android.lib.util;

import java.util.regex.Pattern;


/**
 *     
 * 类描述：去除Html标签工具类
 * <p>
 * 创建人：Lynn
 * <p>
 * 创建时间：2013-3-18 下午5:48:49  
 * <p>     
 * 修改备注：    
 * <p>
 * @version 1.0
 * @since 1.0
 */
public final class HtmlText {
	
	private HtmlText() {
	}
	
	/**
	 * 处理html中的特殊字符
	 * 
	 * @param source 源串
	 * 
	 * @return 处理后的结果字符串
	 */
	public static String htmlEscape(String source) {
		String escape = source;
		if (escape != null) {
			escape = escape.replaceAll("&mdash;", "—");
			escape = escape.replaceAll("&lt;", "<");
			escape = escape.replaceAll("&gt;", ">");
			escape = escape.replaceAll("&amp;", "&");
			escape = escape.replaceAll("&apos;", "'");
			escape = escape.replaceAll("&quot;", "\"");
			escape = escape.replaceAll("&rdquo;", "”");
			escape = escape.replaceAll("&ldquo;", "“");
		}
		return escape;
	}
	
	/**
	 * 去除html字符串中的特殊字符
	 * 
	 * @param html 带html特殊字符的源串
	 * 
	 * @return 处理后的结果字符串
	 */
	public static String html2Text2(String html) {
		StringBuffer buf = new StringBuffer(html);
		int i = buf.length();
		int startF = -1;
		int endF = -1;
		for (int j = 0; j < i; j++) {
			if (buf.charAt(j) == '<' && startF == -1) {
				startF = j;
			}
			
			if (buf.charAt(j) == '>' && endF == -1 && startF > -1) {
				endF = j;
			}
			
			if (startF > -1 && endF > -1) {
				
				buf = buf.replace(startF, endF + 1, "");
				i -= (endF - startF) + 1;
				j -= (endF - startF) + 1;
				startF = -1;
				endF = -1;
			}
			
		}
		
		return buf.toString();
	}
	
	/**
	 * 去除html标签
	 * 
	 * @param html 带有html标签的源字符串
	 * 
	 * @return 不含html标签的字符串
	 */
	public static String html2Text(String html) {
		String htmlStr = html;
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		
		java.util.regex.Pattern p_html1;
		java.util.regex.Matcher m_html1;
		
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }    
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }    
			String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式    
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); //过滤script标签    
			
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); //过滤style标签    
			
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); //过滤html标签    
			
			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); //过滤html标签    
			
			textStr = htmlStr;
			
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;
	}
}
