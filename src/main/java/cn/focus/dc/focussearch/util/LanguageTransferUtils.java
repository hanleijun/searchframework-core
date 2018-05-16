package cn.focus.dc.focussearch.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Legend Han(leijunhan@sohu-inc.com)
 *
 */
public class LanguageTransferUtils {
	/**
	 * get IK Segment first, then transfer to pinyin
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String transferToPinYin(String param) throws Exception {
		List<String> terms = IkAnalyzerUtil.ikAnalyzerForList(param, false);
		if (terms == null || terms.isEmpty()) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String s : terms) {
			sb.append(null != s ? TransferToPinYin.getPinYin(s) : "").append(" ");
		}
		return sb.toString();
	}
	
	public static String transferToJianPin(String param) throws Exception {
		List<String> terms = IkAnalyzerUtil.ikAnalyzerForList(param, false);
		if (terms == null || terms.isEmpty()) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String s : terms) {
			sb.append(null != s ? TransferToPinYin.getJianPin(s) : "").append(" ");
		}
		return sb.toString();
	}
	
	/**
	 * transfer item to pinyin sequence directly
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public static String trans2PinYinSeq(String item) throws Exception{
		if(!StringUtils.isEmpty(item)){
			String re = TransferToPinYin.getPinYin(item);
			return re;
		}
		return null;
	}
	
	public static String trans2JianPinSeq(String item) throws Exception{
		if(!StringUtils.isEmpty(item)){
			String re = TransferToPinYin.getJianPin(item);
			return re;
		}
		return null;
	}
}
