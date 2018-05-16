package cn.focus.dc.focussearch.common;

import org.elasticsearch.action.search.SearchType;

/**
 * 分页信息逻辑的封装
 */
public class PageBaseQuery extends BaseQuery{
	
	/**
	 * 修改为默认页码是从0开始的
	 */
	public static final int DEFAULT_CURPAGE = 0;
	
	public static final int DEFAULT_SIZE = 10;
	
	private int page = DEFAULT_CURPAGE;
	
	private int maxPage = DEFAULT_CURPAGE;
	
	private int pageSize = DEFAULT_SIZE;
	
	private int totalItem;
	
	/**
	 * es搜索方式类别
	 */
	private SearchType searchType = SearchType.QUERY_THEN_FETCH;

	/**
     * 进行聚合统计的字段名称
     */
    private String aggFieldName;
    
    
    public String getAggFieldName() {
		return aggFieldName;
	}

	public void setAggFieldName(String aggFieldName) {
		this.aggFieldName = aggFieldName;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	/**
     * 限制业务端传入的值过大，会引起ES集群服务异常，
     * @param page
     */
	public void setPage(int page) {
		this.page = (page>999999) ? 0 : page;
	}

    /**
     * 限制pageSize 值，最多100条；
     * @param pageSize
     */
	public void setPageSize(int pageSize) {
		this.pageSize = (pageSize>100) ? 100 : pageSize;
	}

	/**
	 * 获得最大的页码
	 * @return
	 */
	public int getMaxPage() {
		return maxPage;
	}
	
	/**
	 * 获取共查询到多少结果
	 * @return
	 */
	public int getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(int totalItem) {
		this.totalItem = totalItem;
		//根据结果总数计算出最大的页面
		this.maxPage = totalItem % pageSize == 0 ? (totalItem / pageSize) : totalItem / pageSize  + 1;
	}
	
	

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 获得当前页的起始查询的记录位置
	 * @return
	 */
	public int getOffset() {
		return page == 1 ? 0 : (page - 1) * pageSize;
	}

}
