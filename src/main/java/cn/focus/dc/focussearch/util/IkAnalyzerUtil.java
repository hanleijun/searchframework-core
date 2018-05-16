package cn.focus.dc.focussearch.util;

import org.apache.commons.lang.StringUtils;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author motongqiu
 */
public class IkAnalyzerUtil {
    /**
     * @param param 需要分词的源数据
     * @param flag true 中文智能消除歧义分词 false 否
     * @return
     * @throws Exception
     */
    public static String ikAnalyzerForString(String param, boolean flag) throws Exception {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        StringBuilder sb = null;
        List<String> list = ikAnalyzerForList(param, flag);
        if (flag) {
            sb = new StringBuilder(param.length() * 2);
        } else {
            sb = new StringBuilder(param.length() * 4);
        }
        for (String str : list) {
            sb.append(str).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * @param param 需要分词的源数据
     * @param flag true 中文智能消除歧义分词 false 否
     * @return
     * @throws Exception
     */
    public static List<String> ikAnalyzerForList(String param, boolean flag) throws Exception {
        List<String> list = null;
        if (StringUtils.isBlank(param)) {
            return list;
        }
        StringReader reader = new StringReader(param);
        IKSegmenter ikSegmenter = new IKSegmenter(reader, flag); // 中文智能消除歧义分词
        Lexeme lex = null;
        if (flag) {
            list = new ArrayList<String>(param.length());
        } else {
            list = new ArrayList<String>(param.length() * 2);
        }
        while ((lex = ikSegmenter.next()) != null) {
            list.add(lex.getLexemeText());
        }
        reader.close();
        if (!flag) {// 在最细粒度切分情况下，如果原始词和切分后的词一致，说明命中了扩展词库中的词，这时为了提高搜索召回率，将单字分词
            if (list.size() == 1 && list.get(0).equalsIgnoreCase(param) && param.length() > 1) {
                char[] array = param.toCharArray();
                for (int i = 0; i < array.length; i++) {
                    list.add(String.valueOf(array[i]));
                }
            }
        }

        return list;
    }
    public  static void  main(String[] args)throws Exception {
        String word = "金科西府";
        List<String> list = IkAnalyzerUtil.ikAnalyzerForList(word, true);
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println(IkAnalyzerUtil.ikAnalyzerForString(word,false));
    }
}
