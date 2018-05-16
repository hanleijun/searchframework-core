package cn.focus.dc.focussearch.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import cn.focus.dc.focussearch.enumeration.Number2Kanji;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * character processor
 * @author Legend Han(leijunhan@sohu-inc.com)
 */
public class LanguagePretreatment {
	
	private static Map<String, List<String>> pinyinMap = new HashMap<String, List<String>>();
	
	// 判断字符串是否是纯汉字
	public static boolean inputType(String s){
		// if the input is all Chinese return true,else return false;
		if (s == null || s.length() == 0)
			return false;
		s = s.replaceAll(" ", "");
		for (int i = 0; i < s.length(); i++) 
		{// 遍历字符串每一个字符
            // 使用正则表达式判断字符是否属于汉字编码
            boolean matches = Pattern.matches("^[\u4E00-\u9FA5]{0,}$", "" + s.charAt(i));
            if (!matches) 
            {
                return false;
            }
        }
		return true;
	}
	
	// 判断单个字符是否是汉字
	public static boolean isChinese(char ch){
        // 使用正则表达式判断字符是否属于汉字编码
        boolean matches = Pattern.matches("^[\u4E00-\u9FA5]{0,}$", "" + ch);
        if (!matches){
            return false;
        }
		return true;
	}
	
	// 将混合拼写字符串转成拼音, 其中将阿拉伯数字先转化成汉子再转化成拼音
	public static String mixedSeq2Latin(String s){
		//change the Chinese to English
		if (s == null || s.length() == 0)
			return null;
		// 枚举映射成数字汉字
		String rex = "\\d+";
		for(Number2Kanji e: Number2Kanji.values()){
			if(StringUtils.contains(s, String.valueOf(e.getId()))){
				s = StringUtils.replacePattern(s, rex, e.getName());
			}
		}
		s = s.replaceAll(" ", "");
		int len = s.length();
		StringBuffer pinyin = new StringBuffer();
		int i = 0;
		while (i < len){
			if (isChinese(s.charAt(i))){
				int start = i;
				while (i<len && isChinese(s.charAt(i))){
					i++;
				}
				int end = i;
				String str = s.substring(start, end);
				pinyin.append(convertToPinyin(str));
			}
			else{
				pinyin.append(s.charAt(i));
				i++;
			}	
		}
		return pinyin.toString().toLowerCase();
	}

	// 将纯汉字字符串转成拼音
	public static String convertToPinyin(String chinese){
		if (chinese ==  null || chinese.length() == 0)
			return null;
		initPinyin();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		
		char[] arr = chinese.toCharArray();
		StringBuffer pin = new StringBuffer();
		for (int i = 0; i < arr.length; i++){
			char ch = arr[i];
			try{
				String[] results = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);
				if (results != null) 
				{ // 中文
					int len = results.length;
					if (len == 1){ // 不是多音字
						pin.append(results[0]);
					} 
					else{ // 多音字
						// 合并同音不同声调（去重）
						List<String> duoyinziPinyins = new ArrayList<String>();// 定义一个空的数组
						for (int k = 0; k < len; k++){
							if (!duoyinziPinyins.contains(results[k])){
								duoyinziPinyins.add(results[k]);
							}
						}
						
						if(duoyinziPinyins.size()==1){
							pin.append(duoyinziPinyins.get(0));// 如果新的集合长度是1，就取第一个
						}
						else{ 
							int length = chinese.length();
							boolean flag = false;
							for (int x = 0; x < duoyinziPinyins.size(); x++){
								String py = duoyinziPinyins.get(x);
								if (i + 3 <= length) { // 后向匹配2个汉字 大西洋
									if (matchPinyins(py, chinese, i, i+3)){
										pin.append(py);
										flag = true;
										break;
									}
								}

								if (i + 2 <= length){ // 后向匹配 1个汉字 大西
									if(matchPinyins(py,chinese, i, i+2)){
										pin.append(py);
										flag = true;
										break;
									}
								}

								if ((i - 2 >= 0) && (i + 1 <= length)) { // 前向匹配2个汉字
									if(matchPinyins(py,chinese, i-2, i+1)){
										pin.append(py);
										flag = true;
										break;
									}
								}

								if ((i - 1 >= 0) && (i + 1 <= length)) { // 前向匹配1个汉字
																			// 固大
									if(matchPinyins(py,chinese, i-1, i+1)){
										pin.append(py);
										flag = true;
										break;
									}
								}

								if ((i - 1 >= 0) && (i + 2 <= length)){ // 前向1个，后向1个		// 固大西																			
									if(matchPinyins(py,chinese, i-1, i+2)){
										pin.append(py);
										flag = true;
										break;
									}
								}
							}

							if (!flag){ 
								// 如果都没有找到，也就是常用读音
//								System.out.println("default = " + duoyinziPinyins.get(0));
								pin.append(duoyinziPinyins.get(0));
							}
						}
					}
				}
			} catch (BadHanyuPinyinOutputFormatCombination e){
				e.printStackTrace();
			}
		}
		return pin.toString();
	}
	
	// 截取词组，并匹配拼音表中的词组
	private static boolean matchPinyins(String py,String chinese,int m,int n){
		String s = chinese.substring(m,n);
		List<String> cizu = pinyinMap.get(py);
		if (cizu!=null && cizu.contains(s)){
			return true;
		}
		return false;
	}
	
	/**
	 * 初始化 所有的多音字词组 即pinyinMap
	 */
	public static void initPinyin() {
		if (pinyinMap.size() >= 240)
			return;
		// 读取多音字的全部拼音表;
		InputStream file = LanguagePretreatment.class.getResourceAsStream("/duoyinzi_dic.txt");
		BufferedReader br = null;
		String s = null;
		try {
			br = new BufferedReader(new InputStreamReader(file,"UTF-8"));
			while ((s = br.readLine()) != null){
				if (s != null){
					String[] arr = s.split("#");
					String pinyin = arr[0];
					String chinese = arr[1];

					if (chinese != null){
						String[] strs = chinese.split(" ");
						//去空						
						List<String> list = arr2List(strs);
						pinyinMap.put(pinyin, list);
					}
				}
			}
		} 
		catch (IOException e){
			e.printStackTrace();
		} finally{
			try {
				br.close();
			} 
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	// 数组转换成集合，并且去掉空格
	private static List<String> arr2List(String[] strs) {
		if (strs!=null && strs.length>0){
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < strs.length; i++){
				if (!"".equals(strs[i].trim())){
					list.add(strs[i].trim());
				}
			}
			return list;
		}
		else{
			return null;
		}
	}
	
	// 多音字转成包含所有情况的拼音数组
	public static List<String> chineseToPinyin(String str){
		List<String> pinyin = new ArrayList<String>();
		List<String> temp = new ArrayList<String>();
		//change the Chinese to English
		if (str == null || str.length() == 0)
			return null;
		str = str.replaceAll(" ", "");
		int len = str.length();
		int i = 0;
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		while (i < len){
			char ch = str.charAt(i);
			try{
				String[] results = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat);
				System.out.println(results.length);
				if (pinyin.size() == 0){
					for (int j = 0; j < results.length; j++)
						pinyin.add(results[j]);
				}
				else{
					//System.out.println(pinyin.size());
					for (String py : pinyin){
						//pinyin.remove(pinyin.indexOf(py));
						for (int j = 0; j < results.length; j++){
							if (!temp.contains(py+results[j]))
								temp.add(py+results[j]);
						}
					}
					pinyin.clear();
					for (String s : temp){
						if (!pinyin.contains(s))
							pinyin.add(s);
					}
					temp.clear();
				}
			} catch (BadHanyuPinyinOutputFormatCombination e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		return pinyin;
	}
	
	public static void main(String[] args){
		String str = "重庆";
		/*String st = "   中 国农业银行 重庆分行 ";
		System.out.println(inputType(st));
		System.out.println(inputType(st.replaceAll(" ", "")));
		str = str.replaceAll(" ", "");*/
		//initPinyin();
		System.out.println(str + ": " + mixedSeq2Latin(str));
		/*str = "重庆朝阳称";
		List<String> pinyin = chineseToPinyin(str);
		for (String p : pinyin)
			System.out.println(p);*/
	}
}
