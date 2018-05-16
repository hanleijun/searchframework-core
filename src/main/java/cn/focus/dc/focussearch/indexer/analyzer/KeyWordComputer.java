package cn.focus.dc.focussearch.indexer.analyzer;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * 
 * @author xuemingtang
 *
 */
@Service
public class KeyWordComputer {

	private static final Map<String, Double> POS_SCORE = new HashMap<String, Double>();

	
	//词性标注
	static {
		POS_SCORE.put("null", 0.0);
		POS_SCORE.put("w", 0.0);
		POS_SCORE.put("en", 0.0);
		POS_SCORE.put("num", 0.0);
		POS_SCORE.put("nr", 0.0);
		POS_SCORE.put("nrf", 0.0);
		POS_SCORE.put("n", 3.0);
		POS_SCORE.put("nw", 0.0);
		POS_SCORE.put("nt", 3.0);
		POS_SCORE.put("a", 0.0);
		POS_SCORE.put("nz", 3.0);
		POS_SCORE.put("v", 0.0);
	}

	private int nKeyword = 10;

	public KeyWordComputer() {
	}

	/**
	 * 返回关键词个数
	 * 
	 * @param nKeyword
	 */
	public KeyWordComputer(int nKeyword) {
		this.nKeyword = nKeyword;

	}

	/**
	 * 
	 * @param content
	 *            正文
	 * @return
	 */
	private List<Keyword> computeArticleTfidf(String content, int titleLength) {
		Map<String, Keyword> tm = new HashMap<String, Keyword>();

//		MyStaticValue.userLibrary="dict";
		List<Term> parse = ToAnalysis.parse(content);
//		System.out.println("parse----------------------------"+parse);
		for (Term term : parse) {
			
			String termString = term.getName().trim();
			termString = termString.replaceAll("\\s*", "");//把空白符替换掉
			if(termString.length() < 2){
				continue;
			}
			double weight = getWeight(term, content.length(), titleLength);
			if (weight == 0)
				continue;
		
			Keyword keyword = tm.get(term.getName());
			if (keyword == null) {
				keyword = new Keyword(term.getName(), term.natrue().allFrequency, weight);
				tm.put(term.getName(), keyword);
			} else {
				keyword.updateWeight(1);
			}
		}

		TreeSet<Keyword> treeSet = new TreeSet<Keyword>(tm.values());

		ArrayList<Keyword> arrayList = new ArrayList<Keyword>(treeSet);
		if (treeSet.size() <= nKeyword) {
			return arrayList;
		} else {
			return arrayList.subList(0, nKeyword);
		}

	}

	/**
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            正文
	 * @return
	 */
	public List<Keyword> computeArticleTfidf(String title, String content) {
		if (StringUtils.isBlank(title)) {
			title = "";
		}
		if (StringUtils.isBlank(content)) {
			content = "";
		}
		return computeArticleTfidf(title + "\t" + content, title.length());
	}

	/**
	 * 只有正文
	 * 
	 * @param content
	 * @return
	 */
	public List<Keyword> computeArticleTfidf(String content) {
		return computeArticleTfidf(content, 0);
	}

	private double getWeight(Term term, int length, int titleLength) {
		if (term.getName().trim().length() < 2) {
			return 0;
		}

		String pos = term.natrue().natureStr;

		Double posScore = POS_SCORE.get(pos);

		if (posScore == null) {
			posScore = 1.0;
		} else if (posScore == 0) {
			return 0;
		}

		if (titleLength > term.getOffe()) {
			return 5 * posScore;
		}
		return (length - term.getOffe()) * posScore / (double) length;
	}

}
