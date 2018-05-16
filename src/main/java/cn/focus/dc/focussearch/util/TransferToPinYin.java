package cn.focus.dc.focussearch.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class TransferToPinYin {

    /**
     * 获取中文的汉语全拼
     *
     * @param name
     * @return
     * @throws Exception
     */
    public static String getPinYin(String name) throws Exception {
        // 用于存放<strong>转换</strong>后的<strong>拼音</strong>字符串
        String pinyin = "";
        // 将<strong>中文</strong>字符串分成<strong>中文</strong>字符数组（如“中国” <strong>转换</strong>成 ‘中’ ‘国’）
        char[] chars = name.toCharArray();
        String[] tmppinyin;
        for (int i = 0; i < chars.length; i++) {
            // 当<strong>转换</strong>的不是<strong>中文</strong>字符时，返回null
            tmppinyin = PinyinHelper.toHanyuPinyinStringArray(chars[i], getDefaultOutputFormat());
            if (tmppinyin != null) {
                pinyin += tmppinyin[0];
            } else {
                pinyin += chars[i];
            }
        }
        return pinyin;
    }

    /**
     * 获取中文的汉语全拼
     *
     * @param name
     * @return
     * @throws Exception
     */
    public static String getJianPin(String name) throws Exception {
        String pinyin = "";
        char[] chars = name.toCharArray();
        String[] tmppinyin;
        for (int i = 0; i < chars.length; i++) {
            // 当<strong>转换</strong>的不是<strong>中文</strong>字符时，返回null
            tmppinyin = PinyinHelper.toHanyuPinyinStringArray(chars[i], getDefaultOutputFormat());
            if (tmppinyin != null) {
                pinyin += tmppinyin[0].charAt(0);
            } else {
                pinyin += chars[i];
            }
        }
        return pinyin;
    }

    private static HanyuPinyinOutputFormat getDefaultOutputFormat() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 没有音调数字
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        return format;
    }
}
