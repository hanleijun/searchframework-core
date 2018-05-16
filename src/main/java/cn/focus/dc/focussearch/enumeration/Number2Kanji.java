package cn.focus.dc.focussearch.enumeration;

/** 
 * 阿拉伯数字到汉子的枚举映射
 * @author Legend Han(leijunhan@sohu-inc.com) 
 * @date 2016-4-12 上午11:39:03 
 */
public enum Number2Kanji {
	ZERO("零", 0), ONE("一", 1), TWO("二", 2), THREE("三", 3), FOUR("四", 4), FIVE("五", 5), 
	SIX("六", 6), SEVEN("七", 7), EIGHT("八", 8), NINE("九", 9), TEN("十", 10),;
    // 成员变量  
    private String name;  
    private int id;  
    // 构造方法  
    private Number2Kanji(String name, int id) {  
        this.name = name;  
        this.id = id;  
    }  
    // 普通方法  
    public static String getName(int id) {  
        for (Number2Kanji c : Number2Kanji.values()) {  
            if (c.getId() == id) {  
                return c.name;  
            }  
        }  
        return null;  
    }  
    // get set 方法  
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public int getId() {  
        return id;  
    }  
    public void setId(int id) {  
        this.id = id;  
    }
}
