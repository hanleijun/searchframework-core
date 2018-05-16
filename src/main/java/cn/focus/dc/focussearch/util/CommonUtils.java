package cn.focus.dc.focussearch.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class CommonUtils {
	
	private static final double LIKE_NR =0.65;
	
	private static final double LIKE_NS = 0.85;
	
	private static int NEWS_RELATED = 1;

    /**
     * 过滤特殊字符
     *
     * @param str
     * @return
     */
    public static String filterSpecialChars(String str) {
    	if(StringUtils.isNotBlank(str)){
    		String regEx = "[`~!@#$%^&*()+=|{}':;,/[/].<>?！￥\\[\\]…（）——+|{}【】‘；：”“’。，、？]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
    	}
        return null;
    }
    
    
   	public static boolean isLike(String title, String nextTitle,int type) {
   		double likeFactor = LIKE_NS;
   		LCS lcs = new LCS(title, nextTitle);
   		// 公共序列长度
   		double termLength = lcs.LCS_length();
   		// title长度
   		double titleLength = title.length();
   		// 下一个标题长度
   		double nextTitleLength = nextTitle.length();
   		double titleD = termLength / titleLength;
   		double nextTitleD = termLength / nextTitleLength;
   		
   		if(type == NEWS_RELATED){
   			likeFactor = LIKE_NR;
   		}
   		if (titleD > likeFactor && nextTitleD > likeFactor) {
   			return true;
   		}
   		return false;
   	}
   	public static void main(String[] args) {
		String s = "[df|";
		System.out.println(filterSpecialChars(s));
	}
}
