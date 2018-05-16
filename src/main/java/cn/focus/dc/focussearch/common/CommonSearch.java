package cn.focus.dc.focussearch.common;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchResultMapper;

import java.util.List;
import java.util.Map;

public interface CommonSearch<T,Q extends PageBaseQuery> {
	
	/**
	 * 搜索
	 * @param q
	 * @param indices
	 * @param types
	 * @param tClass
	 * @return
	 */
	public Page<T> search(Q q,String[] indices,String[] types,Class<T> tClass);
	
	/**
	 * 获取Filter
	 * @param q
	 * @return
	 */
	public QueryBuilder getFilter(Q q);
	
	/**
	 * 获取Queryer
	 * @param q
	 * @return
	 */
	public QueryBuilder getQuery(Q q);
	
	/**
	 * 获取排序
	 * @param q
	 * @return
	 */
	public List<SortBuilder> getSorts(Q q);
	
	/**
	 * 设置高亮结果
	 * @return
	 */
	public SearchResultMapper setHighLightResultMapper(final String... hlFields);
	
	/**
	 * 简单分组
	 * @param indices
	 * @param types
	 * @param field
	 * @return
	 */
	public Map<String,Long> termsAgg(String[] indices,String[] types,String field);
	
	
}
