package cn.focus.dc.focussearch.common;

public class SearchConstants {

    public static final Integer QUERY_LENGTH = 30;

    /**
     * 相关性排序
     */
    public static final int SORT_BY_RELEVENCE = 0;
    /**
     * 按时间排序
     */
    public static final int SORT_BY_TIME = 1;
    /**
     * 一天内
     */
    public static final int ONE_DAY = 1;
    /**
     * 一周内
     */
    public static final int ONE_WEEK = 2;
    /**
     * 一月内
     */
    public static final int ONE_MONTH = 3;
    /**
     * 一年内
     */
    public static final int ONE_YEAR = 4;
    /**
     * 半年
     */
    public static final int HALF_YEAR = 5;
    /**
     * 时间不限
     */
    public static final int NO_LIMIT = 6;
    /**
     * 一天的毫秒数
     */
    public static final long onedayMillis = 24 * 60 * 60 * 1000;
    /**
     * 聚合桶大小，保证聚合数据精确度
     */
    public static final int BUCKET_SIZE = 100;
    
    public static final String NEWS_CITY_KEY = "news_search_city";

    public static final String FORUM_URL_PREFIX="http://house.focus.cn/msglist/";
}
