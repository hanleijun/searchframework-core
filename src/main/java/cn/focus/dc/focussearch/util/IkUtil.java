package cn.focus.dc.focussearch.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
/**
 * ik分词
 * @author xuemingtang
 *
 */
public class IkUtil {

	private static volatile Configuration ikConfig;
	
	static void initIk(){
		ikConfig = DefaultConfig.getInstance();
		ikConfig.setUseSmart(false);
	}
	
	public static HashSet<String> getWordsSet(String keywords) {
		HashSet<String> pwSet = null;
		String key = null;
		if (keywords != null && keywords.trim().length() > 0) {
			pwSet = new HashSet<String>();
			if(null == ikConfig){
				initIk();
			}
			IKSegmenter ikSeg = new IKSegmenter(new StringReader(keywords.trim()), ikConfig);
			Lexeme l = null;
			try {
				while ((l = ikSeg.next()) != null) {
					key = l.getLexemeText().trim();
					if (key.length()>1 && !key.matches("\\d*")) {
						pwSet.add(key);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return pwSet;
	}
}
